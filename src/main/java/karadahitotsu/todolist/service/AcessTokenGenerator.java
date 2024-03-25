package karadahitotsu.todolist.service;

import java.util.Random;

public class AcessTokenGenerator {
    private static final String ALLOWED_CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public String generate() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 200; i++) {
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        }
        return sb.toString();
    }
}
