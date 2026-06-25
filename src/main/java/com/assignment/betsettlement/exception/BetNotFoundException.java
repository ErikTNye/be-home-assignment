package com.assignment.betsettlement.exception;

import java.util.UUID;

public class BetNotFoundException extends RuntimeException {
    public BetNotFoundException(UUID betId) {
        super("No bet found for id " + betId);
    }
}
