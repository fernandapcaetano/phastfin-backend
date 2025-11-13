package io.github.fernandapcaetano.phastfin_backend.statement.domain.repository;

import io.github.fernandapcaetano.phastfin_backend.statement.domain.entity.StatementEmbedding;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.value_object.StatementId;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.value_object.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;

@Repository
public interface IStatementEmbeddingRepository extends JpaRepository<StatementEmbedding, Long> {

    @Query("""
    SELECT se
    FROM StatementEmbedding se
    WHERE se.userId = :userId
    AND se.statementId = :statementId
    """)
    Optional<StatementEmbedding> findByUserIdAndStatementId(
            @Param("userId") UserId userId,
            @Param("statementId") StatementId statementId);

    @Query(value = """
        SELECT * FROM statement_embedding
        WHERE user_id = :userId
        ORDER BY vector <-> CAST(:queryVector AS vector)
        LIMIT :limit
    """, nativeQuery = true)
    List<StatementEmbedding> findMostSimilar(
            @Param("userId") Long userId,
            @Param("queryVector") String queryVector,
            @Param("limit") int limit
    );

}
