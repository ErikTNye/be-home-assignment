package com.assignment.betsettlement.kafka;

import com.assignment.betsettlement.dto.EventOutcomeRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventOutcomeProducer {

    private static final String TOPIC = "event-outcomes";

    private final KafkaTemplate<String, EventOutcomeRequest> kafkaTemplate;

    public EventOutcomeProducer(KafkaTemplate<String, EventOutcomeRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(EventOutcomeRequest outcome) {
        kafkaTemplate.send(TOPIC, outcome.eventId().toString(), outcome);
    }
}
