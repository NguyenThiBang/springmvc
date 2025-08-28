package com.example.springmvc.controller.admin;

import com.example.springmvc.entity.Category;
import com.example.springmvc.entity.Product;
import com.example.springmvc.service.CategoryService;
import com.example.springmvc.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/products")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminProductController {
    
    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping
    public String manageProducts(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(defaultValue = "name") String sort,
                                @RequestParam(defaultValue = "asc") String direction,
                                @RequestParam(required = false) String keyword,
                                Model model) {
        Sort.Direction sortDirection = direction.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<Product> productPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            productPage = productService.searchProducts(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            productPage = productService.findAllProducts(pageable);
        }
        
        List<Category> categories = categoryService.findAllCategories();
        
        model.addAttribute("productPage", productPage);
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortField", sort);
        model.addAttribute("sortDirection", direction);
        
        return "admin/products";
    }
    
    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.findAllCategories());
        return "admin/product-form";
    }
    
    @PostMapping("/add")
    public String addProduct(@Valid @ModelAttribute Product product,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAllCategories());
            return "admin/product-form";
        }
        
        try {
            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("success", "Sản phẩm đã được thêm thành công!");
            return "redirect:/admin/products";
        } catch (Exception e) {
            model.addAttribute("error", "Không thể thêm sản phẩm: " + e.getMessage());
            model.addAttribute("categories", categoryService.findAllCategories());
            return "admin/product-form";
        }
    }
    
    @GetMapping("/{id}/edit")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        Optional<Product> product = productService.findById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            model.addAttribute("categories", categoryService.findAllCategories());
            return "admin/product-form";
        }
        return "redirect:/admin/products";
    }
    
    @PostMapping("/{id}/edit")
    public String editProduct(@PathVariable Long id,
                             @Valid @ModelAttribute Product product,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAllCategories());
            return "admin/product-form";
        }
        
        try {
            product.setId(id);
            productService.updateProduct(product);
            redirectAttributes.addFlashAttribute("success", "Sản phẩm đã được cập nhật thành công!");
            return "redirect:/admin/products";
        } catch (Exception e) {
            model.addAttribute("error", "Không thể cập nhật sản phẩm: " + e.getMessage());
            model.addAttribute("categories", categoryService.findAllCategories());
            return "admin/product-form";
        }
    }
    
    @PostMapping("/{id}/toggle-status")
    public String toggleProductStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Product> productOpt = productService.findById(id);
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                product.setActive(!product.getActive());
                productService.updateProduct(product);
                redirectAttributes.addFlashAttribute("success", 
                    "Sản phẩm đã được " + (product.getActive() ? "kích hoạt" : "vô hiệu hóa") + " thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể cập nhật trạng thái sản phẩm: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }
}
