package com.unilog.app.utils;

import org.bouncycastle.crypto.digests.SHA3Digest;

import java.io.UnsupportedEncodingException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.bouncycastle.util.encoders.Hex.toHexString;

/**
 * This class is tasked with hashing data so that we can accurately represent data that will be stored on the
 * blockchain. We hash because storage is limited and costly on the network and for other security reasons.
 *
 * e.g. hash qualification and hash the associated transcript.
 */
public final class HashingUtils {

    private static final int HASH_STRENGTH = 256;

    private HashingUtils() {
        //private constructor to stop instantiation of utility class
    }

    /**
     * Hashes a string using SHA3 to a specified strength.
     * @param input the string to hash
     * @return The hashed string as a byte array
     * @throws UnsupportedEncodingException
     */
    public static byte[] hashToBytes(final String input) throws UnsupportedEncodingException {
        SHA3Digest sha3Digest = new SHA3Digest(HASH_STRENGTH);
        sha3Digest.update(input.getBytes(UTF_8), 0, input.length());
        byte[] result = new byte[sha3Digest.getDigestSize()];
        sha3Digest.doFinal(result, 0);
        return result;
    }

    public static String hashToHexString(final String input) throws UnsupportedEncodingException {
        return toHexString(hashToBytes(input));
    }
}
