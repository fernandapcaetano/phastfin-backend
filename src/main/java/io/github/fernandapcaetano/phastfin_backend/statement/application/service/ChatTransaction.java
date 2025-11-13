package io.github.fernandapcaetano.phastfin_backend.statement.application.service;

import io.github.fernandapcaetano.phastfin_backend.commons.application.Errors;
import io.github.fernandapcaetano.phastfin_backend.commons.application.Result;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.repository.IUserRepository;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.service.IAgentChat;
import io.github.fernandapcaetano.phastfin_backend.statement.infrastructure.agent.service.BankStatementResponse;
import org.springframework.stereotype.Service;

@Service
public class ChatTransaction {

    private final IUserRepository userRepository;
    private final IAgentChat<BankStatementResponse> agentChat;

    public ChatTransaction(IUserRepository userRepository, IAgentChat<BankStatementResponse> agentChat) {
        this.userRepository = userRepository;
        this.agentChat = agentChat;
    }

    public Result<String> execute(String userEmail, String query){

        if (userEmail == null || userEmail.isBlank())
            return Result.failure(401, Errors.SOMETHING_WENT_WRONG.getMessage());
        var userIdByEmail = userRepository.findIdByEmail(userEmail);

        if (userIdByEmail == null)
            return Result.failure(401, Errors.USER_DOES_NOT_EXISTS.getMessage());

        if (query == null || query.isBlank())
            return Result.failure(400, Errors.INVALID_QUERY.getMessage());

        var chatResponse = agentChat.findSimilarity(userIdByEmail, query);
        if (chatResponse == null)
            return Result.failure(400, Errors.INVALID_QUERY.getMessage());

        return Result.success(chatResponse);
    }

}
