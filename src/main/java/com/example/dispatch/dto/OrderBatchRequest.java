package com.example.dispatch.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record OrderBatchRequest(
        @NotEmpty(message = "orders list must not be empty") List<@Valid OrderRequest> orders) { }