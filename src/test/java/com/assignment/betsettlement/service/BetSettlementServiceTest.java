package com.assignment.betsettlement.service;

import com.assignment.betsettlement.domain.Bet;
import com.assignment.betsettlement.domain.BetStatus;
import com.assignment.betsettlement.dto.BetSettlementMessage;
import com.assignment.betsettlement.exception.BetNotFoundException;
import com.assignment.betsettlement.repository.BetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BetSettlementServiceTest {

    @Mock
    private BetRepository betRepository;

    @InjectMocks
    private BetSettlementService betSettlementService;

    @Test
    void settlesBetAsWon() {
        Bet bet = pendingBet();
        when(betRepository.findById(bet.getId())).thenReturn(Optional.of(bet));

        betSettlementService.settle(new BetSettlementMessage(bet.getId(), 1001L, BetStatus.WON));

        assertThat(bet.getStatus()).isEqualTo(BetStatus.WON);
        verify(betRepository).save(bet);
    }

    @Test
    void settlesBetAsLost() {
        Bet bet = pendingBet();
        when(betRepository.findById(bet.getId())).thenReturn(Optional.of(bet));

        betSettlementService.settle(new BetSettlementMessage(bet.getId(), 1001L, BetStatus.LOST));

        assertThat(bet.getStatus()).isEqualTo(BetStatus.LOST);
        verify(betRepository).save(bet);
    }

    @Test
    void throwsWhenBetDoesNotExist() {
        UUID missingId = UUID.randomUUID();
        when(betRepository.findById(missingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                betSettlementService.settle(new BetSettlementMessage(missingId, 1001L, BetStatus.WON)))
                .isInstanceOf(BetNotFoundException.class);
        verify(betRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    private Bet pendingBet() {
        return Bet.builder()
                .id(UUID.randomUUID())
                .userId(101L)
                .eventId(1001L)
                .eventMarketId(1L)
                .eventWinnerId(5001L)
                .betAmount(new BigDecimal("50.00"))
                .status(BetStatus.PENDING)
                .build();
    }
}
