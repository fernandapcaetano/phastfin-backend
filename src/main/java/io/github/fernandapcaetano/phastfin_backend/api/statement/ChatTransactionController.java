package io.github.fernandapcaetano.phastfin_backend.api.statement;

import io.github.fernandapcaetano.phastfin_backend.commons.application.Result;
import io.github.fernandapcaetano.phastfin_backend.statement.application.service.ChatTransaction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statement/chat")
@Tag(name = "IA Chat")
public class ChatTransactionController {

    private final ChatTransaction service;

    public ChatTransactionController(ChatTransaction service) {
        this.service = service;
    }

    @PostMapping()
    @Operation(
            summary = "Process a user transaction query",
            description = "Receives a text query from the user containing information about "
                    + "their previously made and stored transactions. The service processes "
                    + "the query and returns a response indicating the result of the operation."
    )
    public ResponseEntity<Result<String>> execute(
            @RequestBody String query){
        var userEmail = "usuario@example.com";
        var result = service.execute(userEmail, query);
        if (!result.isSuccess())
            return ResponseEntity.status(result.getErrorCode()).body(result);
        return ResponseEntity.ok(result);
    }
}
