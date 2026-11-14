package com.example.dispatch.strategy;

import com.example.dispatch.dto.DispatchPlanResponse;
import com.example.dispatch.entity.DeliveryOrder;
import com.example.dispatch.entity.Priority;
import com.example.dispatch.entity.Vehicle;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GreedyNearestVehicleStrategyTest {

    private final GreedyNearestVehicleStrategy strategy = new GreedyNearestVehicleStrategy();

    private DeliveryOrder order(String id, double lat, double lon, double w, Priority p) {
        return new DeliveryOrder(id, lat, lon, "addr-" + id, w, p);
    }

    private Vehicle vehicle(String id, double cap, double lat, double lon) {
        return new Vehicle(id, cap, lat, lon, "start-" + id);
    }

    @Test
    void highPriorityIsAssignedBeforeLow() {
        List<DeliveryOrder> orders = List.of(
                order("LOW1", 28.6, 77.2, 10, Priority.LOW),
                order("HIGH1", 28.6, 77.2, 10, Priority.HIGH));
        List<Vehicle> vehicles = List.of(vehicle("V1", 10, 28.6, 77.2));

        DispatchPlanResponse r = strategy.assign(orders, vehicles);

        assertEquals("HIGH1", r.dispatchPlan().get(0).assignedOrders().get(0).orderId());
        assertEquals(1, r.unassignedOrders().size());
        assertEquals("LOW1", r.unassignedOrders().get(0).orderId());
    }

    @Test
    void loadNeverExceedsCapacity() {
        List<DeliveryOrder> orders = List.of(
                order("A", 28.6, 77.2, 30, Priority.HIGH),
                order("B", 28.6, 77.2, 30, Priority.HIGH));
        List<Vehicle> vehicles = List.of(vehicle("V1", 50, 28.6, 77.2));

        DispatchPlanResponse r = strategy.assign(orders, vehicles);

        assertTrue(r.dispatchPlan().get(0).totalLoad() <= 50);
        assertEquals(1, r.unassignedOrders().size());
    }

    @Test
    void nearestVehicleGetsTheOrder() {
        List<DeliveryOrder> orders = List.of(order("A", 28.6139, 77.2090, 10, Priority.HIGH));
        List<Vehicle> vehicles = List.of(
                vehicle("CHENNAI", 100, 13.0827, 80.2707),
                vehicle("DELHI", 100, 28.6139, 77.2090));

        DispatchPlanResponse r = strategy.assign(orders, vehicles);

        var delhi = r.dispatchPlan().stream()
                .filter(v -> v.vehicleId().equals("DELHI")).findFirst().orElseThrow();
        assertEquals(1, delhi.assignedOrders().size());
        assertEquals("0.00 km", delhi.totalDistance());
    }

    @Test
    void orderHeavierThanEveryVehicleIsUnassigned() {
        List<DeliveryOrder> orders = List.of(order("BIG", 28.6, 77.2, 500, Priority.HIGH));
        List<Vehicle> vehicles = List.of(vehicle("V1", 100, 28.6, 77.2));

        DispatchPlanResponse r = strategy.assign(orders, vehicles);

        assertEquals(1, r.unassignedOrders().size());
        assertTrue(r.unassignedOrders().get(0).reason().contains("exceeds"));
    }

    @Test
    void overflowGoesToNextNearestVehicle() {
        List<DeliveryOrder> orders = List.of(
                order("A", 28.6139, 77.2090, 60, Priority.HIGH),
                order("B", 28.6139, 77.2090, 60, Priority.HIGH));
        List<Vehicle> vehicles = List.of(
                vehicle("V1", 100, 28.6139, 77.2090),
                vehicle("V2", 100, 28.7041, 77.1025));

        DispatchPlanResponse r = strategy.assign(orders, vehicles);

        assertTrue(r.unassignedOrders().isEmpty());
        assertEquals(1, r.dispatchPlan().get(0).assignedOrders().size());
        assertEquals(1, r.dispatchPlan().get(1).assignedOrders().size());
    }
}