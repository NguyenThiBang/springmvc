package com.example.springmvc.controller;

import com.example.springmvc.entity.User;
import com.example.springmvc.service.CartService;
import com.example.springmvc.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {
    
    private final CartService cartService;
    private final UserService userService;
    
    @ModelAttribute("cartItemCount")
    public Integer addCartItemCount() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() 
                && !authentication.getPrincipal().equals("anonymousUser")) {
                String username = authentication.getName();
                Optional<User> userOpt = userService.findByUsername(username);
                if (userOpt.isPresent()) {
                    return cartService.getCartItemCount(userOpt.get());
                }
            }
        } catch (Exception e) {
            // Log error and return 0 if there's any issue
            System.err.println("Error getting cart item count: " + e.getMessage());
        }
        return 0;
    }
    
    @ModelAttribute("isAuthenticated")
    public Boolean isAuthenticated() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return authentication != null && authentication.isAuthenticated() 
                && !authentication.getPrincipal().equals("anonymousUser")
                && !"anonymousUser".equals(authentication.getName());
        } catch (Exception e) {
            return false;
        }
    }
    
    @ModelAttribute("currentUsername")
    public String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() 
                && !authentication.getPrincipal().equals("anonymousUser")
                && !"anonymousUser".equals(authentication.getName())) {
                return authentication.getName();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
    
    @ModelAttribute("currentUser")
    public User getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() 
                && !authentication.getPrincipal().equals("anonymousUser")
                && !"anonymousUser".equals(authentication.getName())) {
                String username = authentication.getName();
                Optional<User> userOpt = userService.findByUsername(username);
                return userOpt.orElse(null);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
    
    @ModelAttribute("isAdmin")
    public Boolean isAdmin() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() 
                && !authentication.getPrincipal().equals("anonymousUser")) {
                return authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
