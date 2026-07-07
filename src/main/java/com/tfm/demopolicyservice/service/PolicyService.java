package com.tfm.demopolicyservice.service;

import com.tfm.demopolicyservice.model.Policy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PolicyService {

    private final List<Policy> policies = List.of(
            new Policy("POL-001", "Cliente Demo 1", "Auto", "ACTIVE"),
            new Policy("POL-002", "Cliente Demo 2", "Hogar", "ACTIVE"),
            new Policy("POL-003", "Cliente Demo 3", "Vida", "INACTIVE")
    );

    public List<Policy> findAll() {
        return policies;
    }

    public Optional<Policy> findById(String id) {
        return policies.stream()
                .filter(policy -> policy.id().equalsIgnoreCase(id))
                .findFirst();
    }
}