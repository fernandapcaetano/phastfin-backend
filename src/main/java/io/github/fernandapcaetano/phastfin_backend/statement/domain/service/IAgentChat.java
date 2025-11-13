package io.github.fernandapcaetano.phastfin_backend.statement.domain.service;

import io.github.fernandapcaetano.phastfin_backend.statement.domain.entity.Insight;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.entity.Transaction;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.value_object.StatementId;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.value_object.UserId;

import java.io.InputStream;
import java.util.List;

public interface IAgentChat<T> {
    T analyzeBankStatement(InputStream file);
    void createEmbedding(T statement, UserId userId, StatementId statementId);
    String findSimilarity(Long userId, String query);
    Insight insightStatement(List<Transaction> transactions, UserId userId,
                             StatementId statementId);
    Insight insightGeneral(Long userId);
}
