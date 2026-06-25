package com.assignment.betsettlement.messaging;

import com.assignment.betsettlement.dto.BetSettlementMessage;

public interface BetSettlementPublisher {

    void publish(BetSettlementMessage message);
}
