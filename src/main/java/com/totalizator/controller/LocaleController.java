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
        
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            response.sendRedirect(referer);
        } else {
            response.sendRedirect(request.getContextPath() + "/");
        }
    }
}

