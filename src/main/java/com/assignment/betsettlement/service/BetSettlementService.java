package com.assignment.betsettlement.service;

import com.assignment.betsettlement.domain.Bet;
import com.assignment.betsettlement.domain.BetStatus;
import com.assignment.betsettlement.dto.BetSettlementMessage;
import com.assignment.betsettlement.exception.BetNotFoundException;
import com.assignment.betsettlement.repository.BetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BetSettlementService {

    private final BetRepository betRepository;

    public BetSettlementService(BetRepository betRepository) {
        this.betRepository = betRepository;
    }

    public void settle(BetSettlementMessage message) {
        Bet bet = betRepository.findById(message.betId())
                .orElseThrow(() -> new BetNotFoundException(message.betId()));

        BetStatus previousStatus = bet.getStatus();
        bet.setStatus(message.result());
        betRepository.save(bet);

        log.info("Settled bet {} (userId {}): {} -> {}",
                bet.getId(), bet.getUserId(), previousStatus, bet.getStatus());
    }
}
