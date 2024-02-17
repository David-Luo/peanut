package com.peanut.diff.runtime.checker;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class ChangeCheckerInitialContext {
    public static final String beanCheckerMapping = "bean-diff.properties";

    /**
     * 被检查对象和检查器的关系
     */
    private Map<String, String> beanChecker;

    public ChangeCheckerInitialContext() {
        beanChecker = defaultChecker();
        try (InputStream ppr = Thread.currentThread().getContextClassLoader().getResourceAsStream(beanCheckerMapping)) {
            if (ppr != null) {
                Properties appProps = new Properties();
                appProps.load(ppr);
                appProps.forEach((k, v) -> beanChecker.put(k.toString(), v.toString()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object lookup(String service) {
        try {
            Class<?> clazz = Class.forName(service);
            return clazz.getConstructor().newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void register(String beanName, String checker) {
        beanChecker.put(beanName, checker);
    }

    public String getBeanChecker(String beanClass) {
        return beanChecker.get(beanClass);
    }

    /**
     * 基础类型对象 包含JAVA 8大基础类型
     * java.lang.Boolean#TYPE
     * java.lang.Character#TYPE
     * java.lang.Byte#TYPE
     * java.lang.Short#TYPE
     * java.lang.Integer#TYPE
     * java.lang.Long#TYPE
     * java.lang.Float#TYPE
     * java.lang.Double#TYPE
     * 四种日期类型 LocalDate.class LocalDateTime.class Date.class Timestamp
     * 两种字符类型 String.class Character.class
     * 枚举类型 Enum.class
     * 数字类型 BigDecimal.class
     * Object类型
     */
    public static Map<String, String> defaultChecker() {
        Map<String, String> defaultChecker = new HashMap<>();
        defaultChecker.put(Map.class.getName(), MapDiffChecker.class.getName());
        defaultChecker.put(HashMap.class.getName(), MapDiffChecker.class.getName());
        defaultChecker.put(List.class.getName(), ListSequenceChecker.class.getName());
        defaultChecker.put(ArrayList.class.getName(), ListSequenceChecker.class.getName());
        defaultChecker.put(BigDecimal.class.getName(), ComparableDiffChecker.class.getName());
        defaultChecker.put(String.class.getName(), ComparableDiffChecker.class.getName());
        defaultChecker.put(Character.class.getName(), ComparableDiffChecker.class.getName());
        defaultChecker.put(LocalDateTime.class.getName(), LocalDateTimeChecker.class.getName());
        defaultChecker.put(LocalDate.class.getName(), DefaultDiffChecker.class.getName());
        defaultChecker.put(Timestamp.class.getName(), DefaultDiffChecker.class.getName());
        
        defaultChecker.put(int.class.getName(), DefaultDiffChecker.class.getName());
        defaultChecker.put(Integer.class.getName(), ComparableDiffChecker.class.getName());
        defaultChecker.put(boolean.class.getName(), DefaultDiffChecker.class.getName());
        defaultChecker.put(Boolean.class.getName(), DefaultDiffChecker.class.getName());
        defaultChecker.put(char.class.getName(), DefaultDiffChecker.class.getName());
        defaultChecker.put(Character.class.getName(), DefaultDiffChecker.class.getName());
        defaultChecker.put(byte.class.getName(), DefaultDiffChecker.class.getName());
        defaultChecker.put(Byte.class.getName(), DefaultDiffChecker.class.getName());
        defaultChecker.put(short.class.getName(), DefaultDiffChecker.class.getName());
        defaultChecker.put(Short.class.getName(), DefaultDiffChecker.class.getName());
        defaultChecker.put(long.class.getName(), DefaultDiffChecker.class.getName());
        defaultChecker.put(Long.class.getName(), DefaultDiffChecker.class.getName());
        defaultChecker.put(float.class.getName(), DefaultDiffChecker.class.getName());
        defaultChecker.put(Float.class.getName(), DefaultDiffChecker.class.getName());
        defaultChecker.put(double.class.getName(), DefaultDiffChecker.class.getName());
        defaultChecker.put(Double.class.getName(), DefaultDiffChecker.class.getName());
        defaultChecker.put(Enum.class.getName(), DefaultDiffChecker.class.getName());
        defaultChecker.put(Object.class.getName(), DefaultDiffChecker.class.getName());
        return defaultChecker;
    }
}