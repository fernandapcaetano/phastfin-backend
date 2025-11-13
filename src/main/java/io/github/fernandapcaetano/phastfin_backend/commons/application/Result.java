package io.github.fernandapcaetano.phastfin_backend.commons.application;

import java.util.HashMap;

public class Result<T> {
    private final boolean isSuccess;
    private final int errorCode;
    private final String message;
    private final HashMap<String, String> fieldErrors;
    private final T value;

    private Result(String message, T value) {
        this.isSuccess = true;
        this.errorCode = 0;
        this.fieldErrors = new HashMap<>();
        this.message = message;
        this.value = value;
    }

    private Result(int errorCode, String message) {
        this.isSuccess = false;
        this.errorCode = errorCode;
        this.fieldErrors = new HashMap<>();
        this.message = message;
        this.value = null;
    }

    private Result(int errorCode, HashMap<String, String> fieldErrors) {
        this.isSuccess = false;
        this.errorCode = errorCode;
        this.fieldErrors = fieldErrors;
        this.message = "Invalid input";
        this.value = null;
    }

    public static <T> Result<T> success(String message,T value) {
        return new Result<>(message, value);
    }

    public static <T> Result<T> success(T value) {
        return new Result<>(null, value);
    }

    public static <T> Result<T> success(String message) {
        return new Result<>(message, null);
    }

    public static <T> Result<T> success(T value, String message) {
        return new Result<>(message, value);
    }

    public static <T> Result<T> failure(int errorCode, String message) {
        return new Result<>(errorCode, message);
    }

    public static <T> Result<T> fieldErrors(HashMap<String, String> fieldErrors, int errorCode) {
        return new Result<>(errorCode, fieldErrors);
    }

    public boolean isSuccess() {return isSuccess;}
    public int getErrorCode() {return errorCode;}
    public String getMessage() {return message;}
    public HashMap<String, String> getFieldErrors() {return fieldErrors;}
    public T getValue() {return value;}
}