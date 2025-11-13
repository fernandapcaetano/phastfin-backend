package io.github.fernandapcaetano.phastfin_backend.statement.domain.entity;

import io.github.fernandapcaetano.phastfin_backend.commons.domain.Base;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.value_object.OrganizationId;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.value_object.UserId;
import jakarta.persistence.*;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Entity
public class Statement extends Base {
    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "user_id", nullable = false))
    private final UserId userId;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "organization_id", nullable = false))
    private final OrganizationId organizationId;

    @Column(nullable = true)
    private final String account;

    @Column(nullable = false)
    private final String currency;

    @Column(nullable = false)
    private final LocalDateTime initialDate;

    @Column(nullable = false)
    private LocalDateTime finalDate;

    protected Statement(){
        this.userId = null;
        this.organizationId = null;
        this.account = null;
        this.currency = null;
        this.initialDate = null;
    }

    private Statement(UserId userId, OrganizationId organizationId,
                     String account, String currency,
                      LocalDateTime initialDate, LocalDateTime finalDate) {
        super();
        this.userId = userId;
        this.organizationId = organizationId;
        this.account = account;
        this.currency = currency;
        this.initialDate = initialDate;
        this.finalDate = finalDate;
    }

    public static Statement create(UserId userId, OrganizationId organizationId,
                                                           String account, String currency,
                                   LocalDateTime initialDate, LocalDateTime finalDate) {
        Assert.notNull(userId, "Statement userId cannot be null");
        Assert.notNull(userId.id(), "Statement userId cannot be null");

        Assert.notNull(organizationId, "OrganizationId cannot be null");
        Assert.notNull(organizationId.id(), "OrganizationId.id cannot be null");

        Assert.hasText(currency, "Currency cannot be null or empty");
        Assert.notNull(initialDate, "Initial date cannot be null");
        Assert.notNull(finalDate, "Final date cannot be null");
        Assert.isTrue(initialDate.isBefore(finalDate), "Initial date must be before final date");

        if (account.contains("/")) {
            account = account.substring(account.lastIndexOf("/") + 1);
        }

        return new Statement(userId, organizationId, account, currency, initialDate, finalDate);
    }

    public void updateFinalDate(LocalDateTime finalDate){
        Assert.notNull(finalDate, "Final date cannot be null");
        this.finalDate = finalDate;
    }

    public UserId getUserId() { return userId; }
    public OrganizationId getOrganizationId() { return organizationId; }
    public String getAccount() { return account; }
    public String getCurrency() { return currency; }
    public LocalDateTime getInitialDate() { return initialDate; }
    public LocalDateTime getFinalDate() { return finalDate; }
}
