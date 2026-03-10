package com.inventory.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventory.dto.UserDTO;
import com.inventory.dto.UserRequest;
import com.inventory.entity.Role;
import com.inventory.entity.User;
import com.inventory.entity.UserGroup;
import com.inventory.exception.ForbiddenException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.RoleRepository;
import com.inventory.repository.UserGroupRepository;
import com.inventory.repository.UserRepository;
import com.inventory.security.AuthContext;
import com.inventory.security.PermissionChecker;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserGroupRepository userGroupRepository;
    private final PermissionChecker permissionChecker;
    private final AuditLogService auditLogService;

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers(AuthContext context) {
        // Chỉ ADMIN mới được xem tất cả users
        permissionChecker.requireAdmin(context);
        
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(UUID id, AuthContext context) {
        // User có thể xem thông tin của chính mình
        // ADMIN có thể xem tất cả
        if (!context.getUserId().equals(id)) {
            permissionChecker.requireAdmin(context);
        }
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
        
        return convertToDTO(user);
    }

    @Transactional
    public UserDTO createUser(UserRequest request, AuthContext context) {
        // Chỉ ADMIN mới được tạo user
        permissionChecker.requireAdmin(context);
        
        // Kiểm tra clerkUserId đã tồn tại chưa
        if (userRepository.findByClerkUserId(request.getClerkUserId()).isPresent()) {
            throw new ForbiddenException("Clerk User ID đã tồn tại");
        }
        
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy role"));
        
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setClerkUserId(request.getClerkUserId());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPhone(request.getPhone());
        user.setFullName(request.getFullName());
        user.setRole(role);
        user.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        user.setCreatedAt(LocalDateTime.now());
        user.setCreatedBy(context.getUserId());
        
        // Set user groups
        if (request.getGroupIds() != null && !request.getGroupIds().isEmpty()) {
            List<UserGroup> groups = userGroupRepository.findAllById(request.getGroupIds());
            user.setUserGroups(new HashSet<>(groups));
        }
        
        user = userRepository.save(user);
        
        auditLogService.logCreate(context.getUserId(), "User", user.getId().toString(), 
                "Tạo người dùng: " + user.getEmail());
        
        return convertToDTO(user);
    }

    @Transactional
    public UserDTO updateUser(UUID id, UserRequest request, AuthContext context) {
        // Chỉ ADMIN mới được update user
        permissionChecker.requireAdmin(context);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
        
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy role"));
        
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPhone(request.getPhone());
        user.setFullName(request.getFullName());
        user.setRole(role);
        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }
        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedBy(context.getUserId());
        
        // Update user groups
        if (request.getGroupIds() != null) {
            List<UserGroup> groups = userGroupRepository.findAllById(request.getGroupIds());
            user.setUserGroups(new HashSet<>(groups));
        }
        
        user = userRepository.save(user);
        
        auditLogService.logUpdate(context.getUserId(), "User", user.getId().toString(), 
                "Cập nhật người dùng: " + user.getEmail());
        
        return convertToDTO(user);
    }

    @Transactional
    public void deleteUser(UUID id, AuthContext context) {
        // Chỉ ADMIN mới được xóa user
        permissionChecker.requireAdmin(context);
        
        // Không được xóa chính mình
        if (context.getUserId().equals(id)) {
            throw new ForbiddenException("Không thể xóa chính mình");
        }
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
        
        // Soft delete
        user.setIsActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedBy(context.getUserId());
        userRepository.save(user);
        
        auditLogService.logDelete(context.getUserId(), "User", user.getId().toString(), 
                "Xóa người dùng: " + user.getEmail());
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setClerkUserId(user.getClerkUserId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setPhone(user.getPhone());
        dto.setRoleId(user.getRole().getId());
        dto.setRoleCode(user.getRole().getCode());
        dto.setRoleName(user.getRole().getName());
        
        if (user.getUserGroups() != null && !user.getUserGroups().isEmpty()) {
            dto.setGroupIds(user.getUserGroups().stream()
                    .map(UserGroup::getId)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
}
