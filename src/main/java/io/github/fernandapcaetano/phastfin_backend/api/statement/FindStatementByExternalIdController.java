package io.github.fernandapcaetano.phastfin_backend.api.statement;

import io.github.fernandapcaetano.phastfin_backend.commons.application.Result;
import io.github.fernandapcaetano.phastfin_backend.statement.application.dto.FindStatementResponse;
import io.github.fernandapcaetano.phastfin_backend.statement.application.service.FindStatementByExternalId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/statement")
@Tag(name = "Statement")
public class FindStatementByExternalIdController {
    private final FindStatementByExternalId service;

    public FindStatementByExternalIdController(FindStatementByExternalId service) {
        this.service = service;
    }

    @GetMapping("/{statementExternalId}")
    @Operation(
            summary = "Retrieve a bank statement by external ID",
            description = "Fetches a previously processed and stored bank statement using its external identifier. "
                    + "The method returns detailed information about the statement, including metadata and "
                    + "any extracted transactions associated with it."
    )
    public ResponseEntity<Result<FindStatementResponse>> execute(
            @PathVariable("statementExternalId") UUID statementExternalId
    ){
        var userEmail = "usuario@example.com";
        var result = service.execute(userEmail, statementExternalId);
        if (!result.isSuccess())
            return ResponseEntity.status(result.getErrorCode()).body(result);
        return ResponseEntity.ok(result);
    }
}
