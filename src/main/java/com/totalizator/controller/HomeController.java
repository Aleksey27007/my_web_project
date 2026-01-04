package com.totalizator.controller;

import com.totalizator.model.Competition;
import com.totalizator.model.User;
import com.totalizator.service.CompetitionService;
import com.totalizator.service.factory.ServiceFactory;

import com.totalizator.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@WebServlet(name = "homeController", urlPatterns = "/")
public class HomeController extends HttpServlet {
    private static final Logger logger = LogManager.getLogger();
    private final CompetitionService competitionService = ServiceFactory.getInstance().getCompetitionService();
    private static final ValidationUtil validationUtil = new ValidationUtil();


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("HomeController.doGet() called for URI: {}", request.getRequestURI());

        validationUtil.ensureDefaultLocale(request);
        User user = validationUtil.getUserFromSession(request);
        
        logger.info("HomeController: Starting to load competitions for user: {}", 
                user != null ? user.getUsername() : "anonymous");
        
        List<Competition> competitions = loadCompetitions();
        request.setAttribute("competitions", competitions);
        request.setAttribute("user", user);
        
        logger.info("HomeController: Forwarding to /index.jsp with {} competitions", competitions.size());
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
    
    
    private List<Competition> loadCompetitions() {
        try {
            List<Competition> competitions = competitionService.findAll();
            if (competitions == null) {
                logger.warn("HomeController: competitions is null, returning empty list");
                return new ArrayList<>();
            }
            logger.info("HomeController: Loaded {} competitions", competitions.size());
            return competitions;
        } catch (Exception e) {
            logger.error("HomeController: Error loading competitions", e);
            return new ArrayList<>();
        }
    }
}
