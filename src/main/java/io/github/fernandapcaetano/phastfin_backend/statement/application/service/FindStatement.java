package io.github.fernandapcaetano.phastfin_backend.statement.application.service;

import io.github.fernandapcaetano.phastfin_backend.commons.application.Errors;
import io.github.fernandapcaetano.phastfin_backend.commons.application.PageResponse;
import io.github.fernandapcaetano.phastfin_backend.commons.application.Result;
import io.github.fernandapcaetano.phastfin_backend.commons.domain.Base;
import io.github.fernandapcaetano.phastfin_backend.statement.application.dto.FindStatementResponse;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.entity.Statement;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.repository.IOrganizationRepository;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.repository.IStatementRepository;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.repository.ITransactionRepository;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.repository.IUserRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FindStatement {
    private final ITransactionRepository transactionRepository;
    private final IStatementRepository statementRepository;
    private final IUserRepository userRepository;
    private final IOrganizationRepository organizationRepository;

    public FindStatement(ITransactionRepository transactionRepository,
                         IStatementRepository statementRepository, IUserRepository userRepository,
                         IOrganizationRepository organizationRepository) {
        this.transactionRepository = transactionRepository;
        this.statementRepository = statementRepository;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
    }

    public Result<PageResponse<List<FindStatementResponse>>> execute(String userEmail,
                                                                     UUID organizationExternalId,
                                                                     String account, String currency,
                                                                     LocalDate dateFrom, LocalDate dateTo,
                                                                     int pageNumber, int pageSize){
        if (pageNumber <= 0 || pageSize <= 0)
            return Result.failure(400, Errors.INVALID_PAGE_NUMBER_SIZE.getMessage());

        if (userEmail == null || userEmail.isBlank())
            return Result.failure(401, Errors.SOMETHING_WENT_WRONG.getMessage());

        var userIdByEmail = userRepository.findIdByEmail(userEmail);
        if (userIdByEmail == null)
            return Result.failure(401, Errors.USER_DOES_NOT_EXISTS.getMessage());

        Long organizationId = null;
        if (organizationExternalId != null){
            var organizationIdByExternalId = organizationRepository.findIdByExternalId(organizationExternalId);
            if (organizationIdByExternalId.isEmpty())
                return Result.failure(400, Errors.ORGANIZATION_DOES_NOT_EXISTS.getMessage());
            organizationId = organizationIdByExternalId.get();
        }

        LocalDateTime dateFromDateTime = null;
        if (dateFrom != null)
            dateFromDateTime = dateFrom.atStartOfDay();

        LocalDateTime dateToDateTime = null;
        if (dateTo != null)
            dateToDateTime = dateTo.atStartOfDay();

        var specification = filterStatements(userIdByEmail, organizationId, account,
                currency, dateFromDateTime, dateToDateTime);
        var page = PageRequest.of(pageNumber-1, pageSize, Sort.by("initialDate").descending());
        var found = statementRepository.findAll(specification, page);

        if (found.isEmpty())
            return Result.failure(404, Errors.INVALID_STATEMENT.getMessage());

        var statementIds = found.stream()
                .map(Base::getId)
                .toList();
        var transactionQuantity = getTransactionCountByStatementIds(statementIds);

        var transactionResponses = found.stream()
                .map(s -> {
                    return FindStatementResponse.toDto(s, transactionQuantity.get(s.getId()).intValue(), null);
                })
                .toList();

        var totalElements = found.getTotalElements();
        var totalPages = found.getTotalPages();
        var isLast = found.isLast();
        var isFirst = found.isFirst();

        var response = new PageResponse<>(transactionResponses, pageNumber,
                pageSize, totalElements, totalPages, isLast, isFirst);

        return Result.success(response);
    }

    private Specification<Statement> filterStatements(
            Long userId,
            Long organizationId,
            String account,
            String currency,
            LocalDateTime dateFromDateTime,
            LocalDateTime dateToDateTime
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 🔹 UserId é obrigatório — Statements devem ser do usuário
            predicates.add(cb.equal(root.get("userId").get("id"), userId));

            // 🔹 Organização (opcional)
            if (organizationId != null) {
                predicates.add(cb.equal(root.get("organizationId").get("id"), organizationId));
            }

            // 🔹 Conta (account) — like case-insensitive, se quiser buscar parcialmente
            if (account != null && !account.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("account")), "%" + account.toLowerCase() + "%"));
            }

            // 🔹 Moeda (currency) — igualdade exata
            if (currency != null && !currency.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("currency")), currency.toLowerCase()));
            }

            // 🔹 Intervalo de datas (considerando initialDate / finalDate)
            if (dateFromDateTime != null) {
                // filtra statements que terminam depois da data inicial
                predicates.add(cb.greaterThanOrEqualTo(root.get("initialDate"), dateFromDateTime));
            }
            if (dateToDateTime != null) {
                // filtra statements que começam antes da data final
                predicates.add(cb.lessThanOrEqualTo(root.get("initialDate"), dateToDateTime));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Map<Long, Long> getTransactionCountByStatementIds(List<Long> statementIds) {
        List<Object[]> results = transactionRepository.countByStatementIdsGrouped(statementIds);

        return results.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],  // statementId
                        row -> (Long) row[1]   // count
                ));
    }
}
