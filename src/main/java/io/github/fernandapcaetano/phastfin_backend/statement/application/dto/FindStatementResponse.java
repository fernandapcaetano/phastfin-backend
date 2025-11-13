package io.github.fernandapcaetano.phastfin_backend.statement.application.dto;

import io.github.fernandapcaetano.phastfin_backend.statement.domain.entity.Insight;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.entity.Statement;

import java.time.LocalDateTime;
import java.util.UUID;

public record FindStatementResponse(
        UUID externalId,
        String account,
        String currency,
        LocalDateTime initialDate,
        LocalDateTime finalDate,
        int transactionsQuantity,
        InsightResponse insight
) {
    public record InsightResponse(
            String praise,
            String critic
    ){}

    public static FindStatementResponse toDto(Statement statement, int transactionsQuantity, Insight insight){
        return new FindStatementResponse(
                statement.getExternalId(), statement.getAccount(),
                statement.getCurrency(), statement.getInitialDate(),
                statement.getFinalDate(), transactionsQuantity,
                insight == null ? null : new InsightResponse(insight.getPraise(), insight.getCritic())
        );
    }
}
