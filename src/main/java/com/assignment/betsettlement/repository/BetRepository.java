package com.assignment.betsettlement.repository;

import com.assignment.betsettlement.domain.Bet;
import com.assignment.betsettlement.domain.BetStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BetRepository extends JpaRepository<Bet, UUID> {

    List<Bet> findByEventIdAndStatus(Long eventId, BetStatus status);
}
