package com.peanut.util;

public class StringUtils {

    /**
     * 将给定字符串的第一个字符转换为大写，保留其余字符不变。
     *
     * @param input 需要处理的字符串
     * @return 处理后的字符串，首字母大写，其余字符保持原样
     */
    public static String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        char[] chars = input.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);

        return new String(chars);
    }

    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }
}
