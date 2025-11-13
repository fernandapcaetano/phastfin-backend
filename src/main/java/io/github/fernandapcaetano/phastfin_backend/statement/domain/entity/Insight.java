package io.github.fernandapcaetano.phastfin_backend.statement.domain.entity;

import io.github.fernandapcaetano.phastfin_backend.commons.domain.Base;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.value_object.StatementId;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.value_object.UserId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import org.springframework.util.Assert;

@Entity
public class Insight extends Base {
    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "user_id", nullable = false))
    private final UserId userId;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "statement_id", unique = true))
    private final StatementId statementId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String praise;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String critic;

    private Insight(UserId userId, StatementId statementId, String praise, String critic) {
        this.userId = userId;
        this.statementId = statementId;
        this.praise = praise;
        this.critic = critic;
    }

    protected Insight(){
        this.userId = null;
        this.statementId = null;
    }

    public static Insight createGeneralInsight(UserId userId,
                                               String praise, String critic){
        Assert.notNull(userId, "Insight userId cannot be null");
        Assert.notNull(userId.id(), "Insight userId cannot be null");
        Assert.notNull(praise, "Insight praise cannot be null");
        Assert.hasText(praise, "Insight praise cannot be empty");
        Assert.notNull(critic, "Insight critic cannot be null");
        Assert.hasText(critic, "Insight critic cannot be empty");
        return new Insight(userId, null, praise, critic);
    }

    public static Insight createStatementInsight(UserId userId, StatementId statementId,
                                                 String praise, String critic){
        Assert.notNull(userId, "Insight userId cannot be null");
        Assert.notNull(userId.id(), "Insight userId cannot be null");
        Assert.notNull(statementId, "Insight statementId cannot be null");
        Assert.notNull(statementId.id(), "Insight statementId cannot be null");
        Assert.notNull(praise, "Insight praise cannot be null");
        Assert.hasText(praise, "Insight praise cannot be empty");
        Assert.notNull(critic, "Insight critic cannot be null");
        Assert.hasText(critic, "Insight critic cannot be empty");
        return  new Insight(userId, statementId, praise, critic);
    }

    public void updateGeneralInsight(String praise, String critic){
        Assert.isTrue(statementId == null, "Statement id must be null to update general insight");
        Assert.notNull(praise, "Insight praise cannot be null");
        Assert.hasText(praise, "Insight praise cannot be empty");
        Assert.notNull(critic, "Insight critic cannot be null");
        Assert.hasText(critic, "Insight critic cannot be empty");

        this.praise = praise;
        this.critic = critic;
    }

    public void updateStatementInsight(String praise, String critic){
        Assert.notNull(this.statementId, "Insight statementId cannot be null");

        Assert.notNull(praise, "Insight praise cannot be null");
        Assert.hasText(praise, "Insight praise cannot be empty");
        Assert.notNull(critic, "Insight critic cannot be null");
        Assert.hasText(critic, "Insight critic cannot be empty");

        this.praise = praise;
        this.critic = critic;
    }

    public UserId getUserId() { return userId; }
    public StatementId getStatementId() { return statementId; }
    public String getPraise() { return praise; }
    public String getCritic() { return critic; }
}
