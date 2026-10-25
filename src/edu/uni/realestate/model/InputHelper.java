package edu.uni.realestate.model;

import java.math.BigDecimal;
import java.util.Scanner;

final class InputHelper {

    private InputHelper() { }

    static String readInt(Scanner in, String prompt, int min) {
        while (true) {
            System.out.print(prompt);
            String text = in.nextLine().trim();
            try {
                if (Integer.parseInt(text) >= min) {
                    return text;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("  Please enter a number of at least " + min + ".");
        }
    }

    static String readDouble(Scanner in, String prompt, double min) {
        while (true) {
            System.out.print(prompt);
            String text = in.nextLine().trim();
            try {
                double value = Double.parseDouble(text);
                if (Double.isFinite(value) && value >= min) {
                    return text;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("  Please enter a number of at least "
                    + BigDecimal.valueOf(min).stripTrailingZeros().toPlainString() + ".");
        }
    }

    static String readYesNo(Scanner in, String prompt) {
        return readChoice(in, prompt, "yes", "no");
    }

    static String readChoice(Scanner in, String prompt, String... options) {
        while (true) {
            System.out.print(prompt);
            String text = in.nextLine().trim();
            for (String option : options) {
                if (option.equalsIgnoreCase(text)) {
                    return option;
                }
            }
            System.out.println("  Please choose one of: " + String.join(", ", options));
        }
    }
}
