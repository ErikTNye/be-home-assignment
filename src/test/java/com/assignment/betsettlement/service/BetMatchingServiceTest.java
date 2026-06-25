package com.assignment.betsettlement.service;

import com.assignment.betsettlement.domain.Bet;
import com.assignment.betsettlement.domain.BetStatus;
import com.assignment.betsettlement.dto.BetSettlementMessage;
import com.assignment.betsettlement.dto.EventOutcomeRequest;
import com.assignment.betsettlement.repository.BetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BetMatchingServiceTest {

    @Mock
    private BetRepository betRepository;

    @InjectMocks
    private BetMatchingService betMatchingService;

    @Test
    void marksBetOnTheWinnerAsWonAndOthersAsLost() {
        EventOutcomeRequest outcome = new EventOutcomeRequest(1001L, "Team A vs Team B", 5001L);
        Bet winningBet = bet(1001L, 5001L);
        Bet losingBet = bet(1001L, 5002L);
        when(betRepository.findByEventIdAndStatus(1001L, BetStatus.PENDING))
                .thenReturn(List.of(winningBet, losingBet));

        List<BetSettlementMessage> result = betMatchingService.resolveSettlements(outcome);

        assertThat(result).containsExactly(
                new BetSettlementMessage(winningBet.getId(), 1001L, BetStatus.WON),
                new BetSettlementMessage(losingBet.getId(), 1001L, BetStatus.LOST));
    }

    @Test
    void queriesByEventIdAndPendingStatusSoSettledBetsAreExcluded() {
        EventOutcomeRequest outcome = new EventOutcomeRequest(1002L, "Team C vs Team D", 6001L);
        when(betRepository.findByEventIdAndStatus(1002L, BetStatus.PENDING))
                .thenReturn(List.of());

        List<BetSettlementMessage> result = betMatchingService.resolveSettlements(outcome);

        assertThat(result).isEmpty();
        // The matching service must only ever ask for PENDING bets — already-settled
        // bets (WON/LOST) must never be re-matched on a duplicate/replayed outcome.
        verify(betRepository).findByEventIdAndStatus(1002L, BetStatus.PENDING);
    }

    private Bet bet(Long eventId, Long eventWinnerId) {
        return Bet.builder()
                .id(UUID.randomUUID())
                .userId(101L)
                .eventId(eventId)
                .eventMarketId(1L)
                .eventWinnerId(eventWinnerId)
                .betAmount(new BigDecimal("50.00"))
                .status(BetStatus.PENDING)
                .build();
    }
}
