package com.inventory.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventory.dto.CategoryDTO;
import com.inventory.entity.Category;
import com.inventory.exception.BadRequestException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.CategoryRepository;
import com.inventory.security.AuthContext;
import com.inventory.security.PermissionChecker;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final PermissionChecker permissionChecker;
    private final AuditLogService auditLogService;
    
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public CategoryDTO getCategoryById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return toDTO(category);
    }
    
    @Transactional
    public CategoryDTO createCategory(CategoryDTO dto, AuthContext context) {
        permissionChecker.requireManager(context);
        
        String slug = generateSlug(dto.getName());
        if (categoryRepository.existsBySlug(slug)) {
            throw new BadRequestException("Danh mục với tên này đã tồn tại");
        }
        
        Category category = new Category();
        category.setName(dto.getName());
        category.setSlug(slug);
        category.setDescription(dto.getDescription());
        
        category = categoryRepository.save(category);
        
        auditLogService.log("Category", category.getId().toString(), "CREATE", context.getUserId());
        
        return toDTO(category);
    }
    
    @Transactional
    public CategoryDTO updateCategory(Integer id, CategoryDTO dto, AuthContext context) {
        permissionChecker.requireManager(context);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        
        String slug = generateSlug(dto.getName());
        if (!category.getSlug().equals(slug) && categoryRepository.existsBySlug(slug)) {
            throw new BadRequestException("Danh mục với tên này đã tồn tại");
        }
        
        category.setName(dto.getName());
        category.setSlug(slug);
        category.setDescription(dto.getDescription());
        
        category = categoryRepository.save(category);
        
        auditLogService.log("Category", id.toString(), "UPDATE", context.getUserId());
        
        return toDTO(category);
    }
    
    @Transactional
    public void deleteCategory(Integer id, AuthContext context) {
        permissionChecker.requireAdmin(context);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        
        categoryRepository.delete(category);
        
        auditLogService.log("Category", id.toString(), "DELETE", context.getUserId());
    }
    
    private CategoryDTO toDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setSlug(category.getSlug());
        dto.setDescription(category.getDescription());
        return dto;
    }
    
    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                .replaceAll("[ìíịỉĩ]", "i")
                .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                .replaceAll("[ùúụủũưừứựửữ]", "u")
                .replaceAll("[ỳýỵỷỹ]", "y")
                .replaceAll("[đ]", "d")
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }
}
