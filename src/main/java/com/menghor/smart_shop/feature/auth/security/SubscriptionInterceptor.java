package com.menghor.smart_shop.feature.auth.security;

import com.menghor.smart_shop.enumations.RoleEnum;
import com.menghor.smart_shop.exceptoins.error.BadRequestException;
import com.menghor.smart_shop.feature.auth.models.Role;
import com.menghor.smart_shop.feature.auth.models.UserEntity;
import com.menghor.smart_shop.feature.setting.service.SubscriptionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionInterceptor implements HandlerInterceptor {

    private final SubscriptionService subscriptionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Get the authenticated user from the security context
        UserEntity user = (UserEntity) request.getAttribute("currentUser");

        if (user == null) {
            return true; // No user authenticated, let the security handle it
        }

        // Check if the user has an admin, manager, or developer role
        List<Role> roles = user.getRoles();
        boolean isAdmin = roles.stream()
                .anyMatch(role -> role.getName() == RoleEnum.ADMIN ||
                        role.getName() == RoleEnum.MANAGER ||
                        role.getName() == RoleEnum.DEVELOPER);

        if (isAdmin) {
            return true; // Admin users bypass subscription checks
        }

        // If the user is a shop admin, check if they have an active subscription
        boolean isShopAdmin = roles.stream()
                .anyMatch(role -> role.getName() == RoleEnum.SHOP_ADMIN);

        if (isShopAdmin) {
            // Check subscription status for shop/product operations
            if (isShopOperation(request.getRequestURI())) {
                boolean hasActiveSubscription = subscriptionService.hasActiveSubscription(user.getId());

                if (!hasActiveSubscription) {
                    log.warn("User ID: {} attempted to access shop operation without an active subscription", user.getId());
                    throw new BadRequestException("Your subscription has expired. Please renew your subscription to continue using shop features.");
                }
            }
        }

        return true;
    }

    private boolean isShopOperation(String requestURI) {
        // Define URIs that should be restricted to active subscribers
        return requestURI.contains("/api/v1/shop") ||
                requestURI.contains("/api/v1/product") ||
                requestURI.contains("/api/v1/category") ||
                requestURI.contains("/api/v1/banner") ||
                requestURI.contains("/api/v1/order") ||
                requestURI.contains("/api/v1/cart");
    }
}