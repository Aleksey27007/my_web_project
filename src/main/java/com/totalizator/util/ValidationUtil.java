package com.totalizator.util;


import com.totalizator.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Locale;

public final class ValidationUtil {

    public static boolean isValidId(Integer id) {
        return id != null && id > 0;
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean areAllNotEmpty(String... strings) {
        if (strings == null || strings.length == 0) {
            return false;
        }
        for (String str : strings) {
            if (isNullOrEmpty(str)) {
                return false;
            }
        }
        return true;
    }

    public void ensureDefaultLocale(HttpServletRequest request) {
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

    private boolean isAuthenticated(HttpServletRequest request) {
        return getUserFromSession(request) != null;
    }

    private boolean hasRole(HttpServletRequest request, String roleName) {
        User user = getUserFromSession(request);
        return user != null && user.getRole() != null
                && roleName.equals(user.getRole().getName());
    }

    public boolean requireAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (!isAuthenticated(request)) {
            redirectToLogin(request, response);
            return false;
        }
        return true;
    }

    public boolean requireRole(HttpServletRequest request, HttpServletResponse response, String roleName)
            throws IOException {
        if (!hasRole(request, roleName)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        return true;
    }

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/login");
    }
}

