package com.example.yummy.utils;

import java.security.SecureRandom;

public class RandomIDGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int ID_LENGTH = 10;
    private static final SecureRandom random = new SecureRandom();

    public static String generateFriendlyId() {
        StringBuilder id = new StringBuilder(ID_LENGTH);
        for (int i = 0; i < ID_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            id.append(CHARACTERS.charAt(index));
        }
        return id.toString();
    }

    public static void main(String[] args) {
        String friendlyId = generateFriendlyId();
        System.out.println("Friendly ID: " + friendlyId);
    }
}
