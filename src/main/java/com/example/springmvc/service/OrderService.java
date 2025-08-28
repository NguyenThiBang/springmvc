package com.example.springmvc.service;

import com.example.springmvc.entity.*;
import com.example.springmvc.repository.OrderRepository;
import com.example.springmvc.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final ProductService productService;

    public Order createOrder(User user, String shippingAddress, String paymentMethod, String notes) {
        List<CartItem> cartItems = cartService.getCartItems(user);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Validate stock availability
        if (!cartService.isValidCartForCheckout(user)) {
            throw new RuntimeException("Some items are out of stock");
        }

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        order.setNotes(notes);
        order.setStatus(Order.OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            orderItems.add(orderItem);

            totalAmount = totalAmount.add(orderItem.getSubtotal());
            
            // Update product stock
            productService.updateStock(cartItem.getProduct().getId(), cartItem.getQuantity());
        }

        order.setTotalAmount(totalAmount);
        order.setOrderItems(orderItems);
        
        Order savedOrder = orderRepository.save(order);
        
        // Clear cart after successful order
        cartService.clearCart(user);
        
        return savedOrder;
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> findByUser(User user) {
        return orderRepository.findByUser(user);
    }

    public Page<Order> findByUser(User user, Pageable pageable) {
        return orderRepository.findByUser(user, pageable);
    }

    public Page<Order> findAllOrders(Pageable pageable) {
        return orderRepository.findAllOrderByOrderDateDesc(pageable);
    }

    public List<Order> findByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    public Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable);
    }

    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Optional<Order> orderOpt = findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus(status);
            
            if (status == Order.OrderStatus.SHIPPED) {
                order.setShippedDate(LocalDateTime.now());
            } else if (status == Order.OrderStatus.DELIVERED) {
                order.setDeliveredDate(LocalDateTime.now());
            }
            
            return orderRepository.save(order);
        }
        throw new RuntimeException("Order not found");
    }

    public void cancelOrder(Long orderId) {
        Optional<Order> orderOpt = findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            if (order.getStatus() == Order.OrderStatus.PENDING || 
                order.getStatus() == Order.OrderStatus.CONFIRMED) {
                
                // Restore product stock
                for (OrderItem item : order.getOrderItems()) {
                    Product product = item.getProduct();
                    product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                    productService.updateProduct(product);
                }
                
                order.setStatus(Order.OrderStatus.CANCELLED);
                orderRepository.save(order);
            } else {
                throw new RuntimeException("Cannot cancel order with status: " + order.getStatus());
            }
        }
    }

    public long countOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.countByStatus(status);
    }

    public List<Order> findOrdersBetweenDates(LocalDateTime start, LocalDateTime end) {
        return orderRepository.findByOrderDateBetween(start, end);
    }
}
