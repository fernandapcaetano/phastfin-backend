package io.github.fernandapcaetano.phastfin_backend.statement.infrastructure.agent.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.entity.Insight;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.entity.StatementEmbedding;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.entity.Transaction;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.repository.IStatementEmbeddingRepository;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.service.IAgentChat;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.value_object.StatementId;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.value_object.UserId;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class AgentChat implements IAgentChat<BankStatementResponse> {

    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;
    private final IStatementEmbeddingRepository statementEmbeddingRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentChat.class);
    private final ObjectMapper jsonMapper;

    public AgentChat(ChatClient chatClient, EmbeddingModel embeddingModel, IStatementEmbeddingRepository statementEmbeddingRepository, ObjectMapper jsonMapper) {
        this.chatClient = chatClient;
        this.embeddingModel = embeddingModel;
        this.statementEmbeddingRepository = statementEmbeddingRepository;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public BankStatementResponse analyzeBankStatement(InputStream file) {
        LOGGER.info("Analyzing statement file.");
        var resource = new InputStreamResource(file);
        var documents = new TikaDocumentReader(resource).read();
        if (documents.isEmpty())  return  null;
        var statement = documents.getFirst().getText();
        if (statement == null || statement.trim().isEmpty()) return null;

        final var systemPrompt = """
        You are an AI specialized in reading and structuring bank statements. Analyze the provided text and return a structured JSON according to the specified schema.
        Respond only with valid JSON, without backticks, without Markdown, without comments.
        If the text does not represent a valid bank statement, return the literal value null (without quotes or explanations).
        """;
        final var userPrompt = """
        Here is a bank statement:
        %s
        Return only the JSON based en the schema (or null):
        """.formatted(statement);
        return chatClient.prompt()
                .system(systemPrompt)
                .user(u-> u.text(userPrompt))
                .call()
                .entity(BankStatementResponse.class);
    }

    @Override
    public void createEmbedding(BankStatementResponse statement, UserId userId,
                                StatementId statementId) {
        LOGGER.info("Creating statement embedding.");
        String statementJson = null;

        try { statementJson = jsonMapper.writeValueAsString(statement);
        } catch (JsonProcessingException e) { throw new RuntimeException(e); }

        var vector = embeddingModel.embed(statementJson);
        var existsStatementByUserId = statementEmbeddingRepository
            .findByUserIdAndStatementId(userId, statementId);

        StatementEmbedding embedding = null;
        if (existsStatementByUserId.isPresent()){
            embedding = existsStatementByUserId.get();
            embedding.update(statementJson, vector);
        } else {
            embedding = StatementEmbedding.create(
                    userId, statementId, statementJson, vector
            );
        }
        statementEmbeddingRepository.save(embedding);
    }

    @Override
    public String findSimilarity(Long userId, String query) {
        var embeddedQuery = embeddingModel.embed(query);

        var vectorString = IntStream.range(0, embeddedQuery.length)
                .mapToObj(i -> Float.toString(embeddedQuery[i]))
                .collect(Collectors.joining(",", "[", "]"));

        var vectorEmbedding = statementEmbeddingRepository
                .findMostSimilar(userId, vectorString, 10);

        var context = vectorEmbedding.stream()
                .map(StatementEmbedding::getContent)
                .map(text -> text.length() > 2000 ? text.substring(0, 2000) + "..." : text)
                .collect(Collectors.joining("\n---\n"));

        final var systemPrompt = """
            You are an AI specialized in analyzing and structuring bank statements.
            Use only the information from the provided statements to answer the user's question.
            If the question or context does not contain valid bank statement data, respond with: null
            Be gentle and friendly.
            If theres no bank statements context say "I don't have this information, please upload a statement with that information.".
        """;

        final var userPrompt = """
            User question:
            %s
        
            Bank statements context:
            %s
        """.formatted(query, context);

            return chatClient
                    .prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call()
                    .content();
    }

    @Override
    public Insight insightStatement(List<Transaction> transactions, UserId userId,
                                    StatementId statementId) {
        LOGGER.info("Analyzing transactions transforming to insights.");
        try {
            var jsonTransactions = jsonMapper.writeValueAsString(transactions);
            final var systemPrompt = """
            You are an AI specialized in analyzing financial transactions.
            Your task is to read and evaluate a set of transactions, then provide:
            A compliment highlighting something positive about the transactions or spending behavior.
            A constructive critique suggesting how the user could improve their financial decisions or transaction patterns.
            Keep your tone friendly, insightful, and supportive — like a personal finance coach who gives honest but kind feedback.
            """;
            final var userPrompt = """
            Here is the transactions:
            %s
            Return only the JSON based en the schema (or null):
            """.formatted(jsonTransactions);
            var response = chatClient
                    .prompt().
                    system(systemPrompt)
                    .user(u-> u.text(userPrompt))
                    .call()
                    .entity(InsightResponse.class);
            return Insight.createStatementInsight(userId, statementId, response.praise(), response.critic());
        } catch (JsonProcessingException e) {
            LOGGER.error("Error while analyzing transactions transforming to insights.", e);
            return  null;
        }
    }

    @Override
    public Insight insightGeneral(Long userId) {
        return null;
    }
}
