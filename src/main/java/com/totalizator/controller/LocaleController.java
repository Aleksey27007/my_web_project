package com.totalizator.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Locale;

/**
 * Controller for changing application locale.
 * 
 * @author Totalizator Team
 * @version 1.0
 */
@WebServlet(name = "localeController", urlPatterns = "/locale")
public class LocaleController extends HttpServlet {
    private static final Logger logger = LogManager.getLogger();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String localeParam = request.getParameter("lang");
        Locale locale = Locale.ENGLISH; // default
        
        if (localeParam != null) {
            switch (localeParam.toLowerCase()) {
                case "be":
                    locale = Locale.forLanguageTag("be-BY");
                    break;
                case "de":
                    locale = Locale.GERMAN;
                    break;
                case "ru":
                    locale = Locale.forLanguageTag("ru-RU");
                    break;
                default:
                    locale = Locale.ENGLISH;
            }
        }
        
        HttpSession session = request.getSession();
        session.setAttribute("locale", locale);
        logger.info("Locale changed to: {}", locale);
        
        // Get the referer URL
        String referer = request.getHeader("Referer");
        String contextPath = request.getContextPath();
        
        // Try to redirect back to the same page
        if (referer != null && !referer.isEmpty()) {
            try {
                // Check if referer contains our context path
                if (referer.contains(contextPath)) {
                    // Extract the path part after context path
                    int contextIndex = referer.indexOf(contextPath);
                    String pathAfterContext = referer.substring(contextIndex + contextPath.length());
                    
                    // Remove query parameters if any
                    int queryIndex = pathAfterContext.indexOf('?');
                    if (queryIndex >= 0) {
                        pathAfterContext = pathAfterContext.substring(0, queryIndex);
                    }
                    
                    // Remove fragment if any
                    int fragmentIndex = pathAfterContext.indexOf('#');
                    if (fragmentIndex >= 0) {
                        pathAfterContext = pathAfterContext.substring(0, fragmentIndex);
                    }
                    
                    // If path is empty or just "/", go to home
                    if (pathAfterContext.isEmpty() || pathAfterContext.equals("/")) {
                        response.sendRedirect(contextPath + "/");
                    } else {
                        // Redirect to the same page
                        response.sendRedirect(contextPath + pathAfterContext);
                    }
                } else {
                    // Referer doesn't contain context path, go to home
                    response.sendRedirect(contextPath + "/");
                }
            } catch (Exception e) {
                logger.warn("Error processing referer URL: {}", referer, e);
                response.sendRedirect(contextPath + "/");
            }
        } else {
            // No referer, go to home
            response.sendRedirect(contextPath + "/");
        }
    }
}

