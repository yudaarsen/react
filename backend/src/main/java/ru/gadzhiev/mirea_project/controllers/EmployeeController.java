package ru.gadzhiev.mirea_project.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.gadzhiev.mirea_project.models.Employee;
import ru.gadzhiev.mirea_project.repositories.interfaces.EmployeeRepository;
import ru.gadzhiev.mirea_project.services.AuthService;
import ru.gadzhiev.mirea_project.utils.EmployeeUtils;

import java.util.List;

@Controller
public class EmployeeController {

    private class Token {
        @JsonProperty(value = "access_token")
        private String accessToken;
        @JsonProperty(value = "refresh_token")
        private String refreshToken;

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AuthService authService;

    @PostMapping(path = "/api/access", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Token authenticate(@RequestBody String payload, HttpServletResponse response) {
        EmployeeUtils.AuthenticationPayload authenticationPayload = EmployeeUtils.getAuthenticationPayload(payload);
        if(authenticationPayload == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        Employee employee = null;

        try {
            employee = employeeRepository.getEmployeeByEmailAndPassword(authenticationPayload.getEmail(), authenticationPayload.getPassword());
        } catch (EmptyResultDataAccessException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }

        if(employee == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }

        String accessToken = authService.generateAccessToken(employee.getId()).getToken();
        String refreshToken = authService.generateRefreshToken(employee.getId()).getToken();

        authService.updateAccessToken(employee.getId(), accessToken);
        authService.updateRefreshToken(employee.getId(), refreshToken);

        response.addCookie(new Cookie("accessToken", accessToken));
        //response.addCookie(new Cookie("refreshToken", refreshToken));

        Token token = new Token();
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        return token;
    }

    @PostMapping(path = "/api/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Token refresh(@RequestParam(value = "refreshToken") String refreshToken, HttpServletResponse response) {
        if(!authService.validateRefreshToken(refreshToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }

        AuthService.TokenData tokenData = authService.getTokenData(refreshToken);

        String newAccessToken = authService.generateAccessToken(tokenData.getEmployee()).getToken();
        String newRefreshToken = authService.generateRefreshToken(tokenData.getEmployee()).getToken();

        authService.updateAccessToken(tokenData.getEmployee(), newAccessToken);
        authService.updateRefreshToken(tokenData.getEmployee(), newRefreshToken);

        response.addCookie(new Cookie("accessToken", newAccessToken));
        //response.addCookie(new Cookie("refreshToken", newRefreshToken));

        Token token = new Token();
        token.setAccessToken(newAccessToken);
        token.setRefreshToken(newRefreshToken);
        return token;
    }

    @GetMapping(path = "/api/employees", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Employee> getEmployees() {
        return employeeRepository.getEmployees();
    }
}
