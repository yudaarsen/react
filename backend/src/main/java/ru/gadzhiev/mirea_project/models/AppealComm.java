package ru.gadzhiev.mirea_project.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class AppealComm {
    @JsonIgnore
    private int appealId;
    @JsonProperty(value = "create_date")
    private Timestamp createDate;
    private Employee employee;
    @JsonProperty(value = "text_body")
    private String textBody;

    public int getAppealId() {
        return appealId;
    }

    public void setAppealId(int appealId) {
        this.appealId = appealId;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getTextBody() {
        return textBody;
    }

    public void setTextBody(String textBody) {
        this.textBody = textBody;
    }
}
