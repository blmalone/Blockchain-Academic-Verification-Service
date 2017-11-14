package com.unilog.app.utils;

public interface PasswordRule {
    boolean testRule(final String password);
    String errorMessage();
}
