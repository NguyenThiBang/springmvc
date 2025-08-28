package com.example.springmvc.controller;

import com.example.springmvc.entity.User;
import com.example.springmvc.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/account")
public class MyAccountController {

    private final UserService userService;

    // Hiển thị thông tin tài khoản
    @GetMapping
    public String myAccount(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow();
        model.addAttribute("user", user);
        return "fragments/my-account";
    }
@PostMapping("/update")
public String updateAccount(@ModelAttribute("user") User user,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {

    String username = authentication.getName();
    User currentUser = userService.findByUsername(username).orElseThrow();

    currentUser.setFullName(user.getFullName());
    currentUser.setEmail(user.getEmail());
    currentUser.setPhone(user.getPhone());
    currentUser.setAddress(user.getAddress());

    userService.updateUser(currentUser);
    redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thành công!");
    return "redirect:/account";
}
@GetMapping("/change-password")
public String showChangePasswordForm() {
    return "fragments/change-password";
}
@PostMapping("/change-password")
public String changePassword(@RequestParam String currentPassword,
                             @RequestParam String newPassword,
                             @RequestParam String confirmPassword,  // <-- bắt buộc
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
    String username = authentication.getName();
    User user = userService.findByUsername(username).orElseThrow();

    // 1. Kiểm tra mật khẩu hiện tại
    if (!user.getPassword().replace("{noop}", "").equals(currentPassword)) {
        redirectAttributes.addFlashAttribute("error", "Mật khẩu hiện tại không đúng!");
        return "redirect:/account/change-password";
    }

    // 2. Kiểm tra mật khẩu mới có khác mật khẩu cũ không
    if (currentPassword.equals(newPassword)) {
        redirectAttributes.addFlashAttribute("error", "Mật khẩu mới không được trùng mật khẩu hiện tại!");
        return "redirect:/account/change-password";
    }

    // 3. Kiểm tra độ dài mật khẩu mới
    if (newPassword.length() < 6) {
        redirectAttributes.addFlashAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự!");
        return "redirect:/account/change-password";
    }

    // 4. Kiểm tra xác nhận mật khẩu
    if (!newPassword.equals(confirmPassword)) {
        redirectAttributes.addFlashAttribute("error", "Xác nhận mật khẩu không khớp!");
        return "redirect:/account/change-password";
    }

    // 5. Lưu mật khẩu mới
    user.setPassword("{noop}" + newPassword);
    userService.updateUser(user);

    redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");
    return "redirect:/account";
}

}
