package com.unilog.app.security;

import java.math.BigInteger;
import java.security.SecureRandom;

public final class SecureSessionCode {
    private static SecureRandom random = new SecureRandom();
    private static final int NUM_BITS = 130;
    private static final int RADIX = 32;

    private SecureSessionCode() {
    }

    public static String generateCode() {
        return new BigInteger(NUM_BITS, random).toString(RADIX);
    }
}
