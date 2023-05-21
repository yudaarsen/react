package ru.gadzhiev.mirea_project.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.regex.Pattern;

public class AppealUtils {

    public static class AppealCreatePayload {
        private String title;
        @JsonProperty(value = "text_body")
        private String textBody;
        @JsonProperty(value = "first_name")
        private String firstName;
        @JsonProperty(value = "last_name")
        private String lastName;
        @JsonProperty(value = "middle_name")
        private String middleName;
        private String email;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTextBody() {
            return textBody;
        }

        public void setTextBody(String textBody) {
            this.textBody = textBody;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getMiddleName() {
            return middleName;
        }

        public void setMiddleName(String middleName) {
            this.middleName = middleName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class AppealUpdatePayload {
        private int id;
        @JsonProperty(value = "category_id")
        private int categoryId;
        @JsonProperty(value = "status_id")
        private int statusId;
        @JsonProperty(value = "department_id")
        private int departmentId;
        @JsonProperty(value = "employee_id")
        private int employeeId;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(int categoryId) {
            this.categoryId = categoryId;
        }

        public int getStatusId() {
            return statusId;
        }

        public void setStatusId(int statusId) {
            this.statusId = statusId;
        }

        public int getDepartmentId() {
            return departmentId;
        }

        public void setDepartmentId(int departmentId) {
            this.departmentId = departmentId;
        }

        public int getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(int employeeId) {
            this.employeeId = employeeId;
        }
    }

    public static class AppealCommPayload {
        @JsonIgnore
        private int appealId;
        @JsonProperty(value = "text_body")
        private String textBody;
        private int author;

        public int getAppealId() {
            return appealId;
        }

        public void setAppealId(int appealId) {
            this.appealId = appealId;
        }

        public String getTextBody() {
            return textBody;
        }

        public void setTextBody(String textBody) {
            this.textBody = textBody;
        }

        public int getAuthor() {
            return author;
        }

        public void setAuthor(int author) {
            this.author = author;
        }
    }

    public static AppealCreatePayload getAppealCreatePayload(String payload) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            AppealCreatePayload appealCreatePayload = objectMapper.readValue(payload, AppealCreatePayload.class);
            if(validateAppeal(appealCreatePayload))
                return appealCreatePayload;
            return null;
        } catch (JsonProcessingException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static AppealUpdatePayload getAppealUpdatePayload(String payload) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            AppealUpdatePayload appealUpdatePayload = objectMapper.readValue(payload, AppealUpdatePayload.class);
            if(appealUpdatePayload.getStatusId() <= 0)
                return null;
            return appealUpdatePayload;
        } catch (JsonProcessingException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static AppealCommPayload getAppealCommPayload(String payload) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(payload, AppealCommPayload.class);
        } catch (JsonProcessingException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    private static boolean validateEmail(String email) {
        if(email == null || email.length() == 0)
            return false;
        return Pattern.compile("^(.+)@(\\S+)$")
                .matcher(email)
                .matches();
    }
    private static boolean validateAppeal(AppealCreatePayload appealCreatePayload) {
        if(appealCreatePayload.getTitle() == null || appealCreatePayload.getTitle().length() == 0)
            return false;
        if(appealCreatePayload.getTextBody() == null || appealCreatePayload.getTextBody().length() == 0)
            return false;
        if(!validateEmail(appealCreatePayload.getEmail()))
            return false;
        if(appealCreatePayload.getFirstName() == null || appealCreatePayload.getFirstName().length() == 0)
            return false;
        if(appealCreatePayload.getLastName() == null || appealCreatePayload.getLastName().length() == 0)
            return false;
        return true;
    }

}
