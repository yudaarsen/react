package ru.gadzhiev.mirea_project.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Attachment {

    @JsonIgnore
    private int appealId;
    @JsonIgnore
    private int attachNum;
    private String name;

    public int getAppealId() {
        return appealId;
    }

    public void setAppealId(int appealId) {
        this.appealId = appealId;
    }

    public int getAttachNum() {
        return attachNum;
    }

    public void setAttachNum(int attachNum) {
        this.attachNum = attachNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
