package com.example.dispatch.controller;

import com.example.dispatch.dto.*;
import com.example.dispatch.service.DispatchService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dispatch")
public class DispatchController {

    private final DispatchService service;

    public DispatchController(DispatchService service) {
        this.service = service;
    }

    @PostMapping("/orders")
    public ResponseEntity<ApiResponse> addOrders(@Valid @RequestBody OrderBatchRequest request) {
        service.saveOrders(request.orders());
        return ResponseEntity.ok(ApiResponse.success("Delivery orders accepted."));
    }

    @PostMapping("/vehicles")
    public ResponseEntity<ApiResponse> addVehicles(@Valid @RequestBody VehicleBatchRequest request) {
        service.saveVehicles(request.vehicles());
        return ResponseEntity.ok(ApiResponse.success("Vehicle details accepted."));
    }

    @GetMapping("/plan")
    public ResponseEntity<DispatchPlanResponse> getPlan() {
        return ResponseEntity.ok(service.generatePlan());
    }
}