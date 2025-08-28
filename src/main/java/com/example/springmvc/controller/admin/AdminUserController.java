package com.example.springmvc.controller.admin;

import com.example.springmvc.entity.User;
import com.example.springmvc.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUserController {
    
    private final UserService userService;

    @GetMapping
    public String manageUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "admin/users";
    }
    @GetMapping("/new")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/create-user";  // trỏ tới file HTML bạn sẽ tạo
    }

    @PostMapping("/new")
    public String createUser(@ModelAttribute("user") User user) {
        userService.saveUser(user);
        return "redirect:/admin/users"; // quay về danh sách
    }
    
    @PostMapping("/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<User> userOpt = userService.findById(id);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setEnabled(!user.getEnabled());
                userService.updateUser(user);
                redirectAttributes.addFlashAttribute("success", 
                    "Người dùng đã được " + (user.getEnabled() ? "kích hoạt" : "vô hiệu hóa") + " thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể cập nhật trạng thái người dùng: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
    @PostMapping("/add")
    public String addUser(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String email,
                          @RequestParam String fullName,
                          @RequestParam User.Role role,
                          RedirectAttributes redirectAttributes) {
        if (userService.existsByUsername(username)) {
            redirectAttributes.addFlashAttribute("error", "Tên đăng nhập đã tồn tại!");
            return "redirect:/admin/users";
        }
        if (userService.existsByEmail(email)) {
            redirectAttributes.addFlashAttribute("error", "Email đã tồn tại!");
            return "redirect:/admin/users";
        }

        User user = new User();
        user.setUsername(username);
        if (!password.startsWith("{noop}")) {
            user.setPassword("{noop}" + password); // test thôi, thực tế cần BCrypt
        } else {
            user.setPassword(password);
        }
        user.setEmail(email);
        user.setFullName(fullName);
        user.setRole(role);
        user.setEnabled(true);

        userService.saveUser(user);

        redirectAttributes.addFlashAttribute("success", "Thêm người dùng thành công!");
        return "redirect:/admin/users";
    }
    
}
