package com.unilog.app.utils;

public abstract class BaseRule implements PasswordRule {
    private final String message;
    public BaseRule(final String message) {
        this.message = message;
    }

    public String errorMessage() {
        return message;
    }
}
