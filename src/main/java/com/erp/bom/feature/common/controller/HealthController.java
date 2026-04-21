package com.erp.bom.feature.common.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/healthz")
    public ResponseEntity<Map<String, Object>> healthChecks() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of("status", "OK"));
    }
}
