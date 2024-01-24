package com.kits.kowsarapp.model;

public class NumberFunctions {

    private static final String[] persianNumbers = new String[]{"۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹"};

    public static String PerisanNumber(String text) {

        if (text == null) {

            return "";
        }
        if (text.length() == 0) {

            return "";
        }
        StringBuilder out = new StringBuilder();
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            if ('0' <= c && c <= '9') {
                int number = Integer.parseInt(String.valueOf(c));
                out.append(persianNumbers[number]);
            } else if (c == '٫') {
                out.append('،');
            } else {
                out.append(c);
            }
        }
        return out.toString();

    }


    public static String EnglishNumber(String number) {
        char[] chars = new char[number.length()];
        for (int i = 0; i < number.length(); i++) {
            char ch = number.charAt(i);
            if (ch >= 0x0660 && ch <= 0x0669)
                ch -= 0x0660 - '0';
            else if (ch >= 0x06f0 && ch <= 0x06F9)
                ch -= 0x06f0 - '0';
            chars[i] = ch;
        }
        return new String(chars);
    }
}
