package io.github.fernandapcaetano.phastfin_backend.statement.domain.entity;

import io.github.fernandapcaetano.phastfin_backend.commons.domain.Base;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.enumerator.TransactionCategory;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.enumerator.TransactionType;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.value_object.StatementId;
import jakarta.persistence.*;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Transaction extends Base {
    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "statement_id", nullable = false))
    private final StatementId statementId;

    @Column(nullable = false)
    private String category;

    @Enumerated(EnumType.STRING)
    private final TransactionType type;

    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime data;

    @Column(nullable = false)
    private BigDecimal value;

    @Column(nullable = true)
    private Long referenceOriginalId;

    protected Transaction(){
        this.statementId = null;
        this.type = null;
    }

    private Transaction(StatementId statementId, String category, TransactionType type,
                       String name, String description,
                        LocalDateTime data, BigDecimal value) {
        super();
        this.statementId = statementId;
        this.category = category;
        this.type = type;
        this.name = name;
        this.description = description;
        this.data = data;
        this.value = value;
        this.referenceOriginalId = null;
    }

    public static Transaction create(StatementId statementId, TransactionCategory category,
                                     TransactionType type, String name, String description,
                                     LocalDateTime data, BigDecimal value){
        Assert.notNull(statementId, "Transaction statementId cannot be null");
        Assert.notNull(statementId.id(), "Transaction statementId cannot be null");

        Assert.notNull(category, "Transaction category cannot be null");
        Assert.notNull(type, "Transaction type cannot be null");
        Assert.notNull(value, "Transaction value cannot be null");

        if (value.compareTo(BigDecimal.ZERO) > 0 && type == TransactionType.DEBIT)
            value = value.negate();

        return new Transaction(statementId, category.name(), type, name, description, data, value);
    }

    public void update(TransactionCategory category,
                       String name, String description,
                       LocalDateTime data, BigDecimal value,
                       LocalDateTime startStatementDate, LocalDateTime finalStatementDate,
                       Long referenceOriginalId){
        if (category != null){
            this.category = category.name();
            this.referenceOriginalId = referenceOriginalId;
        }

        if (name != null && !name.isBlank()){
            this.name = name;
            this.referenceOriginalId = referenceOriginalId;
        }

        if (description != null && !description.isBlank()){
            this.description = description;
            this.referenceOriginalId = referenceOriginalId;
        }

        if (data != null &&
                (data.isAfter(startStatementDate) || data.isEqual(startStatementDate)) &&
                (data.isBefore(finalStatementDate) || data.isEqual(finalStatementDate))){
            this.data = data;
            this.referenceOriginalId = referenceOriginalId;
        }


        if (value != null){
            this.value = value;
            this.referenceOriginalId = referenceOriginalId;
        }

    }

    public StatementId getStatementId() { return statementId; }
    public String getCategory() { return category; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public LocalDateTime getData() { return data; }
    public BigDecimal getValue() { return value; }

    public TransactionType getType() {
        return type;
    }

    public Long getReferenceOriginalId() {
        return referenceOriginalId;
    }
}
