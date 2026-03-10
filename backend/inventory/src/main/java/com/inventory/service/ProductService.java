package com.inventory.service;

import com.inventory.dto.ProductDTO;
import com.inventory.entity.Category;
import com.inventory.entity.Product;
import com.inventory.exception.BadRequestException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.CategoryRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.security.AuthContext;
import com.inventory.security.PermissionChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final PermissionChecker permissionChecker;
    private final AuditLogService auditLogService;
    
    public List<ProductDTO> getAllProducts(AuthContext context) {
        // Tất cả user đều có thể xem danh sách sản phẩm
        return productRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<ProductDTO> getActiveProducts(AuthContext context) {
        return productRepository.findAllActive().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public ProductDTO getProductById(Integer id, AuthContext context) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        auditLogService.log("Product", id.toString(), "VIEW", context.getUserId());
        
        return toDTO(product);
    }
    
    public ProductDTO getProductByCode(String code, AuthContext context) {
        Product product = productRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "code", code));
        return toDTO(product);
    }
    
    public List<ProductDTO> searchProducts(String keyword, AuthContext context) {
        return productRepository.searchByKeyword(keyword).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ProductDTO createProduct(ProductDTO dto, AuthContext context) {
        permissionChecker.requireManager(context); // Chỉ MANAGER hoặc ADMIN
        
        if (productRepository.existsByCode(dto.getCode())) {
            throw new BadRequestException("Mã sản phẩm đã tồn tại: " + dto.getCode());
        }
        
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", dto.getCategoryId()));
        
        Product product = new Product();
        product.setCode(dto.getCode());
        product.setName(dto.getName());
        product.setSlug(generateSlug(dto.getName()));
        product.setImageUrl(dto.getImageUrl());
        product.setCategory(category);
        product.setUnit(dto.getUnit());
        product.setPriceIn(dto.getPriceIn());
        product.setPriceOut(dto.getPriceOut());
        product.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        
        product = productRepository.save(product);
        
        auditLogService.log("Product", product.getId().toString(), "CREATE", context.getUserId());
        
        return toDTO(product);
    }
    
    @Transactional
    public ProductDTO updateProduct(Integer id, ProductDTO dto, AuthContext context) {
        permissionChecker.requireManager(context);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        if (!product.getCode().equals(dto.getCode()) && productRepository.existsByCode(dto.getCode())) {
            throw new BadRequestException("Mã sản phẩm đã tồn tại: " + dto.getCode());
        }
        
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", dto.getCategoryId()));
        
        product.setCode(dto.getCode());
        product.setName(dto.getName());
        product.setSlug(generateSlug(dto.getName()));
        product.setImageUrl(dto.getImageUrl());
        product.setCategory(category);
        product.setUnit(dto.getUnit());
        product.setPriceIn(dto.getPriceIn());
        product.setPriceOut(dto.getPriceOut());
        product.setIsActive(dto.getIsActive());
        
        product = productRepository.save(product);
        
        auditLogService.log("Product", product.getId().toString(), "UPDATE", context.getUserId());
        
        return toDTO(product);
    }
    
    @Transactional
    public void deleteProduct(Integer id, AuthContext context) {
        permissionChecker.requireAdmin(context); // Chỉ ADMIN
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        productRepository.delete(product);
        
        auditLogService.log("Product", id.toString(), "DELETE", context.getUserId());
    }
    
    private ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setCode(product.getCode());
        dto.setName(product.getName());
        dto.setSlug(product.getSlug());
        dto.setImageUrl(product.getImageUrl());
        dto.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
        dto.setCategoryName(product.getCategory() != null ? product.getCategory().getName() : null);
        dto.setUnit(product.getUnit());
        dto.setPriceIn(product.getPriceIn());
        dto.setPriceOut(product.getPriceOut());
        dto.setIsActive(product.getIsActive());
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
