package com.unilog.app.utils;

import com.unilog.app.entity.Qualification;
import com.unilog.app.entity.Transcript;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ValidatorUtils {

    private ValidatorUtils() {
    }

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
