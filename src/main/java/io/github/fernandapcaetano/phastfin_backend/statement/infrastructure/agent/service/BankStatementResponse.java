package io.github.fernandapcaetano.phastfin_backend.statement.infrastructure.agent.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.enumerator.TransactionCategory;
import io.github.fernandapcaetano.phastfin_backend.statement.domain.enumerator.TransactionType;
import io.github.fernandapcaetano.phastfin_backend.statement.infrastructure.agent.configuration.LocalDateTimeZDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Structured response representing a bank statement, or null if the provided text is not a valid statement.")
public record BankStatementResponse(

        @JsonProperty(required = true)
        @Schema(description = "Unique account identifier (e.g., \"123456\" or \"4656-09\").")
        String account,

        @JsonProperty(required = true)
        @Schema(description = "Currency of the account (e.g., BRL, USD).")
        String currency,

        @JsonDeserialize(using = LocalDateTimeZDeserializer.class)
        @JsonProperty(required = true)
        @Schema(
                description = "Start date of the statement period in the format 'YYYY-MM-DD'.",
                pattern = "^\\d{4}-\\d{2}-\\d{2}$"
        )
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime initDate,

        @JsonDeserialize(using = LocalDateTimeZDeserializer.class)
        @JsonProperty(required = true)
        @Schema(
                description = "End date of the statement period in the format 'YYYY-MM-DD'.",
                pattern = "^\\d{4}-\\d{2}-\\d{2}$"
        )
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime finalDate,

        @JsonProperty(required = true)
        @Schema(description = "List of all transactions recorded within the specified period.")
        List<Transaction> transactions
) {

        public record Transaction(

                @JsonDeserialize(using = LocalDateTimeZDeserializer.class)
                @JsonProperty(required = true)
                @Schema(
                        description = "Date and time of the transaction in ISO 8601 format ('YYYY-MM-DDThh:mm:ss').",
                        pattern = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$"
                )
                @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
                LocalDateTime date,

                @JsonProperty(required = true)
                @Schema(description = "Name of the person or entity to whom the transaction was sent or from whom it was received.")
                String name,

                @JsonProperty(required = true)
                @Schema(description = "Detailed description or reference of the transaction.")
                String description,

                @JsonProperty(required = true)
                @Schema(description = "Transaction amount (e.g., 100.50). If category is debit amount must be negative.")
                BigDecimal amount,

                @JsonProperty(required = true)
                @Schema(description = "Type of the transaction. You must respond only with values that are explicitly listed in parentheses it cannot be null or empty (DEBIT, CREDIT)")
                TransactionType type,

                @JsonProperty(required = true)
                @Schema(description = """
                Category of the transaction. You must respond only with values that are explicitly listed in parentheses it cannot be null or empty:
                (HOUSING) – Expenses related to living arrangements, such as rent, mortgage payments, home maintenance, utilities (electricity, water, gas), property taxes, or home insurance.
                (GROCERIES) – Purchases of food, beverages, and household supplies from supermarkets, grocery stores, or similar vendors for personal or family consumption.
                (TRANSPORTATION) – Costs associated with travel or commuting, including fuel, public transportation fares, vehicle maintenance, parking, tolls, car insurance, or ride-sharing services.
                (EDUCATION) – Payments related to learning or training, such as tuition fees, books, school supplies, online courses, educational software, or workshops.
                (HEALTHCARE) – Medical or wellness-related expenses, including doctor visits, hospital bills, medications, health insurance, dental or vision care, and fitness or therapy services.
                (ENTERTAINMENT) – Spending on leisure and recreational activities, such as movies, concerts, streaming services, hobbies, games, sports, dining out, or vacations.
                (FINANCIAL) – Transactions involving money management, such as bank fees, loan payments, savings, investments, interest, insurance, or financial service charges.
                (DONATIONS) – Charitable contributions or gifts to organizations, causes, or individuals, including tithes, fundraising contributions, or sponsorships.
                """)
                TransactionCategory category
        ) {}
}
