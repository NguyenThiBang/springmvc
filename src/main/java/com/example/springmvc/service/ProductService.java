package com.example.springmvc.service;

import com.example.springmvc.entity.Product;
import com.example.springmvc.entity.Category;
import com.example.springmvc.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> findActiveProducts() {
        return productRepository.findByActiveTrue();
    }

    public Page<Product> findActiveProducts(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable);
    }

    public List<Product> findByCategory(Category category) {
        return productRepository.findByCategory(category);
    }

    public Page<Product> findByCategory(Category category, Pageable pageable) {
        return productRepository.findByActiveTrueAndCategory(category, pageable);
    }

    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return productRepository.findByKeyword(keyword, pageable);
    }

    public Page<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findByPriceRange(minPrice, maxPrice, pageable);
    }

    public List<Product> findLatestProducts() {
        return productRepository.findTop8ByActiveTrueOrderByCreatedAtDesc();
    }

    public List<Product> findByBrand(String brand) {
        return productRepository.findByBrandAndActiveTrue(brand);
    }

    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public void deactivateProduct(Long id) {
        Optional<Product> productOpt = findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setActive(false);
            updateProduct(product);
        }
    }

    public void activateProduct(Long id) {
        Optional<Product> productOpt = findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setActive(true);
            updateProduct(product);
        }
    }

    public boolean isInStock(Long productId, Integer quantity) {
        Optional<Product> productOpt = findById(productId);
        return productOpt.map(product -> product.getStockQuantity() >= quantity).orElse(false);
    }

    public void updateStock(Long productId, Integer quantity) {
        Optional<Product> productOpt = findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setStockQuantity(product.getStockQuantity() - quantity);
            updateProduct(product);
        }
    }

    public Page<Product> findAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
}
