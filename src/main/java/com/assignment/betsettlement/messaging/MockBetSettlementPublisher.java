package com.assignment.betsettlement.messaging;

import com.assignment.betsettlement.dto.BetSettlementMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Stands in for a real RocketMQ producer. It logs the payload
 * and then hands the message straight to the consumer-side settlement code in-process,
 * so the producer->consumer->settle shape is real even though the transport is faked.
 */
@Slf4j
@Component
@Profile("mock")
public class MockBetSettlementPublisher implements BetSettlementPublisher {

    private static final String TOPIC = "bet-settlements";

    private final BetSettlementConsumer betSettlementConsumer;

    public MockBetSettlementPublisher(BetSettlementConsumer betSettlementConsumer) {
        this.betSettlementConsumer = betSettlementConsumer;
    }

    @Override
    public void publish(BetSettlementMessage message) {
        log.info("MOCK ROCKETMQ PUBLISH -> topic={}, payload={}", TOPIC, message);
        betSettlementConsumer.onMessage(message);
    }
}
