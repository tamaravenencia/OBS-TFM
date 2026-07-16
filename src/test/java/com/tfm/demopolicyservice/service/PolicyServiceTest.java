package com.tfm.demopolicyservice.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PolicyServiceTest {

    private final PolicyService policyService = new PolicyService();

    @Test
    void shouldReturnAllPolicies() {
        var policies = policyService.findAll();

        assertEquals(3, policies.size());
    }

    @Test
    void shouldFindPolicyByIdIgnoringCase() {
        var policy = policyService.findById("pol-001");

        assertTrue(policy.isPresent());
        assertEquals("POL-001", policy.get().id());
    }

    @Test
    void shouldReturnEmptyWhenPolicyDoesNotExist() {
        var policy = policyService.findById("POL-999");

        assertTrue(policy.isEmpty());
    }
}