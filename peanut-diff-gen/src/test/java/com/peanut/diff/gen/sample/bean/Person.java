package com.peanut.diff.gen.sample.bean;

import jakarta.persistence.Id;

public class Person {
    @Id
    private int code;
    private String name;
    private boolean isDisabled;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public void setDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

}
