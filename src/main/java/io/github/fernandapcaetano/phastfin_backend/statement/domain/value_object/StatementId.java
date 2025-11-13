package io.github.fernandapcaetano.phastfin_backend.statement.domain.value_object;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record StatementId(
        @Column(name = "statement_id") Long id
) {}
