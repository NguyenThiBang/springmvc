package com.example.springmvc.controller;

import com.example.springmvc.entity.Order;
import com.example.springmvc.entity.User;
import com.example.springmvc.service.CartService;
import com.example.springmvc.service.OrderService;
import com.example.springmvc.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final UserService userService;

    @GetMapping
    public String viewOrders(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            Model model,
                            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orderPage = orderService.findByUser(user, pageable);

        model.addAttribute("orderPage", orderPage);
        model.addAttribute("currentPage", page);
        
        int cartItemCount = cartService.getCartItemCount(user);
        model.addAttribute("cartItemCount", cartItemCount);

        return "orders/orders";
    }

    @GetMapping("/{id}")
    public String viewOrderDetail(@PathVariable Long id,
                                 Model model,
                                 Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        

        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        Optional<Order> orderOpt = orderService.findById(id);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            // Check if the order belongs to the current user or user is admin
            if (order.getUser().getId().equals(user.getId()) || user.getRole() == User.Role.ADMIN) {
                model.addAttribute("order", order);
                
                int cartItemCount = cartService.getCartItemCount(user);
                model.addAttribute("cartItemCount", cartItemCount);
                
                return "orders/order-detail";
            }
            
   
           
        }

        return "redirect:/orders";
    }
    

    @GetMapping("/checkout")
    public String showCheckout(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        if (cartService.getCartItems(user).isEmpty()) {
            return "redirect:/cart";
        }

        BigDecimal cartTotal = cartService.getCartTotal(user);
        model.addAttribute("cartTotal", cartTotal);
        model.addAttribute("user", user);
        
        int cartItemCount = cartService.getCartItemCount(user);
        model.addAttribute("cartItemCount", cartItemCount);

        return "orders/checkout";
    }

    @PostMapping("/place")
    public String placeOrder(@RequestParam String shippingAddress,
                            @RequestParam String paymentMethod,
                            @RequestParam(required = false) String notes,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            if (!cartService.isValidCartForCheckout(user)) {
                redirectAttributes.addFlashAttribute("error", "Some items in your cart are out of stock!");
                return "redirect:/cart";
            }

            Order order = orderService.createOrder(user, shippingAddress, paymentMethod, notes);
            redirectAttributes.addFlashAttribute("success", "Order placed successfully! Order ID: " + order.getId());
            return "redirect:/orders/" + order.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to place order: " + e.getMessage());
            return "redirect:/orders/checkout";
        }
    }

    @PostMapping("/{id}/cancel")
    public String cancelOrder(@PathVariable Long id,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            Optional<Order> orderOpt = orderService.findById(id);
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                // Check if the order belongs to the current user or user is admin
                if (order.getUser().getId().equals(user.getId()) || user.getRole() == User.Role.ADMIN) {
                    orderService.cancelOrder(id);
                    redirectAttributes.addFlashAttribute("success", "Order cancelled successfully!");
                } else {
                    redirectAttributes.addFlashAttribute("error", "You don't have permission to cancel this order!");
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Order not found!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to cancel order: " + e.getMessage());
        }

        return "redirect:/orders/" + id;
    }
    
}
