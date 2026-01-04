package com.totalizator.controller;

import com.totalizator.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;

public abstract class BaseController {
    protected static final Logger logger = LogManager.getLogger();
    
    protected void ensureDefaultLocale(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        if (session.getAttribute("locale") == null) {
            session.setAttribute("locale", Locale.ENGLISH);
        }
    }
    
    public User getUserFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object userObj = session.getAttribute("user");
        return userObj instanceof User ? (User) userObj : null;
    }
    
    protected boolean isAuthenticated(HttpServletRequest request) {
        return getUserFromSession(request) != null;
    }
    
    protected boolean hasRole(HttpServletRequest request, String roleName) {
        User user = getUserFromSession(request);
        return user != null && user.getRole() != null 
                && roleName.equals(user.getRole().getName());
    }
    
    public boolean requireAuthentication(HttpServletRequest request, HttpServletResponse response) 
            throws java.io.IOException {
        if (!isAuthenticated(request)) {
            redirectToLogin(request, response);
            return false;
        }
        return true;
    }
    
    public boolean requireRole(HttpServletRequest request, HttpServletResponse response, String roleName) 
            throws java.io.IOException {
        if (!hasRole(request, roleName)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        return true;
    }
    
    protected void redirectToLogin(HttpServletRequest request, HttpServletResponse response) 
            throws java.io.IOException {
        response.sendRedirect(request.getContextPath() + "/login");
    }
}

