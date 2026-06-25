package com.assignment.betsettlement.dto;

import com.assignment.betsettlement.domain.BetStatus;
import java.util.UUID;

public record BetSettlementMessage(UUID betId, Long eventId, BetStatus result) {}
