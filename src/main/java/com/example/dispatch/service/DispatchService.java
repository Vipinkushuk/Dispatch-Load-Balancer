package com.example.dispatch.service;

import com.example.dispatch.dto.DispatchPlanResponse;
import com.example.dispatch.dto.OrderRequest;
import com.example.dispatch.dto.VehicleRequest;
import java.util.List;

public interface DispatchService {
    void saveOrders(List<OrderRequest> orders);
    void saveVehicles(List<VehicleRequest> vehicles);
    DispatchPlanResponse generatePlan();
}