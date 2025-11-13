package io.github.fernandapcaetano.phastfin_backend.api.statement;

import io.github.fernandapcaetano.phastfin_backend.commons.application.PageResponse;
import io.github.fernandapcaetano.phastfin_backend.commons.application.Result;
import io.github.fernandapcaetano.phastfin_backend.statement.application.dto.TransactionResponse;
import io.github.fernandapcaetano.phastfin_backend.statement.application.service.FindTransaction;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.enumerator.TransactionCategory;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.enumerator.TransactionType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/statement")
@Tag(name = "Statement")
public class FindTransactionController {

    private final FindTransaction service;

    public FindTransactionController(FindTransaction service) {
        this.service = service;
    }

    @GetMapping("/transaction")
    @Operation(
            summary = "Retrieve transactions using filters and pagination",
            description = "Returns a paginated list of financial transactions filtered by the provided optional parameters. "
                    + "Clients may filter by statement ID, category, type, date range, name, description, and value range. "
                    + "If no filters are provided, all transactions accessible to the user are returned. "
                    + "The response includes pagination metadata along with the list of matching transactions."
    )
    public ResponseEntity<Result<PageResponse<List<TransactionResponse>>>> execute(
            @RequestParam(required = false) UUID statementExternalId,
            @RequestParam(required = false) TransactionCategory category,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) BigDecimal greaterThan,
            @RequestParam(required = false) BigDecimal lessThan,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ){
        var userEmail = "usuario@example.com";
        var result = service.execute(userEmail, statementExternalId, null,
        category, type, dateFrom, dateTo, name, description, greaterThan, lessThan,pageNumber, pageSize);
        if (!result.isSuccess())
            return ResponseEntity.status(result.getErrorCode()).body(result);
        return ResponseEntity.ok(result);
    }
}
