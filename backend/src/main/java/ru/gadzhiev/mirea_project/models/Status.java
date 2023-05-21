package ru.gadzhiev.mirea_project.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class Status {
    private int code;
    private String name;
    @JsonIgnore
    private String lang;
    private List<Status> nextStatuses;

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

    public List<Status> getNextStatuses() {
        return nextStatuses;
    }

    public void setNextStatuses(List<Status> nextStatuses) {
        this.nextStatuses = nextStatuses;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
