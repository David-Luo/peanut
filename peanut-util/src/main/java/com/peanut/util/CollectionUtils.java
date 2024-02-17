package com.peanut.util;

import java.util.*;

public class CollectionUtils {
    public static boolean isEmpty(Map<?,?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isEmpty(Collection<?> list) {
        return list == null || list.isEmpty();
    }

    /**
     * 
     * @param className
     * @return
     */
    public static boolean isCollection(String className){
        return List.class.getName().equals(className) 
            || Set.class.getName().equals(className) 
            || Queue.class.getName().equals(className) 
            || Stack.class.getName().equals(className);
    }
}
