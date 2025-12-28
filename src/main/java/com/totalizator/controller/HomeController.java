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
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Home page controller.
 * 
 * @author Totalizator Team
 * @version 1.0
 */
@WebServlet(name = "homeController", urlPatterns = "/")
public class HomeController extends HttpServlet {
    private final CompetitionService competitionService;

    public HomeController() {
        this.competitionService = new CompetitionServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            User user = session != null ? (User) session.getAttribute("user") : null;
            
            List<Competition> competitions = competitionService.findAll();
            
            request.setAttribute("competitions", competitions);
            request.setAttribute("user", user);
            
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        } catch (Exception e) {
            org.apache.logging.log4j.LogManager.getLogger(HomeController.class)
                    .error("Error in HomeController", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Error loading page: " + e.getMessage());
        }
    }
}
