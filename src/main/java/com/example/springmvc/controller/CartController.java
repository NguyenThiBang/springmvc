package com.example.springmvc.controller;

import com.example.springmvc.entity.CartItem;
import com.example.springmvc.entity.User;
import com.example.springmvc.service.CartService;
import com.example.springmvc.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    @GetMapping
    public String viewCart(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        List<CartItem> cartItems = cartService.getCartItems(user);
        BigDecimal cartTotal = cartService.getCartTotal(user);
        int cartItemCount = cartService.getCartItemCount(user);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartTotal);
        model.addAttribute("cartItemCount", cartItemCount);

        return "cart/cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                           @RequestParam(defaultValue = "1") Integer quantity,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }        try {
            cartService.addToCart(user, productId, quantity);
            redirectAttributes.addFlashAttribute("success", "Đã thêm sản phẩm vào giỏ hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể thêm sản phẩm vào giỏ hàng!");
        }

        return "redirect:/products/" + productId;
    }

    @PostMapping("/add-ajax")
    @ResponseBody
    public ResponseEntity<?> addToCartAjax(@RequestParam Long productId,
                                          @RequestParam(defaultValue = "1") Integer quantity,
                                          Authentication authentication) {        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Vui lòng đăng nhập trước");
        }

        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).body("Vui lòng đăng nhập trước");
        }

        try {
            cartService.addToCart(user, productId, quantity);
            int cartItemCount = cartService.getCartItemCount(user);
            return ResponseEntity.ok().body("{\"success\": true, \"cartItemCount\": " + cartItemCount + "}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Không thể thêm sản phẩm vào giỏ hàng");
        }
    }

    @PostMapping("/update")
    public String updateCartItem(@RequestParam Long cartItemId,
                                @RequestParam Integer quantity,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        try {
            cartService.updateCartItem(cartItemId, quantity);
            redirectAttributes.addFlashAttribute("success", "Cart updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update cart!");
        }

        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long cartItemId,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        try {
            cartService.removeFromCart(cartItemId);
            redirectAttributes.addFlashAttribute("success", "Item removed from cart!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to remove item from cart!");
        }

        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            cartService.clearCart(user);
            redirectAttributes.addFlashAttribute("success", "Cart cleared successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to clear cart!");
        }

        return "redirect:/cart";
    }

    @GetMapping("/count")
    @ResponseBody
    public ResponseEntity<Integer> getCartItemCount(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.ok(0);
        }

        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.ok(0);
        }

        int count = cartService.getCartItemCount(user);
        return ResponseEntity.ok(count);
    }
}
