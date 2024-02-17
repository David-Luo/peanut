package com.peanut.reflect.model;

import jakarta.persistence.Id;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TypeInfo{

    private List<TypeInfo> genericTypes;
    private boolean array=false;
    private boolean innerClass=false;
    @Id
    private String name;
    private String simpleName;
    private String packageName;
    public TypeInfo(){}
    public TypeInfo(String name) {
        resetName(name);
    }
    public TypeInfo(Class<?> clazz){
        this(clazz.getName());
    }
    public TypeInfo(String packageName, String simpleName) {
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
        TypeInfo typeName = (TypeInfo) o;
        return Objects.equals(name, typeName.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
//    this.innerClass = getSimpleName().contains("$") ? true : false;

    /**
     * 是否有范型
     * @return
     */
    public boolean hasGeneric(){
        return genericTypes != null && !genericTypes.isEmpty();
    }
    public List<TypeInfo> getGenericTypes() {
        return genericTypes;
    }

    public void addGenericType(TypeInfo genericType){
        if (this.genericTypes == null) {
            this.genericTypes = new LinkedList<>();
        }
        this.genericTypes.add(genericType);
    }
    public TypeInfo getFirstGenericType(){
        if(hasGeneric()){
            return genericTypes.get(0);
        }
        return null;
    }
    public void setGenericTypes(List<TypeInfo> genericTypes) {
        if(genericTypes==null){
            genericTypes = new LinkedList<>();
        }
        this.genericTypes = genericTypes;
    }

    public boolean isArray() {
        return array;
    }

    public void setArray(boolean array) {
        this.array = array;
    }

    public boolean isInnerClass() {
        return innerClass;
    }

    // TODO 内部类的引包暂时根据路径中是否存在$判断
    public boolean isNotInnerClass() {
        return !innerClass;
    }

    public String toTypeString() {
        StringBuilder type = new StringBuilder(this.getSimpleName());
        if (this.hasGeneric()) {
            type.append('<')
                    .append(this.getGenericTypes()
                            .stream().map(TypeInfo::toTypeString)
                            .collect(Collectors.joining(","))
                    )
                    .append('>');
        }
        return type.toString();
    }
}
