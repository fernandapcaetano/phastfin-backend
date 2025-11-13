package io.github.fernandapcaetano.phastfin_backend.statement.application.service;

import io.github.fernandapcaetano.phastfin_backend.statement.domain.entity.*;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.enumerator.TransactionCategory;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.enumerator.TransactionType;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.repository.*;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.value_object.OrganizationId;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.value_object.StatementId;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.value_object.UserId;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@SpringBootTest
@Testcontainers
public class FindStatementByExternalIdTest {

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("pgvector/pgvector:0.8.0-pg17");

    @Autowired
    private FindStatementByExternalId service;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IStatementRepository statementRepository;
    @Autowired
    private ITransactionRepository transactionRepository;
    @Autowired
    private IOrganizationRepository organizationRepository;
    @Autowired
    private IInsightRepository insightRepository;
    @Autowired
    private JdbcTemplate template;

    public record ExternalIds(
            String userEmail,
            UUID organization, UUID user,
            UUID statement, UUID insight
    ){}

    public ExternalIds setup(){
        template.execute("""
        TRUNCATE TABLE "user" RESTART IDENTITY CASCADE;
        TRUNCATE TABLE "statement" RESTART IDENTITY CASCADE;
        TRUNCATE TABLE "transaction" RESTART IDENTITY CASCADE;
        TRUNCATE TABLE "organization" RESTART IDENTITY CASCADE;
        TRUNCATE TABLE "insight" RESTART IDENTITY CASCADE;
        """);
        var organization = Organization.create("BANK", "001", null);
        organization = organizationRepository.save(organization);
        var organizationId = new OrganizationId(organization.getId());

        var userEmail = "filomenadeaucantara@gmail.com";
        var user = User.create(userEmail);
        user = userRepository.save(user);
        var userId = new UserId(user.getId());

        var statement = Statement.create(userId, organizationId,
                "123-45", "BRL",
                LocalDate.of(20225,11,1).atStartOfDay(),
                LocalDate.of(20225,11,7).atStartOfDay());
        statement = statementRepository.save(statement);
        var statementId = new StatementId(statement.getId());

        var category = TransactionCategory.FINANCIAL;
        var type = TransactionType.DEBIT;

        var transactions = List.of(
            Transaction.create(statementId, category, type, "Teste1", "Teste",
                    LocalDate.of(2025, 11,1).atStartOfDay(), new BigDecimal(1)),
            Transaction.create(statementId, category, type, "Teste2", "Teste",
                LocalDate.of(2025, 11,2).atStartOfDay(), new BigDecimal(2))
        );
        transactionRepository.saveAll(transactions);

        var insight = Insight.createStatementInsight(userId, statementId, "Good", "Bad");
        insightRepository.save(insight);

        return new ExternalIds(userEmail, organization.getExternalId(), user.getExternalId(),
                statement.getExternalId(), insight.getExternalId());
    }

    @Test
    void findStatementSuccessfully(){
        var setup = setup();
        var userEmail = setup.userEmail();
        var statement = setup.statement();
        var result = service.execute(userEmail, statement);

    }
}
