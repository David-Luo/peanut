package com.peanut.reflect.model;

import java.util.HashMap;
import java.util.Map;

public class AnnotationInfo extends TypeInfo{
    private Map<String,Object> members;
    public AnnotationInfo(){
        super();
    }
    public AnnotationInfo(String name){
        super(name);
        members = new HashMap<>();
    }

    public Map<String, Object> getMembers() {
        return members;
    }

    public void setMembers(Map<String, Object> members) {
        this.members = members;
    }
    public void setMember(String name, Object member){
        members.put(name,member);
    }
}
