package store.utils;

import java.text.NumberFormat;

public class NumberFormatter {
    public static String formatWithCommas(int number) {
        NumberFormat formatter = NumberFormat.getInstance();
        return formatter.format(number);
    }
}

