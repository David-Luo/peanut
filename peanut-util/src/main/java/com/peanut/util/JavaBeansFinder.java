package com.peanut.util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JavaBeansFinder {

    public static Set<String> findJavaBeansInPackage(String packageName) throws ClassNotFoundException, IOException {
        Set<String> javaBeans = new HashSet<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        // 查找当前包及子包下的.class文件
        Enumeration<URL> resources = classLoader.getResources(packageName.replace(".", "/"));
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String protocol = resource.getProtocol();
            if ("file".equals(protocol)) {
                File dir = new File(resource.getFile());
                scanForJavaBeans(dir, packageName, javaBeans);
            } else if ("jar".equals(protocol)) {
                JarFile jar = ((JarURLConnection) resource.openConnection()).getJarFile();
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    if (name.startsWith(packageName.replace(".", "/")) && name.endsWith(".class")) {
                        String className = name.substring(0, name.length() - 6).replace("/", ".");
                        if (JavaBeanUtils.isJavaBean(className)) {
                            javaBeans.add(className);
                        }
                    }
                }
            }
        }
        return javaBeans;
    }

    private static void scanForJavaBeans(File directory, String packageName, Set<String> javaBeans) throws ClassNotFoundException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    scanForJavaBeans(file, packageName + "." + file.getName(), javaBeans);
                } else if (file.getName().endsWith(".class")) {
                    String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                    if (JavaBeanUtils.isJavaBean(className)) {
                        javaBeans.add(className);
                    }
                }
            }
        }
    }
    public static Set<String> findInJar(String jarPath) {
        Set<String> javaBeans = new HashSet<>();
        try (JarFile jar = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String className = entry.getName();
                if (!entry.isDirectory() && className.endsWith(".class")) {
                    className = className.substring(0, className.length() - 6).replace('/', '.');
                    if (JavaBeanUtils.isJavaBean(className)) {
                        javaBeans.add(className);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return javaBeans;
    }

}