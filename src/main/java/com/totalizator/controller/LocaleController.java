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
        String contextPath = request.getContextPath();

        if (referer != null && !referer.isEmpty()) {
            try {

                if (referer.contains(contextPath)) {

                    int contextIndex = referer.indexOf(contextPath);
                    String pathAfterContext = referer.substring(contextIndex + contextPath.length());

                    int queryIndex = pathAfterContext.indexOf('?');
                    if (queryIndex >= 0) {
                        pathAfterContext = pathAfterContext.substring(0, queryIndex);
                    }

                    int fragmentIndex = pathAfterContext.indexOf('#');
                    if (fragmentIndex >= 0) {
                        pathAfterContext = pathAfterContext.substring(0, fragmentIndex);
                    }

                    if (pathAfterContext.isEmpty() || pathAfterContext.equals("/")) {
                        response.sendRedirect(contextPath + "/");
                    } else {

                        response.sendRedirect(contextPath + pathAfterContext);
                    }
                } else {

                    response.sendRedirect(contextPath + "/");
                }
            } catch (Exception e) {
                logger.warn("Error processing referer URL: {}", referer, e);
                response.sendRedirect(contextPath + "/");
            }
        } else {

            response.sendRedirect(contextPath + "/");
        }
    }
}

