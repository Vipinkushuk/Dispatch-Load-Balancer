package com.example.dispatch.strategy;

import com.example.dispatch.dto.DispatchPlanResponse;
import com.example.dispatch.entity.DeliveryOrder;
import com.example.dispatch.entity.Vehicle;
import java.util.List;

public interface AssignmentStrategy {
    DispatchPlanResponse assign(List<DeliveryOrder> orders, List<Vehicle> vehicles);
}