package com.assignment.betsettlement.kafka;

import com.assignment.betsettlement.dto.BetSettlementMessage;
import com.assignment.betsettlement.dto.EventOutcomeRequest;
import com.assignment.betsettlement.messaging.BetSettlementPublisher;
import com.assignment.betsettlement.service.BetMatchingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class EventOutcomeConsumer {

    private final BetMatchingService betMatchingService;
    private final BetSettlementPublisher betSettlementPublisher;

    public EventOutcomeConsumer(BetMatchingService betMatchingService,
                                BetSettlementPublisher betSettlementPublisher) {
        this.betMatchingService = betMatchingService;
        this.betSettlementPublisher = betSettlementPublisher;
    }

    @KafkaListener(topics = "event-outcomes", groupId = "bet-settlement-group")
    public void onEventOutcome(EventOutcomeRequest outcome) {
        List<BetSettlementMessage> settlements = betMatchingService.resolveSettlements(outcome);
        log.info("Matched {} bet(s) to settle for event {}", settlements.size(), outcome.eventId());
        settlements.forEach(betSettlementPublisher::publish);
    }
}
