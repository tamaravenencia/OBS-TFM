package com.tfm.demopolicyservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class VersionController {

    @Value("${app.version:1.0.0}")
    private String version;

    @Value("${spring.application.name:demo-policy-service}")
    private String appName;

    @GetMapping("/api/version")
    public Map<String, String> getVersion() {
        return Map.of(
                "application", appName,
                "version", version
        );
    }
}