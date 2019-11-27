package com.coronation.upload.util;

import java.util.Map;

/**
 * Created by Toyin on 7/4/19.
 */
public class Test {
    public static String findMostProbableCriminal(Map<String, String> criminals, String possibleName) {
        String key = null;
        int score = 0;
        for (Map.Entry<String, String> entry: criminals.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(possibleName)) {
                key = entry.getKey();
                break;
            } else if (entry.getKey().toLowerCase().contains(possibleName.toLowerCase())) {
                key = entry.getKey();
                score = 3;
            } else if (score < 2 && entry.getValue() != null) {
                String[] aliases = entry.getValue().split(", ");
                for (String alias: aliases) {
                    if (alias.equalsIgnoreCase(possibleName)) {
                        score = 2;
                        key = entry.getKey();
                    } else if (score < 1) {
                        if (alias.toLowerCase().contains(possibleName.toLowerCase())) {
                            score = 1;
                            key = entry.getKey();
                        }
                    }
                }
            }
        }

        if (key == null) {
            return "No match";
        } else {
            StringBuilder builder = new StringBuilder("First name: " + key + ".");
            String names = criminals.get(key);
            if (names != null) {
                builder.append(" Aliases: ");
                builder.append(names);
            }
            return builder.toString();
        }
    }
}
