package com.example.assets_management.security.controller;

import com.example.assets_management.security.auth.RefreshTokenService;
import com.example.assets_management.security.dto.*;
import com.example.assets_management.security.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwt;
    private final RefreshTokenService refresh;
    private final com.example.assets_management.employee.repository.EmployeeRepository users;

    @org.springframework.beans.factory.annotation.Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtService jwt, RefreshTokenService refresh, com.example.assets_management.employee.repository.EmployeeRepository users) {
        this.authenticationManager = authenticationManager;
        this.jwt = jwt;
        this.refresh = refresh;
        this.users = users;
    }

    public AuthController(AuthenticationManager authenticationManager, JwtService jwt) {
        this(authenticationManager, jwt, null, null);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest r) {
        Authentication a = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(r.email().trim(), r.password()));
        UserDetails d = (UserDetails) a.getPrincipal();
        String access = jwt.generateToken(d);
        CustomUserDetails c = (CustomUserDetails) d;
        String rt = null;
        if (refresh != null && users != null && c.getUserId() != null)
            rt = refresh.issue(users.findById(c.getUserId()).orElseThrow());
        return ResponseEntity.ok(new LoginResponse("Bearer", access, jwt.getExpirationMs(), c.getUserId(), d.getUsername(), c.getRoleName(), d.getAuthorities().stream().map(x -> x.getAuthority()).toList(), rt));
    }
}
