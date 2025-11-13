package io.github.fernandapcaetano.phastfin_backend.statement.application.dto;

import java.util.UUID;

public record StatementRequest(
        UUID organizationExternalId
) {
}
