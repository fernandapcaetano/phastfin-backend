package io.github.fernandapcaetano.phastfin_backend.api.statement;

import io.github.fernandapcaetano.phastfin_backend.commons.application.Result;
import io.github.fernandapcaetano.phastfin_backend.statement.application.dto.StatementResponse;
import io.github.fernandapcaetano.phastfin_backend.statement.application.service.CreateStatement;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/statement")
@Tag(name = "Statement")
public class CreateStatementController {
    private final CreateStatement service;

    public CreateStatementController(CreateStatement service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload a bank statement file and extract transactions",
            description = "Accepts a bank statement file (PDF or text-based document) and processes it "
                    + "using AI to read, interpret, and extract the contained financial transactions. "
                    + "The extracted data is then stored in the database and a summary response is returned."
    )
    public ResponseEntity<Result<StatementResponse>> execute(
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        var fileStream = file.getInputStream();
        var userEmail = "usuario@example.com";
        var organizationExternalId = UUID.fromString("9a7b1f21-8a4b-4c58-9b32-8b7e73e1a2d4");
        var result = service.execute(userEmail, organizationExternalId, fileStream);
        if (!result.isSuccess())
            return ResponseEntity.status(result.getErrorCode()).body(result);
        return ResponseEntity.ok(result);
    }
}
