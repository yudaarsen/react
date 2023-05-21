package ru.gadzhiev.mirea_project.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EmployeeUtils {

    public static class AuthenticationPayload {
        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static AuthenticationPayload getAuthenticationPayload(String payload) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(payload, AuthenticationPayload.class);
        } catch (JsonProcessingException exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
