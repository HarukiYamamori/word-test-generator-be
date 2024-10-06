package com.application.pdfapplication.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController implements HealthCheckApi {
    @Override
    public ResponseEntity<Void> healthCheckGet() {
        return ResponseEntity.ok().build();
    }
}
