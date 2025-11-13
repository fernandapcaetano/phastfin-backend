package io.github.fernandapcaetano.phastfin_backend.statement.domain.repository;

import io.github.fernandapcaetano.phastfin_backend.statement.domain.entity.Statement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IStatementRepository extends JpaRepository<Statement, Long>, JpaSpecificationExecutor<Statement> {

    @Query("""
    SELECT s
    FROM Statement s
    WHERE s.userId.id = :userId
      AND s.account = :account
      AND s.initialDate = :initDate
    """)
    Optional<Statement> existsStatementWithInitDate(
            @Param("initDate") LocalDateTime initDate,
            @Param("userId") Long userId,
            @Param("account") String account);

    @Query("""
    SELECT s
    FROM Statement s
    WHERE s.userId.id = :userId
        AND s.externalId = :externalId
    """)
    Optional<Statement> findByUserIdAndExternalId(
            @Param("userId") Long userId,
            @Param("externalId") UUID statementExternalId);

    @Query("""
    SELECT s.id
    FROM Statement s
    WHERE s.userId.id = :userId
        AND s.externalId = :externalId
    """)
    Optional<Long> findIdByStatementExternalId(@Param("userId") Long userId,
                                               @Param("externalId") UUID statementExternalId);
}
