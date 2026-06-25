package com.assignment.betsettlement.messaging;

import com.assignment.betsettlement.dto.BetSettlementMessage;
import com.assignment.betsettlement.service.BetSettlementService;
import org.springframework.stereotype.Component;

@Component
public class BetSettlementConsumer {

    private final BetSettlementService betSettlementService;

    public BetSettlementConsumer(BetSettlementService betSettlementService) {
        this.betSettlementService = betSettlementService;
    }

    public void onMessage(BetSettlementMessage message) {
        betSettlementService.settle(message);
    }
}
