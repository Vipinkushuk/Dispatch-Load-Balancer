
package com.example.dispatch.strategy;

import com.example.dispatch.dto.*;
import com.example.dispatch.entity.DeliveryOrder;
import com.example.dispatch.entity.Vehicle;
import com.example.dispatch.util.HaversineCalculator;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class GreedyNearestVehicleStrategy implements AssignmentStrategy {

    private static final double EPS = 1e-9;

    private static final Comparator<DeliveryOrder> ORDER_SORT =
            Comparator.comparingInt((DeliveryOrder o) -> o.getPriority().getRank())
                    .thenComparing(
                            DeliveryOrder::getPackageWeight,
                            Comparator.reverseOrder()
                    )
                    .thenComparing(DeliveryOrder::getOrderId);

    @Override
    public DispatchPlanResponse assign(
            List<DeliveryOrder> orders,
            List<Vehicle> vehicles) {

        List<VehicleState> states = vehicles.stream()
                .sorted(Comparator.comparing(Vehicle::getVehicleId))
                .map(VehicleState::new)
                .collect(Collectors.toList());

        double maxCapacity = vehicles.stream()
                .mapToDouble(Vehicle::getCapacity)
                .max()
                .orElse(0);

        List<DeliveryOrder> sortedOrders = new ArrayList<>(orders);
        sortedOrders.sort(ORDER_SORT);

        List<UnassignedOrderDto> unassigned = new ArrayList<>();

        // Step 1: Assign orders based on priority, weight and nearest vehicle.
        for (DeliveryOrder order : sortedOrders) {

            if (order.getPackageWeight() > maxCapacity) {
                unassigned.add(UnassignedOrderDto.from(
                        order,
                        "Package weight exceeds the capacity of every vehicle"
                ));
                continue;
            }

            VehicleState best = null;
            double bestDistance = Double.MAX_VALUE;

            for (VehicleState state : states) {

                if (!state.canCarry(order)) {
                    continue;
                }

                double d = state.distanceTo(order);

                boolean closer = d < bestDistance - EPS;

                boolean tieMoreRoom =
                        Math.abs(d - bestDistance) <= EPS
                                && best != null
                                && state.remaining() > best.remaining();

                if (best == null || closer || tieMoreRoom) {
                    best = state;
                    bestDistance = d;
                }
            }

            if (best == null) {
                unassigned.add(UnassignedOrderDto.from(
                        order,
                        "No vehicle has enough remaining capacity"
                ));
            } else {
                best.add(order, bestDistance);
            }
        }

        // Step 2: Optimize delivery stop sequence for each vehicle.
        List<VehicleAssignment> plan = states.stream()
                .map(VehicleState::toAssignment)
                .collect(Collectors.toList());

        return new DispatchPlanResponse(plan, unassigned);
    }

    /**
     * Keeps track of one vehicle while the dispatch plan is being built.
     */
    private static class VehicleState {

        private final Vehicle vehicle;
        private final List<DeliveryOrder> orders = new ArrayList<>();

        private double load = 0;
        private double lat;
        private double lon;

        VehicleState(Vehicle vehicle) {
            this.vehicle = vehicle;
            this.lat = vehicle.getCurrentLatitude();
            this.lon = vehicle.getCurrentLongitude();
        }

        double remaining() {
            return vehicle.getCapacity() - load;
        }

        boolean canCarry(DeliveryOrder o) {
            return o.getPackageWeight() <= remaining() + EPS;
        }

        double distanceTo(DeliveryOrder o) {
            return HaversineCalculator.distanceKm(
                    lat,
                    lon,
                    o.getLatitude(),
                    o.getLongitude()
            );
        }

        void add(DeliveryOrder o, double legDistance) {
            orders.add(o);
            load += o.getPackageWeight();

            lat = o.getLatitude();
            lon = o.getLongitude();
        }

        /**
         * Reorders delivery stops using the nearest-neighbour approach.
         * Starts from the vehicle's original location and repeatedly
         * selects the nearest remaining delivery location.
         */
        VehicleAssignment toAssignment() {

            List<DeliveryOrder> remaining = new ArrayList<>(orders);
            List<AssignedOrderDto> route = new ArrayList<>();

            double curLat = vehicle.getCurrentLatitude();
            double curLon = vehicle.getCurrentLongitude();

            double distance = 0;

            while (!remaining.isEmpty()) {

                DeliveryOrder next = null;
                double best = Double.MAX_VALUE;

                for (DeliveryOrder o : remaining) {

                    double d = HaversineCalculator.distanceKm(
                            curLat,
                            curLon,
                            o.getLatitude(),
                            o.getLongitude()
                    );

                    if (d < best) {
                        best = d;
                        next = o;
                    }
                }

                remaining.remove(next);

                route.add(AssignedOrderDto.from(next));

                distance += best;

                curLat = next.getLatitude();
                curLon = next.getLongitude();
            }

            return new VehicleAssignment(
                    vehicle.getVehicleId(),
                    load,
                    String.format(Locale.US, "%.2f km", distance),
                    route
            );
        }
    }
}
