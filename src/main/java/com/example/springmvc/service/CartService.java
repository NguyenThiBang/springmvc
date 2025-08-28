package com.example.springmvc.service;

import com.example.springmvc.entity.*;
import com.example.springmvc.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;

    public void addToCart(User user, Long productId, Integer quantity) {
        Optional<Product> productOpt = productService.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            Optional<CartItem> existingItem = cartItemRepository.findByUserAndProduct(user, product);
            
            if (existingItem.isPresent()) {
                CartItem cartItem = existingItem.get();
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                cartItemRepository.save(cartItem);
            } else {
                CartItem cartItem = new CartItem();
                cartItem.setUser(user);
                cartItem.setProduct(product);
                cartItem.setQuantity(quantity);
                cartItemRepository.save(cartItem);
            }
        }
    }

    public void updateCartItem(Long cartItemId, Integer quantity) {
        Optional<CartItem> cartItemOpt = cartItemRepository.findById(cartItemId);
        if (cartItemOpt.isPresent()) {
            CartItem cartItem = cartItemOpt.get();
            if (quantity > 0) {
                cartItem.setQuantity(quantity);
                cartItemRepository.save(cartItem);
            } else {
                cartItemRepository.delete(cartItem);
            }
        }
    }

    public void removeFromCart(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    public List<CartItem> getCartItems(User user) {
        return cartItemRepository.findByUser(user);
    }

    public BigDecimal getCartTotal(User user) {
        List<CartItem> cartItems = getCartItems(user);
        return cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getCartItemCount(User user) {
        return (int) cartItemRepository.countByUser(user);
    }

    public void clearCart(User user) {
        cartItemRepository.deleteByUser(user);
    }

    public boolean isValidCartForCheckout(User user) {
        List<CartItem> cartItems = getCartItems(user);
        return cartItems.stream()
                .allMatch(item -> productService.isInStock(item.getProduct().getId(), item.getQuantity()));
    }
}
