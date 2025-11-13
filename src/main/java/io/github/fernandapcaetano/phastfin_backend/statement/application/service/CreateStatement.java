package io.github.fernandapcaetano.phastfin_backend.statement.application.service;

import io.github.fernandapcaetano.phastfin_backend.commons.application.Errors;
import io.github.fernandapcaetano.phastfin_backend.commons.application.Result;
import io.github.fernandapcaetano.phastfin_backend.statement.application.dto.StatementResponse;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.entity.Insight;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.entity.Statement;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.entity.Transaction;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.repository.*;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.service.IAgentChat;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.value_object.OrganizationId;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.value_object.StatementId;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.value_object.UserId;
import io.github.fernandapcaetano.phastfin_backend.statement.infrastructure.agent.service.BankStatementResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@Service
public class CreateStatement {
    private final IAgentChat<BankStatementResponse> agent;
    private final IUserRepository userRepository;
    private final IStatementRepository statementRepository;
    private final ITransactionRepository transactionRepository;
    private final IInsightRepository insightRepository;
    private final IOrganizationRepository organizationRepository;

    public CreateStatement(IAgentChat<BankStatementResponse> agent,
                           IUserRepository userRepository,
                           IStatementRepository statementRepository,
                           ITransactionRepository transactionRepository,
                           IInsightRepository iInsightRepository, IOrganizationRepository organizationRepository) {
        this.agent = agent;
        this.userRepository = userRepository;
        this.statementRepository = statementRepository;
        this.transactionRepository = transactionRepository;
        this.insightRepository = iInsightRepository;
        this.organizationRepository = organizationRepository;
    }

    @Transactional
    public Result<StatementResponse> execute(String userEmail, UUID organizationExternalId, InputStream file){

        if (userEmail == null || userEmail.isBlank())
            return Result.failure(401, Errors.SOMETHING_WENT_WRONG.getMessage());
        var userIdByEmail = userRepository.findIdByEmail(userEmail);

        if (userIdByEmail == null)
            return Result.failure(401, Errors.USER_DOES_NOT_EXISTS.getMessage());

        var fieldErrors = new HashMap<String, String>();

        var organizationIdFromRequest = organizationRepository.findIdByExternalId(organizationExternalId);
        if (organizationIdFromRequest.isEmpty())
            fieldErrors.put("organizationExternalId", Errors.INVALID_VALUE.getMessage());

        var statementFromAgent = agent.analyzeBankStatement(file);
        if (statementFromAgent == null)
            fieldErrors.put("file", Errors.INVALID_FILE.getMessage());

        if(!fieldErrors.isEmpty())
            return Result.fieldErrors(fieldErrors, 404);

        var organizationId = new OrganizationId(organizationIdFromRequest.get());
        var userId = new UserId(userIdByEmail);

        var initDate = statementFromAgent.initDate();
        var account = statementFromAgent.account();
        var statementWihSameInitDate = statementRepository
                .existsStatementWithInitDate(initDate, userIdByEmail, account);

        var currency = statementFromAgent.currency();
        var initialDate = statementFromAgent.initDate();
        var finalDate = statementFromAgent.finalDate();

        var statementExisted = false;
        Statement statement = null;
        if (statementWihSameInitDate.isPresent()) {
            statementExisted = true;
            statement = statementWihSameInitDate.get();
            statement.updateFinalDate(statementFromAgent.finalDate());
        } else{

            statement = Statement.create(userId, organizationId,
                    account, currency, initialDate, finalDate);
        }
        var savedStatement = statementRepository.save(statement);
        var statementId = new StatementId(savedStatement.getId());

        var transactions = new ArrayList<Transaction>();
        statementFromAgent.transactions()
                .forEach(t -> transactions.add(
                        Transaction.create(statementId, t.category(),
                                t.type(), t.name(), t.description(), t.date(), t.amount())
                ));

        transactionRepository.saveAll(transactions);
        transactionRepository.deleteDuplicates(statementId.id());

        var insightFromAgent = agent.insightStatement(transactions, userId, statementId);
        if (insightFromAgent == null)
            return Result.failure(500, Errors.SOMETHING_WENT_WRONG.getMessage());

        Insight insight = null;
        if (statementExisted){
            var isInsight = insightRepository.findByStatementId(statementId);
            if (isInsight.isEmpty())
                return Result.failure(500, Errors.SOMETHING_WENT_WRONG.getMessage());
            insight = isInsight.get();
            insight.updateStatementInsight(insightFromAgent.getPraise(), insightFromAgent.getCritic());
        } else {
            insight = insightFromAgent;
        }

        insightRepository.save(insight);
        agent.createEmbedding(statementFromAgent, userId, statementId);

        var externalId = savedStatement.getExternalId();
        return Result.success("Statement created successfully.",
                new StatementResponse(externalId, account, currency,
                        initialDate, finalDate, transactions.size()));
    }
}
