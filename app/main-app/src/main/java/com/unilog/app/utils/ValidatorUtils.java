package com.unilog.app.utils;

import com.unilog.app.entity.Qualification;
import com.unilog.app.entity.Transcript;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ValidatorUtils {

    private static final int MINIMUM_PASSWORD_LENGTH = 8;

    private ValidatorUtils() {
    }

    public static String validatePassword(final String password1, final String password2) {
        if (password1 == null || password2 == null || password1.isEmpty() || password2.isEmpty()) {
            return "Please fill all password fields.";
        }
        if (!password1.equals(password2)) {
            return "Passwords do not match.";
        }

        StringBuilder errorMessages = new StringBuilder();
        boolean pass = true;
        for (PasswordRule rule : PASSWORD_RULES) {
            if (!rule.testRule(password1)) {
                errorMessages.append(rule.errorMessage()).append("<br>");
                pass = false;
            }
        }
        return pass ? "success" : errorMessages.toString();
    }


    private static final PasswordRule[] PASSWORD_RULES = {
            new BaseRule("Password is too short. Needs to have at least 8 characters.") {
                @Override
                public boolean testRule(final String password) {
                    return password.length() >= ValidatorUtils.MINIMUM_PASSWORD_LENGTH;
                }
            },
            new BaseRule("Password needs an upper case letter.") {
                private final Pattern hasUppercase = Pattern.compile("[A-Z]");
                @Override
                public boolean testRule(final String password) {
                    return hasUppercase.matcher(password).find();
                }
            },
            new BaseRule("Password needs a lower case letter.") {
                private final Pattern hasLowercase = Pattern.compile("[a-z]");
                @Override
                public boolean testRule(final String password) {
                    return hasLowercase.matcher(password).find();
                }
            },
            new BaseRule("Password needs to contain a number.") {
                private final Pattern hasNumber = Pattern.compile("\\d");
                @Override
                public boolean testRule(final String password) {
                    return hasNumber.matcher(password).find();
                }
            },
            new BaseRule("Password needs to contain a special character.") {
                private final Pattern hasSpecialChar = Pattern.compile("[^a-zA-Z0-9 ]");
                @Override
                public boolean testRule(final String password) {
                    return hasSpecialChar.matcher(password).find();
                }
            }
    };

    public static boolean validEmailPatterns(final Object toCheck) {
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        try {
            Qualification qualification = (Qualification) toCheck;
            for (Transcript transcript : qualification.getTranscripts()) {
                Matcher matcher = pattern.matcher(transcript.getRecipientEmailAddress());
                if (!matcher.matches()) {
                    return false;
                }
            }
        } catch (ClassCastException e) {
            String email = (String) toCheck;
            Matcher matcher = pattern.matcher(email);
            if (!matcher.matches()) {
                return false;
            }
        }
        return true;
    }

}
