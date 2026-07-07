package com.tfm.demopolicyservice.model;

public record Policy(
        String id,
        String holder,
        String type,
        String status
) {
}