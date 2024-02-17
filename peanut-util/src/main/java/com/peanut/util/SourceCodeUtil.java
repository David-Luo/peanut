package com.peanut.util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class SourceCodeUtil {
    public static void findAllSourceCode(File file, Set<File> all, String postfix) throws IOException {
        if(isSymlink(file)){
            return;
        }
        if(file.getName().endsWith(postfix)){
            all.add(file);
            return;
        }
        if(file.isDirectory()){
            File[] sub = file.listFiles();
            for (int i = 0; i < sub.length; i++) {
                findAllSourceCode(sub[i],all,postfix);
            }
        }
    }

    public static boolean isSymlink(File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("File must not be null");
        }
        File canon;
        if (file.getParent() == null) {
            canon = file;
        } else {
            File canonDir = file.getParentFile().getCanonicalFile();
            canon = new File(canonDir, file.getName());
        }
        return !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
    }

    public static List<String> findAllClassInPackage(String sourcePath, String scanPackage) throws IOException {
        String packagePath = scanPackage.replace(".","/");
        File sourceRoot = new File(sourcePath);
        int pathPrefix = sourceRoot.getAbsolutePath().length()+1;
        sourceRoot = new File(sourceRoot,packagePath);
        Set<File> f = new TreeSet<>();
        findAllSourceCode(sourceRoot,f,".java");
        if (f.isEmpty()){
            return null;
        }
        return f.stream().map(File::getAbsolutePath)
                .map(p -> p.substring(pathPrefix).replace(".java","").replace("/", "."))
                .collect(Collectors.toList());
    }
}
