package com.example.dispatch.dto;

import com.example.dispatch.entity.DeliveryOrder;
import com.example.dispatch.entity.Priority;

public record UnassignedOrderDto(String orderId, String address, double packageWeight,
                                 Priority priority, String reason) {
    public static UnassignedOrderDto from(DeliveryOrder o, String reason) {
        return new UnassignedOrderDto(o.getOrderId(), o.getAddress(),
                o.getPackageWeight(), o.getPriority(), reason);
    }
}