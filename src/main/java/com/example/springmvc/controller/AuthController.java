package com.example.springmvc.controller;

import com.example.springmvc.entity.User;
import com.example.springmvc.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "logout", required = false) String logout,
                       @RequestParam(value = "success", required = false) String success,
                       Model model) {
        if (error != null) {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng. Vui lòng thử lại!");
        }
        if (logout != null) {
            model.addAttribute("message", "Bạn đã đăng xuất thành công. Hẹn gặp lại!");
        }
        if (success != null) {
            model.addAttribute("message", "Đăng ký thành công! Vui lòng đăng nhập để tiếp tục.");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                              BindingResult result,
                              @RequestParam String confirmPassword,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        
        // Check for validation errors
        if (result.hasErrors()) {
            return "auth/register";
        }        // Check if passwords match
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp!");
            return "auth/register";
        }

        // Check if username already exists
        if (userService.existsByUsername(user.getUsername())) {
            model.addAttribute("error", "Tên đăng nhập đã tồn tại!");
            return "auth/register";
        }

        // Check if email already exists
        if (userService.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "Email đã được sử dụng!");
            return "auth/register";
        }

        try {
            userService.registerUser(user.getUsername(), user.getPassword(), user.getEmail(), user.getFullName());
            redirectAttributes.addFlashAttribute("success", "true");
            return "redirect:/login?success=true";
        } catch (Exception e) {
            model.addAttribute("error", "Đăng ký thất bại. Vui lòng thử lại.");
            return "auth/register";
        }
    }
    @GetMapping("/login-status")
    @ResponseBody
    public String loginStatus(org.springframework.security.core.Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            return "Đã đăng nhập: " + authentication.getName();
        } else {
            return "Chưa đăng nhập";
        }
    }
     @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        // vì file nằm trong /templates/auth/forgot-password.html
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email,
                                        RedirectAttributes redirectAttributes) {
        // TODO: kiểm tra email trong DB
        boolean emailExists = true; // giả định, bạn sẽ thay bằng userService.checkEmail(email);

        if (emailExists) {
            // TODO: gửi link reset mật khẩu qua email
            redirectAttributes.addFlashAttribute("message",
                    "Liên kết đặt lại mật khẩu đã được gửi tới " + email);
        } else {
            redirectAttributes.addFlashAttribute("error",
                    "Email không tồn tại trong hệ thống. Vui lòng thử lại!");
        }

        // dùng redirect để tránh lỗi F5 form resubmit
        return "redirect:/forgot-password";
    }

}
