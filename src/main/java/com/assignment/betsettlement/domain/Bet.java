package com.assignment.betsettlement.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.util.UUID;

import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Long userId;

    private Long eventId;

    private Long eventMarketId;

    private Long eventWinnerId;

    private BigDecimal betAmount;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BetStatus status = BetStatus.PENDING;
}
