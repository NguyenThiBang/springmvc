package com.example.springmvc.controller.admin;

import com.example.springmvc.entity.Category;
import com.example.springmvc.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/categories")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminCategoryController {
    
    private final CategoryService categoryService;

    @GetMapping
    public String manageCategories(Model model) {
        model.addAttribute("categories", categoryService.findAllCategories());
        model.addAttribute("category", new Category());
        return "admin/categories";
    }
    
    @PostMapping("/add")
    public String addCategory(@Valid @ModelAttribute Category category,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAllCategories());
            return "admin/categories";
        }
        
        if (categoryService.existsByName(category.getName())) {
            model.addAttribute("error", "Danh mục với tên này đã tồn tại!");
            model.addAttribute("categories", categoryService.findAllCategories());
            return "admin/categories";
        }
        
        try {
            categoryService.saveCategory(category);
            redirectAttributes.addFlashAttribute("success", "Danh mục đã được thêm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể thêm danh mục: " + e.getMessage());
        }
        
        return "redirect:/admin/categories";
    }
    
    @PostMapping("/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("success", "Danh mục đã được xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa danh mục: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }
        @GetMapping("/{id}/edit")
    public String editCategoryForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return categoryService.findById(id)
                .map(category -> {
                    model.addAttribute("category", category);
                    model.addAttribute("categories", categoryService.findAllCategories());
                    return "admin/categories";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Danh mục không tồn tại!");
                    return "redirect:/admin/categories";
                });
    }
    @PostMapping("/{id}/update")
    public String updateCategory(@PathVariable Long id,
                                @Valid @ModelAttribute("category") Category category,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAllCategories());
            return "admin/categories";
        }

        return categoryService.findById(id).map(existing -> {
            // kiểm tra trùng tên (nếu khác tên cũ)
            if (!existing.getName().equals(category.getName()) &&
                    categoryService.existsByName(category.getName())) {
                model.addAttribute("categories", categoryService.findAllCategories());
                model.addAttribute("error", "Tên danh mục đã tồn tại!");
                return "admin/categories";
            }

            existing.setName(category.getName());
            existing.setDescription(category.getDescription());
            categoryService.updateCategory(existing);

            redirectAttributes.addFlashAttribute("success", "Cập nhật danh mục thành công!");
            return "redirect:/admin/categories";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("error", "Danh mục không tồn tại!");
            return "redirect:/admin/categories";
        });
    }

}
