package com.example.dispatch.dto;

import java.util.List;

public record DispatchPlanResponse(List<VehicleAssignment> dispatchPlan,
                                   List<UnassignedOrderDto> unassignedOrders) { }