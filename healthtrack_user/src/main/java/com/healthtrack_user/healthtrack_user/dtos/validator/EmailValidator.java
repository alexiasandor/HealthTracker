package com.healthtrack_user.healthtrack_user.dtos.validator;

import java.util.regex.Pattern;

public class EmailValidator {
    private static final String EMAIL_REGEX = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" +
            "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    public static boolean isValid(String email) {
        return Pattern.compile(EMAIL_REGEX).matcher(email).matches();
    }
}
