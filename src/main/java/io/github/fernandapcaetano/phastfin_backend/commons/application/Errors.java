package io.github.fernandapcaetano.phastfin_backend.commons.application;

public enum Errors {
    EMPTY_VALUE("Value is empty."),
    USER_ALREADY_EXISTS("User already exists."),
    USER_DOES_NOT_EXISTS("User does not exists."),
    INVALID_VALUE("Invalid value."),
    INVALID_STATEMENT("Statement does not exists."),
    EMPTY_STATEMENT("There is no statement"),
    EXPIRED_TOKEN("Expired token."),
    VERIFY_EMAIL("Please, verify your email."),
    INVALID_VALIDATION("Invalid validation."),
    SOMETHING_WENT_WRONG("Something went wrong."),
    INVALID_FILE("Invalid file"),
    INVALID_PAGE_NUMBER_SIZE("Page number or page size cannot be equal or less than 0 (zero)."),
    ORGANIZATION_DOES_NOT_EXISTS("Organization does not exists."),
    INVALID_QUERY("Invalid query.");

    private final String message;
    Errors(String message) { this.message = message; }
    public String getMessage() { return message; }
}
