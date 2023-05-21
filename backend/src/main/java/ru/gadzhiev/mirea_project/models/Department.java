package ru.gadzhiev.mirea_project.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Department {
    private int id;
    private String name;
    @JsonIgnore
    private String lang;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
