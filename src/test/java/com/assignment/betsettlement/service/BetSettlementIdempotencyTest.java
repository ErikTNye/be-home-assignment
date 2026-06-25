package com.assignment.betsettlement.service;

import com.assignment.betsettlement.domain.Bet;
import com.assignment.betsettlement.domain.BetStatus;
import com.assignment.betsettlement.dto.BetSettlementMessage;
import com.assignment.betsettlement.dto.EventOutcomeRequest;
import com.assignment.betsettlement.repository.BetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration-style slice over the real {@link BetRepository} + H2, proving the
 * idempotency-lite guarantee end to end: a replayed outcome re-fetches only
 * PENDING bets, so already-settled bets are never re-matched or re-settled.
 */
@DataJpaTest
class BetSettlementIdempotencyTest {

    @Autowired
    private BetRepository betRepository;

    private BetMatchingService betMatchingService;
    private BetSettlementService betSettlementService;

    @BeforeEach
    void setUp() {
        betMatchingService = new BetMatchingService(betRepository);
        betSettlementService = new BetSettlementService(betRepository);
    }

    @Test
    void replayedOutcomeDoesNotReSettleAlreadySettledBets() {
        Bet winner = betRepository.save(pendingBet(1001L, 5001L));
        Bet loser = betRepository.save(pendingBet(1001L, 5002L));
        EventOutcomeRequest outcome = new EventOutcomeRequest(1001L, "Team A vs Team B", 5001L);

        // First delivery: both PENDING bets are matched and settled.
        List<BetSettlementMessage> firstPass = betMatchingService.resolveSettlements(outcome);
        firstPass.forEach(betSettlementService::settle);

        assertThat(betRepository.findById(winner.getId()).orElseThrow().getStatus())
                .isEqualTo(BetStatus.WON);
        assertThat(betRepository.findById(loser.getId()).orElseThrow().getStatus())
                .isEqualTo(BetStatus.LOST);

        // Replay of the same outcome: nothing is PENDING anymore, so nothing matches.
        List<BetSettlementMessage> replay = betMatchingService.resolveSettlements(outcome);

        assertThat(replay).isEmpty();
        // Statuses are unchanged by the replay — no re-settlement occurred.
        assertThat(betRepository.findById(winner.getId()).orElseThrow().getStatus())
                .isEqualTo(BetStatus.WON);
        assertThat(betRepository.findById(loser.getId()).orElseThrow().getStatus())
                .isEqualTo(BetStatus.LOST);
    }

    private Bet pendingBet(Long eventId, Long eventWinnerId) {
        return Bet.builder()
                .userId(101L)
                .eventId(eventId)
                .eventMarketId(1L)
                .eventWinnerId(eventWinnerId)
                .betAmount(new BigDecimal("50.00"))
                .status(BetStatus.PENDING)
                .build();
    }
}
