package io.github.fernandapcaetano.phastfin_backend.api.statement;

import io.github.fernandapcaetano.phastfin_backend.commons.application.PageResponse;
import io.github.fernandapcaetano.phastfin_backend.commons.application.Result;
import io.github.fernandapcaetano.phastfin_backend.statement.application.dto.FindStatementResponse;
import io.github.fernandapcaetano.phastfin_backend.statement.application.service.FindStatement;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/statement")
@Tag(name = "Statement")
public class FindStatementController {

    private final FindStatement service;

    public FindStatementController(FindStatement service) {
        this.service = service;
    }

    @GetMapping()
    @Operation(
            summary = "Retrieve bank statements using filters and pagination",
            description = "Returns a paginated list of bank statements based on the provided optional filters. "
                    + "Clients may filter results by account number, currency, and a date range. "
                    + "If no filters are supplied, all statements accessible to the user are returned. "
                    + "The response includes metadata such as total pages, total items, and the current page."
    )
    public ResponseEntity<Result<PageResponse<List<FindStatementResponse>>>> execute(
            @RequestParam(required = false) String account,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ){
        var userEmail = "usuario@example.com";
        var result = service.execute(userEmail, null, account, currency,
                dateFrom, dateTo, pageNumber, pageSize);
        if (!result.isSuccess())
            return ResponseEntity.status(result.getErrorCode()).body(result);
        return ResponseEntity.ok(result);
    }
}
