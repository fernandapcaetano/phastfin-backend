package io.github.fernandapcaetano.phastfin_backend.statement.infrastructure.agent.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "A structured response containing personalized financial insights based on analyzed transactions.")
public record InsightResponse(

        @JsonProperty(required = true)
        @Schema(description = "A personalized compliment that highlights positive financial habits or spending patterns identified in the user's transactions.")
        String praise,

        @JsonProperty(required = true)
        @Schema(description = "A constructive and friendly piece of feedback offering suggestions on how the user can improve their financial behavior or transaction patterns. The tone should remain supportive and insightful, similar to a personal finance coach providing kind but honest advice.")
        String critic

) { }

