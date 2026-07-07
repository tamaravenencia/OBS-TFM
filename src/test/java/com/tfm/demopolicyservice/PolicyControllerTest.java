package com.tfm.demopolicyservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void policiesEndpointShouldReturnPolicyList() throws Exception {
        mockMvc.perform(get("/api/policies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("POL-001"))
                .andExpect(jsonPath("$[0].holder").value("Cliente Demo 1"));
    }

    @Test
    void policyByIdShouldReturnPolicyWhenExists() throws Exception {
        mockMvc.perform(get("/api/policies/POL-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("POL-001"))
                .andExpect(jsonPath("$.type").value("Auto"));
    }

    @Test
    void policyByIdShouldReturnNotFoundWhenPolicyDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/policies/POL-999"))
                .andExpect(status().isNotFound());
    }
}