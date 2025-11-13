package io.github.fernandapcaetano.phastfin_backend.statement.application.service;

import io.github.fernandapcaetano.phastfin_backend.commons.application.Errors;
import io.github.fernandapcaetano.phastfin_backend.commons.application.PageResponse;
import io.github.fernandapcaetano.phastfin_backend.commons.application.Result;
import io.github.fernandapcaetano.phastfin_backend.statement.application.dto.TransactionResponse;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.entity.Statement;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.entity.Transaction;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.enumerator.TransactionCategory;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.enumerator.TransactionType;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.repository.IOrganizationRepository;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.repository.IStatementRepository;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.repository.ITransactionRepository;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.repository.IUserRepository;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class FindTransaction {

    private final IUserRepository userRepository;
    private final IStatementRepository statementRepository;
    private final ITransactionRepository transactionRepository;
    private final IOrganizationRepository organizationRepository;

    public FindTransaction(IUserRepository userRepository, IStatementRepository statementRepository, ITransactionRepository transactionRepository, IOrganizationRepository organizationRepository) {
        this.userRepository = userRepository;
        this.statementRepository = statementRepository;
        this.transactionRepository = transactionRepository;
        this.organizationRepository = organizationRepository;
    }

    public Result<PageResponse<List<TransactionResponse>>> execute(String userEmail, UUID statementExternalId, UUID organizationExternalId,
                                                                   TransactionCategory category, TransactionType type,
                                                                   LocalDate dateFrom, LocalDate dateTo, String name, String description,
                                                                   BigDecimal greaterThan, BigDecimal lessThan,
                                                                   int pageNumber, int pageSize){
        if (pageNumber <= 0 || pageSize <= 0)
            return Result.failure(400, Errors.INVALID_PAGE_NUMBER_SIZE.getMessage());

        if (userEmail == null || userEmail.isBlank())
            return Result.failure(401, Errors.USER_DOES_NOT_EXISTS.getMessage());

        var userId = userRepository.findIdByEmail(userEmail);
        if (userId == null)
            return Result.failure(401, Errors.USER_DOES_NOT_EXISTS.getMessage());

        Long statementId = null;
        if (statementExternalId != null) {
            var existStatement = statementRepository
                    .findIdByStatementExternalId(userId, statementExternalId);
            if (existStatement.isEmpty())
                return Result.failure(400, Errors.INVALID_STATEMENT.getMessage());
            statementId = existStatement.get();
        }

        LocalDateTime dateFromDateTime = null;
        if (dateFrom != null)
            dateFromDateTime = dateFrom.atStartOfDay();

        LocalDateTime dateToDateTime = null;
        if (dateTo != null)
            dateToDateTime = dateTo.atStartOfDay();

        Long organizatonId = null;
        if (organizationExternalId != null){
            var organization = organizationRepository.findIdByExternalId(organizationExternalId);
            if (organization.isEmpty())
                return Result.failure(400, Errors.ORGANIZATION_DOES_NOT_EXISTS.getMessage());
            organizatonId = organization.get();
        }

        var specification = filterTransactions(userId, statementId, organizatonId,
                category, type, dateFromDateTime, dateToDateTime, name, description,
                greaterThan, lessThan);
        var page = PageRequest.of(pageNumber-1, pageSize, Sort.by("data").descending());
        var found = transactionRepository.findAll(specification, page);
        var edits = found.stream()
                .map(Transaction::getReferenceOriginalId)
                .filter(Objects::nonNull)
                .toList();

        var transactionResponses = found.stream()
                .map(t -> {
                    if (edits.contains(t.getId()))
                        return null;
                    return new TransactionResponse(
                            TransactionCategory.valueOf(t.getCategory()),
                            t.getType(),
                            t.getName(),
                            t.getDescription(),
                            t.getData(),
                            t.getValue()
                    );
                })
                .filter(Objects::nonNull)
                .toList();

        var totalElements = found.getTotalElements();
        var totalPages = found.getTotalPages();
        var isLast = found.isLast();
        var isFirst = found.isFirst();

        var response = new PageResponse<>(transactionResponses, pageNumber,
                pageSize, totalElements, totalPages, isLast, isFirst);

        return Result.success(response);
    }


    public Specification<Transaction> filterTransactions(
            Long userId,
            Long statementId,
            Long organizationId,
            TransactionCategory category,
            TransactionType type,
            LocalDateTime dateFrom,
            LocalDateTime dateTo,
            String name,
            String description,
            BigDecimal greaterThan,
            BigDecimal lessThan
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 🔹 Subquery para filtrar Statements do usuário (e opcionalmente da organização)
            Subquery<Long> statementSub = query.subquery(Long.class);
            Root<Statement> stRoot = statementSub.from(Statement.class);
            statementSub.select(stRoot.get("id"));

            List<Predicate> statementPredicates = new ArrayList<>();
            statementPredicates.add(cb.equal(stRoot.get("userId").get("id"), userId));

            if (organizationId != null) {
                statementPredicates.add(cb.equal(stRoot.get("organizationId").get("id"), organizationId));
            }

            statementSub.where(cb.and(statementPredicates.toArray(new Predicate[0])));

            // 🔹 Transaction deve pertencer a um Statement do usuário (e org, se passado)
            predicates.add(root.get("statementId").get("id").in(statementSub));

            // 🔹 Statement específico (opcional)
            if (statementId != null) {
                predicates.add(cb.equal(root.get("statementId").get("id"), statementId));
            }

            // 🔹 Categoria
            if (category != null) {
                predicates.add(cb.equal(root.get("category"), category));
            }

            // 🔹 Tipo
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }

            // 🔹 Intervalo de datas
            if (dateFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("data"), dateFrom));
            }
            if (dateTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("data"), dateTo));
            }

            // 🔹 Nome / Descrição (like case-insensitive)
            if (name != null && !name.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (description != null && !description.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%"));
            }

            // 🔹 Faixa de valores
            if (greaterThan != null) {
                predicates.add(cb.greaterThan(root.get("value"), greaterThan));
            }
            if (lessThan != null) {
                predicates.add(cb.lessThan(root.get("value"), lessThan));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }


}

