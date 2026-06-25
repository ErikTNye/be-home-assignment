package com.assignment.betsettlement.config;

import com.assignment.betsettlement.domain.Bet;
import com.assignment.betsettlement.domain.BetStatus;
import com.assignment.betsettlement.repository.BetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Seeds a fixed set of PENDING bets on startup so manual testing is
 * reproducible. IDs are UUIDs generated at insert time; cross-reference rows
 * by userId when checking results.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final BetRepository betRepository;

    @Override
    public void run(String... args) {
        List<Bet> seedBets = List.of(
                bet(101L, 1001L, 1L, 5001L, "50.00"),
                bet(102L, 1001L, 1L, 5002L, "25.00"),
                bet(103L, 1002L, 1L, 6001L, "100.00"),
                bet(104L, 1002L, 2L, 6002L, "10.00"),
                bet(105L, 1003L, 1L, 7001L, "75.00"));

        betRepository.saveAll(seedBets);
        log.info("Seeded {} bets", seedBets.size());
    }

    private Bet bet(Long userId, Long eventId, Long eventMarketId, Long eventWinnerId, String betAmount) {
        return Bet.builder()
                .userId(userId)
                .eventId(eventId)
                .eventMarketId(eventMarketId)
                .eventWinnerId(eventWinnerId)
                .betAmount(new BigDecimal(betAmount))
                .status(BetStatus.PENDING)
                .build();
    }
}
