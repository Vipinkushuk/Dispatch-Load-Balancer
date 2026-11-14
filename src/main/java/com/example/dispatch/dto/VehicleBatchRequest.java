package com.example.dispatch.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record VehicleBatchRequest(
        @NotEmpty(message = "vehicles list must not be empty") List<@Valid VehicleRequest> vehicles) { }