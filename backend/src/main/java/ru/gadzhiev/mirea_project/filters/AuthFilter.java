package ru.gadzhiev.mirea_project.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gadzhiev.mirea_project.services.AuthService;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Фильтр авторизации для контроля доступа к ресурсам
 */
@Component
public class AuthFilter implements Filter {

    @Autowired
    private AuthService authService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if(req.getMethod().equals("OPTIONS")) {
            chain.doFilter(request, response);
            return;
        }

        if(req.getMethod().equals("POST")
                && ( req.getRequestURI().equals("/api/appeal")
                || Pattern.compile("/api/appeal/\\d+/attachment").matcher(req.getRequestURI()).matches())) {
            chain.doFilter(request, response);
            return;
        }

        Cookie[] cookies = req.getCookies();
        String accessToken = "";
        if(cookies == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            //chain.doFilter(request, response);
            return;
        }
        for(Cookie cookie : cookies) {
            if(cookie.getName().equals("accessToken")) {
                accessToken = cookie.getValue();
                if(!authService.validateAccessToken(accessToken)) {
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    chain.doFilter(request, response);
                    return;
                }
            }
        }
        chain.doFilter(request, response);
    }

}
