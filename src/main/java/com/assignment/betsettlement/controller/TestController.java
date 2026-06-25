package com.assignment.betsettlement.controller;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public TestController(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/ping")
    public String ping() {
        kafkaTemplate.send("test-topic", "hello");
        return "sent";
    }
}
