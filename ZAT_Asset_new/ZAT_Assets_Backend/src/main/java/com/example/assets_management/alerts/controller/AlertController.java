package com.example.assets_management.alerts.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.example.assets_management.alerts.entity.AssetAlert;
import com.example.assets_management.alerts.repository.AssetAlertRepository;
import com.example.assets_management.alerts.service.AlertSchedulerService;
import com.example.assets_management.exception.ResourceNotFoundException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {
    private final AssetAlertRepository repo;
    private final AlertSchedulerService scheduler;

    public AlertController(AssetAlertRepository repo, AlertSchedulerService scheduler) {
        this.repo = repo;
        this.scheduler = scheduler;
    }

    @PreAuthorize("hasAuthority(\'Asset.Alert.View\')")
    @GetMapping
    public List<AssetAlert> all() {
        return repo.findAll();
    }

    @PreAuthorize("hasAuthority(\'Asset.Alert.View\')")
    @GetMapping("/upcoming")
    public List<AssetAlert> upcoming() {
        return repo.findByAcknowledgedFalseOrderByDueDateAsc();
    }

    @PreAuthorize("hasAuthority(\'Asset.Alert.Generate\')")
    @PostMapping("/generate")
    public ResponseEntity<String> generate() {
        return ResponseEntity.ok("Generated alerts: " + scheduler.generate());
    }

    @PreAuthorize("hasAuthority(\'Asset.Alert.Acknowledge\')")
    @PatchMapping("/{id}/acknowledge")
    public AssetAlert acknowledge(@PathVariable Long id) {
        AssetAlert a = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Alert not found"));
        a.setAcknowledged(true);
        return repo.save(a);
    }
}
