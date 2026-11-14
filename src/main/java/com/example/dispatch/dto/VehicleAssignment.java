package com.example.dispatch.dto;

import java.util.List;

public record VehicleAssignment(String vehicleId, double totalLoad, String totalDistance,
                                List<AssignedOrderDto> assignedOrders) { }