package com.totalizator.controller;

import com.totalizator.model.Competition;
import com.totalizator.model.User;
import com.totalizator.service.CompetitionService;
import com.totalizator.service.impl.CompetitionServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

/**
 * Home page controller.
 * 
 * @author Totalizator Team
 * @version 1.0
 */
@WebServlet(name = "homeController", urlPatterns = "/")
public class HomeController extends HttpServlet {
    private static final Logger logger = LogManager.getLogger();
    private final CompetitionService competitionService;

    public HomeController() {
        this.competitionService = new CompetitionServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(true);
            
            // Set default locale to English if not set
            if (session.getAttribute("locale") == null) {
                session.setAttribute("locale", java.util.Locale.ENGLISH);
            }
            
            User user = (User) session.getAttribute("user");
            
            logger.info("HomeController: Starting to load competitions");
            List<Competition> competitions;
            try {
                competitions = competitionService.findAll();
                logger.info("HomeController: competitionService.findAll() returned: {}", 
                        competitions != null ? "list with " + competitions.size() + " items" : "null");
            } catch (Exception e) {
                logger.error("HomeController: Error calling competitionService.findAll()", e);
                competitions = new java.util.ArrayList<>();
            }
            
            if (competitions == null) {
                logger.warn("HomeController: competitions is null, initializing empty list");
                competitions = new java.util.ArrayList<>();
            }
            
            logger.info("HomeController: Setting competitions attribute with {} items for user: {}", 
                    competitions.size(), 
                    user != null ? user.getUsername() : "anonymous");
            
            request.setAttribute("competitions", competitions);
            request.setAttribute("user", user);
            
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error in HomeController", e);
            // Set empty list instead of null to prevent JSP errors
            request.setAttribute("competitions", new java.util.ArrayList<>());
            request.setAttribute("user", null);
            try {
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            } catch (Exception ex) {
                logger.error("Error forwarding to index.jsp", ex);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                        "Error loading page: " + e.getMessage());
            }
        }
    }
}
