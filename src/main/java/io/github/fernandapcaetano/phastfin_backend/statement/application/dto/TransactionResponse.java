package io.github.fernandapcaetano.phastfin_backend.statement.application.dto;

import io.github.fernandapcaetano.phastfin_backend.statement.domain.enumerator.TransactionCategory;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.enumerator.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        TransactionCategory category,
        TransactionType type, String name, String description,
        LocalDateTime data, BigDecimal value
) {}
