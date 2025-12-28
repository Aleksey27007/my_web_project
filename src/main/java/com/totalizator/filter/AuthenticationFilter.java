package com.totalizator.filter;

import com.totalizator.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filter for authentication and authorization.
 * Checks if user is logged in and has required role.
 * 
 * @author Totalizator Team
 * @version 1.0
 */
public class AuthenticationFilter implements Filter {
    private static final Logger logger = LogManager.getLogger();
    
    private static final String LOGIN_PAGE = "/login";
    private static final String LOGIN_ACTION = "/login";
    private static final String REGISTER_ACTION = "/register";
    private static final String STATIC_RESOURCES = "/css/";
    private static final String USER_ATTRIBUTE = "user";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            logger.info("AuthenticationFilter initializing...");
            // Filter initialization logic here if needed
            logger.info("AuthenticationFilter initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize AuthenticationFilter", e);
            throw new ServletException("Failed to initialize AuthenticationFilter", e);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(true);
        
        // Set default locale to English if not set
        if (session.getAttribute("locale") == null) {
            session.setAttribute("locale", java.util.Locale.ENGLISH);
        }
        
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());
        
        // Allow access to login and register pages, index.jsp, pages, and static resources
        if (path.equals(LOGIN_PAGE) || path.equals(LOGIN_ACTION) || path.equals(REGISTER_ACTION) 
                || path.equals("/") || path.equals("/index.jsp") || path.startsWith(STATIC_RESOURCES) 
                || path.startsWith("/api/") || path.startsWith("/locale") || path.startsWith("/pages/")
                || path.endsWith(".css") || path.endsWith(".js") || path.endsWith(".png") 
                || path.endsWith(".jpg") || path.endsWith(".gif") || path.endsWith(".ico")) {
            chain.doFilter(request, response);
            return;
        }
        
        // Check if user is logged in
        if (session == null || session.getAttribute(USER_ATTRIBUTE) == null) {
            logger.debug("Unauthenticated access attempt to: {}", path);
            httpResponse.sendRedirect(contextPath + LOGIN_PAGE);
            return;
        }
        
        User user = (User) session.getAttribute(USER_ATTRIBUTE);
        
        // Check role-based access
        if (path.startsWith("/admin") && !user.getRole().getName().equals("ADMIN")) {
            logger.warn("Unauthorized access attempt to admin area by user: {}", user.getUsername());
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        if (path.startsWith("/bookmaker") && !user.getRole().getName().equals("BOOKMAKER")) {
            logger.warn("Unauthorized access attempt to bookmaker area by user: {}", user.getUsername());
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        logger.info("AuthenticationFilter destroyed");
    }
}

