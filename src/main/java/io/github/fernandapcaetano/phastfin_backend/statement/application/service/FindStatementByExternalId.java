package io.github.fernandapcaetano.phastfin_backend.statement.application.service;

import io.github.fernandapcaetano.phastfin_backend.commons.application.Errors;
import io.github.fernandapcaetano.phastfin_backend.commons.application.Result;
import io.github.fernandapcaetano.phastfin_backend.statement.application.dto.FindStatementResponse;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.entity.Insight;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.repository.IInsightRepository;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.repository.IStatementRepository;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.repository.ITransactionRepository;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.repository.IUserRepository;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.value_object.StatementId;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.value_object.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FindStatementByExternalId {
    private final IUserRepository userRepository;
    private final IInsightRepository insightRepository;
    private final ITransactionRepository transactionRepository;
    private final IStatementRepository statementRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(FindStatementByExternalId.class);

    public FindStatementByExternalId(IUserRepository userRepository,
                                     IInsightRepository insightRepository,
                                     ITransactionRepository transactionRepository,
                                     IStatementRepository statementRepository) {
        this.userRepository = userRepository;
        this.insightRepository = insightRepository;
        this.transactionRepository = transactionRepository;
        this.statementRepository = statementRepository;
    }

    public Result<FindStatementResponse> execute(String userEmail ,UUID statementExternalId){

        if (userEmail == null || userEmail.isBlank())
            return Result.failure(401, Errors.SOMETHING_WENT_WRONG.getMessage());

        var userIdByEmail = userRepository.findIdByEmail(userEmail);
        if (userIdByEmail == null)
            return Result.failure(401, Errors.USER_DOES_NOT_EXISTS.getMessage());
        var userId = new UserId(userIdByEmail);

        var isStatement = statementRepository.findByUserIdAndExternalId(userId.id(), statementExternalId);
        if (isStatement.isEmpty())
            return Result.failure(400, Errors.INVALID_STATEMENT.getMessage());
        var statement = isStatement.get();
        var statementId = new StatementId(statement.getId());

        var isInsight = insightRepository.findByStatementId(statementId);
        Insight insight = null;
        if (isInsight.isPresent())
            insight = isInsight.get();

        var transactionQuantity = transactionRepository
                .countByStatementId(statementId);

        assert insight != null;
        var response = FindStatementResponse.toDto(statement, transactionQuantity, insight);

        return Result.success(response);
    }
}
