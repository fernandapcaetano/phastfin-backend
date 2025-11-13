package io.github.fernandapcaetano.phastfin_backend.statement.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record StatementResponse(
        UUID externalId,
        String account,
        String currency,
        LocalDateTime initialDate,
        LocalDateTime finalDate,
        int transactionsQuantity
) { }
