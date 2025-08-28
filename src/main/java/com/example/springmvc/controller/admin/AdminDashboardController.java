package com.example.springmvc.controller.admin;

import com.example.springmvc.entity.*;
import com.example.springmvc.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminDashboardController {
    
    private final UserService userService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final OrderService orderService;

    @GetMapping({"", "/dashboard"})
    public String dashboard(Model model) {
        // Get statistics
        List<User> allUsers = userService.findAllUsers();
        List<Product> allProducts = productService.findAllProducts();
        List<Category> allCategories = categoryService.findAllCategories();
        
        long totalUsers = allUsers.size();
        long totalCustomers = userService.findCustomers().size();
        long totalProducts = allProducts.size();
        long activeProducts = productService.findActiveProducts().size();
        long totalCategories = allCategories.size();
        
        // Order statistics
        long pendingOrders = orderService.countOrdersByStatus(Order.OrderStatus.PENDING);
        long confirmedOrders = orderService.countOrdersByStatus(Order.OrderStatus.CONFIRMED);
        long shippedOrders = orderService.countOrdersByStatus(Order.OrderStatus.SHIPPED);
        long deliveredOrders = orderService.countOrdersByStatus(Order.OrderStatus.DELIVERED);
        long cancelledOrders = orderService.countOrdersByStatus(Order.OrderStatus.CANCELLED);
        
        // Recent orders
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = LocalDateTime.now();
        List<Order> recentOrders = orderService.findOrdersBetweenDates(startOfMonth, endOfMonth);
        
        // Calculate total revenue for current month
        BigDecimal monthlyRevenue = recentOrders.stream()
                .filter(order -> order.getStatus() == Order.OrderStatus.DELIVERED)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Low stock products (less than 10 items)
        List<Product> lowStockProducts = allProducts.stream()
                .filter(product -> product.getStockQuantity() < 10)
                .toList();
        
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("activeProducts", activeProducts);
        model.addAttribute("totalCategories", totalCategories);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("confirmedOrders", confirmedOrders);
        model.addAttribute("shippedOrders", shippedOrders);
        model.addAttribute("deliveredOrders", deliveredOrders);
        model.addAttribute("cancelledOrders", cancelledOrders);
        model.addAttribute("monthlyRevenue", monthlyRevenue);
        model.addAttribute("lowStockProducts", lowStockProducts);
        model.addAttribute("recentOrders", recentOrders.size() > 10 ? recentOrders.subList(0, 10) : recentOrders);
        
        return "admin/dashboard";
    }
}
