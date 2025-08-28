package com.example.springmvc.controller.admin;

import com.example.springmvc.entity.Order;
import com.example.springmvc.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminOrderController {
    
    private final OrderService orderService;

    @GetMapping
    public String manageOrders(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "20") int size,
                              @RequestParam(required = false) Order.OrderStatus status,
                              Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "orderDate"));
        
        Page<Order> orderPage;
        if (status != null) {
            orderPage = orderService.findByStatus(status, pageable);
            model.addAttribute("selectedStatus", status);
        } else {
            orderPage = orderService.findAllOrders(pageable);
        }
        
        model.addAttribute("orderPage", orderPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("orderStatuses", Order.OrderStatus.values());
        
        return "admin/orders";
    }
    
    @GetMapping("/{id}")
    public String viewOrderDetail(@PathVariable Long id, Model model) {
        Optional<Order> order = orderService.findById(id);
        if (order.isPresent()) {
            model.addAttribute("order", order.get());
            model.addAttribute("orderStatuses", Order.OrderStatus.values());
            return "admin/order-detail";
        }
        return "redirect:/admin/orders";
    }
    
    @PostMapping("/{id}/update-status")
    public String updateOrderStatus(@PathVariable Long id,
                                   @RequestParam Order.OrderStatus status,
                                   RedirectAttributes redirectAttributes) {
        try {
            orderService.updateOrderStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Trạng thái đơn hàng đã được cập nhật thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể cập nhật trạng thái đơn hàng: " + e.getMessage());
        }
        return "redirect:/admin/orders/" + id;
    }
}
