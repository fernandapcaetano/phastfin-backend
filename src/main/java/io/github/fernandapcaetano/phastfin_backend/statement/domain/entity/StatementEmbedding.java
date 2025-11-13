package io.github.fernandapcaetano.phastfin_backend.statement.domain.entity;

import com.pgvector.PGvector;
import io.github.fernandapcaetano.phastfin_backend.commons.domain.Base;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.value_object.StatementId;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.value_object.UserId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.util.Assert;

@Entity
public class StatementEmbedding extends Base {

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "user_id", nullable = false))
    private final UserId userId;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "statement_id", nullable = false))
    private final StatementId statementId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(columnDefinition = "vector(1536)")
    @JdbcTypeCode(SqlTypes.VECTOR)
    private float[] vector;

    protected StatementEmbedding(){
        this.userId = null;
        this.statementId = null;
    }

    private StatementEmbedding(UserId userId, StatementId statementId,
                               String content, float[] vector) {
        super();
        this.userId = userId;
        this.statementId = statementId;
        this.content = content;
        this.vector = vector;
    }

    public static  StatementEmbedding create(UserId userId, StatementId statementId,
                                             String content, float[] vector){
        Assert.notNull(statementId, "StatementEmbedding statementId cannot be null");
        Assert.notNull(statementId.id(), "StatementEmbedding statementId cannot be null");
        Assert.notNull(content, "StatementEmbedding content cannot be null");
        Assert.hasText(content, "StatementEmbedding content cannot be empty");

        Assert.notNull(vector, "StatementEmbedding vector cannot be null");
        Assert.isTrue(vector.length > 1, "StatementEmbedding vector length must be greater than 1");

        return new StatementEmbedding(userId, statementId, content, vector);
    }

    public void update(String content, float[] vector){
        Assert.notNull(content, "StatementEmbedding content cannot be null");
        Assert.hasText(content, "StatementEmbedding content cannot be empty");

        Assert.notNull(vector, "StatementEmbedding vector cannot be null");
        Assert.isTrue(vector.length > 1, "StatementEmbedding vector length must be greater than 1");

        this.content = content;
        this.vector = vector;
    }

    public StatementId getStatementId() { return statementId;}
    public String getContent() { return content; }
    public float[] getVector() { return vector; }
}
