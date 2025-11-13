package io.github.fernandapcaetano.phastfin_backend.statement.domain.repository;

import io.github.fernandapcaetano.phastfin_backend.statement.domain.entity.Transaction;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.value_object.StatementId;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.value_object.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ITransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    @Modifying
    @Query(value = """
        WITH duplicates AS (
            SELECT
                t.statement_id,
                ROW_NUMBER() OVER (
                    PARTITION BY t.name, t.description, t.value, t.category, t.data
                    ORDER BY t.data DESC, t.statement_id DESC
                ) AS rn
            FROM transaction t
            WHERE t.statement_id = :statementId
            AND t.reference_original_id = NULL
        )
        DELETE FROM transaction
        WHERE statement_id IN (
            SELECT statement_id
            FROM duplicates
            WHERE rn > 1
        );
    """, nativeQuery = true)
    void deleteDuplicates(@Param("statementId") Long statementId);

    int countByStatementId(StatementId statementId);

    @Query("""
        SELECT t.statementId.id, COUNT(t.id)
        FROM Transaction t
        WHERE t.statementId.id IN :statementIds
        GROUP BY t.statementId.id
    """)
    List<Object[]> countByStatementIdsGrouped(@Param("statementIds") List<Long> statementIds);

}
