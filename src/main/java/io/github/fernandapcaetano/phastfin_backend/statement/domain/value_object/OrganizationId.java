package io.github.fernandapcaetano.phastfin_backend.statement.domain.value_object;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record OrganizationId(
        @Column(name = "organization_id") Long id
) { }
