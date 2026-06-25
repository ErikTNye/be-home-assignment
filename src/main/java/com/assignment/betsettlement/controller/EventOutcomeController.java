package com.assignment.betsettlement.controller;

import com.assignment.betsettlement.dto.EventOutcomeRequest;
import com.assignment.betsettlement.kafka.EventOutcomeProducer;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event-outcomes")
public class EventOutcomeController {

    private final EventOutcomeProducer producer;

    public EventOutcomeController(EventOutcomeProducer producer) {
        this.producer = producer;
    }

    @PostMapping
    public ResponseEntity<String> publishOutcome(@Valid @RequestBody EventOutcomeRequest request) {
        producer.publish(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body("Event outcome accepted for event " + request.eventId());
    }
}
