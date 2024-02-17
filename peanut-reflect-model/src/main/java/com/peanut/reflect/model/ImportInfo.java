package com.peanut.reflect.model;

import jakarta.persistence.Id;

import java.util.Objects;

public class ImportInfo {
    @Id
    private String name;
    private String simpleName;
    private String packageName;
    public ImportInfo(){}
    public ImportInfo(String name) {
        resetName(name);
    }
    public ImportInfo(String packageName, String simpleName) {
        resetName(packageName, simpleName);
    }

    public void resetName(String packageName, String simpleName){
        this.name = packageName + "." + simpleName;
        this.simpleName = simpleName;
        this.packageName = packageName;
    }
    public void resetName(String name){
        this.name = name;
        if(name.contains(".")) {
            int index = name.lastIndexOf(".");
            simpleName = name.substring(index + 1);
            packageName = name.substring(0, index);
        }else {
            simpleName = name;
            packageName = "";
        }
    }
    
    public String getName() {
        return name;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public String getPackageName() {
        return packageName;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        ImportInfo importInfo = (ImportInfo) o;
        return Objects.equals(name, importInfo.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
