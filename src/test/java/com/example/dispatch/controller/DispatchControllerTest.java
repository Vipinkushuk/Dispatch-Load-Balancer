package com.example.dispatch.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DispatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void validOrdersAreAccepted() throws Exception {
        String body = """
            {"orders":[{"orderId":"T1","latitude":28.6139,"longitude":77.2090,
            "address":"Connaught Place, Delhi, India","packageWeight":15,"priority":"HIGH"}]}""";
        mockMvc.perform(post("/api/dispatch/orders").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void invalidLatitudeIsRejected() throws Exception {
        String body = """
            {"orders":[{"orderId":"T2","latitude":200,"longitude":77.2090,
            "address":"X","packageWeight":15,"priority":"HIGH"}]}""";
        mockMvc.perform(post("/api/dispatch/orders").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    void invalidPriorityIsRejected() throws Exception {
        String body = """
            {"orders":[{"orderId":"T3","latitude":28.6,"longitude":77.2,
            "address":"X","packageWeight":15,"priority":"URGENT"}]}""";
        mockMvc.perform(post("/api/dispatch/orders").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void emptyVehicleListIsRejected() throws Exception {
        mockMvc.perform(post("/api/dispatch/vehicles").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"vehicles\":[]}"))
                .andExpect(status().isBadRequest());
    }
}