package io.github.fernandapcaetano.phastfin_backend.statement.domain.repository;

import io.github.fernandapcaetano.phastfin_backend.statement.domain.entity.Insight;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.value_object.StatementId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IInsightRepository extends JpaRepository<Insight, Long> {
    Optional<Insight> findByStatementId(StatementId statementId);
}
