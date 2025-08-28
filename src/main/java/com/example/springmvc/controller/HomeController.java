package com.example.springmvc.controller;

import com.example.springmvc.entity.Category;
import com.example.springmvc.entity.Product;
import com.example.springmvc.entity.User;
import com.example.springmvc.service.CategoryService;
import com.example.springmvc.service.ProductService;
import com.example.springmvc.service.CartService;
import com.example.springmvc.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final CartService cartService;
    private final UserService userService;

    @GetMapping({"/", "/home"})
    public String home(Model model, Authentication authentication) {
        List<Product> latestProducts = productService.findLatestProducts();
        List<Category> categories = categoryService.findAllCategories();
        
        model.addAttribute("latestProducts", latestProducts);
        model.addAttribute("categories", categories);
        
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.findByUsername(authentication.getName()).orElse(null);
            if (user != null) {
                int cartItemCount = cartService.getCartItemCount(user);
                model.addAttribute("cartItemCount", cartItemCount);
            }
        }
        
        return "home";
    }

    @GetMapping("/products")
    public String products(@RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "12") int size,
                          @RequestParam(defaultValue = "name") String sort,
                          @RequestParam(defaultValue = "asc") String direction,
                          @RequestParam(required = false) Long categoryId,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) BigDecimal minPrice,
                          @RequestParam(required = false) BigDecimal maxPrice,
                          Model model, Authentication authentication) {
        
        Sort.Direction sortDirection = direction.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<Product> productPage;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            productPage = productService.searchProducts(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else if (categoryId != null) {
            Optional<Category> category = categoryService.findById(categoryId);
            if (category.isPresent()) {
                productPage = productService.findByCategory(category.get(), pageable);
                model.addAttribute("selectedCategory", category.get());
            } else {
                productPage = productService.findActiveProducts(pageable);
            }
        } else if (minPrice != null && maxPrice != null) {
            productPage = productService.findByPriceRange(minPrice, maxPrice, pageable);
            model.addAttribute("minPrice", minPrice);
            model.addAttribute("maxPrice", maxPrice);
        } else {
            productPage = productService.findActiveProducts(pageable);
        }
        
        List<Category> categories = categoryService.findAllCategories();
        
        model.addAttribute("productPage", productPage);
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortField", sort);
        model.addAttribute("sortDirection", direction);
        
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.findByUsername(authentication.getName()).orElse(null);
            if (user != null) {
                int cartItemCount = cartService.getCartItemCount(user);
                model.addAttribute("cartItemCount", cartItemCount);
            }
        }
        
        return "products";
    }

    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Long id, Model model, Authentication authentication) {
        Optional<Product> product = productService.findById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            
            // Get related products from same category
            List<Product> relatedProducts = productService.findByCategory(product.get().getCategory());
            relatedProducts.removeIf(p -> p.getId().equals(id)); // Remove current product
            if (relatedProducts.size() > 4) {
                relatedProducts = relatedProducts.subList(0, 4);
            }
            model.addAttribute("relatedProducts", relatedProducts);
            
            if (authentication != null && authentication.isAuthenticated()) {
                User user = userService.findByUsername(authentication.getName()).orElse(null);
                if (user != null) {
                    int cartItemCount = cartService.getCartItemCount(user);
                    model.addAttribute("cartItemCount", cartItemCount);
                }
            }
            
            return "product-detail";
        } else {
            return "redirect:/products";
        }
    }

    @GetMapping("/category/{id}")
    public String categoryProducts(@PathVariable Long id,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "12") int size,
                                  Model model, Authentication authentication) {
        return products(page, size, "name", "asc", id, null, null, null, model, authentication);
    }

    @GetMapping("/search")
    public String search(@RequestParam String keyword,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "12") int size,
                        Model model, Authentication authentication) {
        return products(page, size, "name", "asc", null, keyword, null, null, model, authentication);
    }
}
