package com.peanut.reflect.util;

import java.lang.reflect.Modifier;
import java.util.stream.Stream;

public class ModifiersUtils {
    public static String[] getModifiers(int modifiers) {
        if (modifiers == 0){
            return new String[0];
        }
        return  Stream.of(Modifier.toString(modifiers)
                    .split(" "))
                    .toArray(String[]::new);
    }
}
