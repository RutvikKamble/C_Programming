package com.example.assets_management.security.context;

import com.example.assets_management.security.service.CustomUserDetails;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CurrentUserService {

    public CustomUserDetails requireCurrentUser() {
        Authentication authentication = currentAuthentication();
        if (authentication.getPrincipal() instanceof CustomUserDetails user) {
            return user;
        }
        throw new IllegalStateException("Authenticated user details are not available");
    }

    public UUID requireUserId() {
        UUID userId = requireCurrentUser().getUserId();
        if (userId == null) {
            throw new IllegalStateException("Authenticated user UUID is not available");
        }
        return userId;
    }

    public String requireEmail() {
        return requireCurrentUser().getUsername();
    }

    public UUID getCompanyId() { return requireCurrentUser().getCompanyId(); }
    public UUID getBranchId() { return requireCurrentUser().getBranchId(); }
    public UUID getDepartmentId() { return requireCurrentUser().getDepartmentId(); }
    public UUID getRoleId() { return requireCurrentUser().getRoleId(); }
    public String getRoleName() { return requireCurrentUser().getRoleName(); }

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    private Authentication currentAuthentication() {
        if (!isAuthenticated()) {
            throw new IllegalStateException("No authenticated user is available");
        }
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
