package com.assignment.betsettlement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EventOutcomeRequest(
        @NotNull Long eventId,
        @NotBlank String eventName,
        @NotNull Long eventWinnerId
) {}
