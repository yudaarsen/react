package ru.gadzhiev.mirea_project.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Category {
    private int code;
    private String name;
    @JsonIgnore
    private String lang;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
