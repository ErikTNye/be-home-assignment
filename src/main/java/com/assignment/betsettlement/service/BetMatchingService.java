package com.assignment.betsettlement.service;

import com.assignment.betsettlement.domain.Bet;
import com.assignment.betsettlement.domain.BetStatus;
import com.assignment.betsettlement.dto.BetSettlementMessage;
import com.assignment.betsettlement.dto.EventOutcomeRequest;
import com.assignment.betsettlement.repository.BetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class BetMatchingService {

    private final BetRepository betRepository;

    public BetMatchingService(BetRepository betRepository) {
        this.betRepository = betRepository;
    }

    /**
     * Finds the bets eligible for settlement for the given event outcome, and resolves
     * each one's result (WON/LOST) by comparing its eventWinnerId against the outcome's.
     * Matching is by event ID alone, regardless of market. Only PENDING bets are
     * considered, so already-settled bets are never re-matched on a duplicate or
     * replayed outcome.
     */
    public List<BetSettlementMessage> resolveSettlements(EventOutcomeRequest outcome) {
        return findPendingByEventId(outcome.eventId()).stream()
                .map(bet -> new BetSettlementMessage(
                        bet.getId(),
                        bet.getEventId(),
                        Objects.equals(bet.getEventWinnerId(), outcome.eventWinnerId())
                                ? BetStatus.WON
                                : BetStatus.LOST)
                )
                .toList();
    }

    private List<Bet> findPendingByEventId(Long eventId) {
        return betRepository.findByEventIdAndStatus(eventId, BetStatus.PENDING);
    }
}
