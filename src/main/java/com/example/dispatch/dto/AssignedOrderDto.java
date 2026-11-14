package com.example.dispatch.dto;

import com.example.dispatch.entity.DeliveryOrder;
import com.example.dispatch.entity.Priority;

public record AssignedOrderDto(String orderId, double latitude, double longitude,
                               String address, double packageWeight, Priority priority) {
    public static AssignedOrderDto from(DeliveryOrder o) {
        return new AssignedOrderDto(o.getOrderId(), o.getLatitude(), o.getLongitude(),
                o.getAddress(), o.getPackageWeight(), o.getPriority());
    }
}