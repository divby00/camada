package org.wildcat.camada.service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PasswordGenerator {

    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String PUNCTUATION = "@#$%";

    public static String generate() {
        int length = 12;
        StringBuilder password = new StringBuilder(length);
        Random random = new Random(System.nanoTime());

        List<String> charCategories = Arrays.asList(LOWER, DIGITS, UPPER, PUNCTUATION);

        int position = 0;
        String charCategory = "";
        for (int i = 0; i < length / 4; i++) {
            for (int a = 0; a < 4; a++) {
                charCategory = charCategories.get(a);
                position = random.nextInt(charCategory.length());
                password.append(charCategory.charAt(position));
            }
        }
        return new String(password);
    }
}
