package com.example.dispatch.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "delivery_orders")
public class DeliveryOrder {

    @Id
    private String orderId;
    private double latitude;
    private double longitude;
    private String address;
    private double packageWeight;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    protected DeliveryOrder() { }   // required by JPA

    public DeliveryOrder(String orderId, double latitude, double longitude,
                         String address, double packageWeight, Priority priority) {
        this.orderId = orderId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.packageWeight = packageWeight;
        this.priority = priority;
    }

    public String getOrderId() { return orderId; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getAddress() { return address; }
    public double getPackageWeight() { return packageWeight; }
    public Priority getPriority() { return priority; }
}