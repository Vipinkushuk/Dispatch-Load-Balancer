package com.example.dispatch.service;

import com.example.dispatch.dto.DispatchPlanResponse;
import com.example.dispatch.dto.OrderRequest;
import com.example.dispatch.dto.VehicleRequest;
import com.example.dispatch.entity.DeliveryOrder;
import com.example.dispatch.entity.Vehicle;
import com.example.dispatch.exception.InvalidInputException;
import com.example.dispatch.repository.OrderRepository;
import com.example.dispatch.repository.VehicleRepository;
import com.example.dispatch.strategy.AssignmentStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DispatchServiceImpl implements DispatchService {

    private final OrderRepository orderRepository;
    private final VehicleRepository vehicleRepository;
    private final AssignmentStrategy strategy;

    public DispatchServiceImpl(OrderRepository orderRepository,
                               VehicleRepository vehicleRepository,
                               AssignmentStrategy strategy) {
        this.orderRepository = orderRepository;
        this.vehicleRepository = vehicleRepository;
        this.strategy = strategy;
    }

    @Override
    @Transactional
    public void saveOrders(List<OrderRequest> requests) {
        checkDuplicates(requests.stream().map(r -> r.orderId().trim()).toList(), "orderId");
        List<DeliveryOrder> entities = requests.stream()
                .map(r -> new DeliveryOrder(r.orderId().trim(), r.latitude(), r.longitude(),
                        r.address(), r.packageWeight(), r.priority()))
                .toList();
        orderRepository.saveAll(entities);   // insert new, update existing (upsert)
    }

    @Override
    @Transactional
    public void saveVehicles(List<VehicleRequest> requests) {
        checkDuplicates(requests.stream().map(r -> r.vehicleId().trim()).toList(), "vehicleId");
        List<Vehicle> entities = requests.stream()
                .map(r -> new Vehicle(r.vehicleId().trim(), r.capacity(), r.currentLatitude(),
                        r.currentLongitude(), r.currentAddress()))
                .toList();
        vehicleRepository.saveAll(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public DispatchPlanResponse generatePlan() {
        List<DeliveryOrder> orders = orderRepository.findAll();
        List<Vehicle> vehicles = vehicleRepository.findAll();
        if (orders.isEmpty()) {
            throw new InvalidInputException("No orders found. Please POST orders first.");
        }
        if (vehicles.isEmpty()) {
            throw new InvalidInputException("No vehicles found. Please POST vehicles first.");
        }
        return strategy.assign(orders, vehicles);
    }

    private void checkDuplicates(List<String> ids, String fieldName) {
        Set<String> seen = new HashSet<>();
        for (String id : ids) {
            if (!seen.add(id)) {
                throw new InvalidInputException("Duplicate " + fieldName + " in request: " + id);
            }
        }
    }
}