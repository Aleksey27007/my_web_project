package com.totalizator.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.totalizator.model.Competition;
import com.totalizator.service.CompetitionService;
import com.totalizator.service.impl.CompetitionServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * REST API controller for competition operations.
 * Web Service implementation.
 * 
 * @author Totalizator Team
 * @version 1.0
 */
@WebServlet(name = "competitionApiController", urlPatterns = "/api/competitions/*")
public class CompetitionApiController extends HttpServlet {
    private static final Logger logger = LogManager.getLogger();
    private final CompetitionService competitionService;
    private final ObjectMapper objectMapper;

    public CompetitionApiController() {
        this.competitionService = new CompetitionServiceImpl();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Get all competitions
                List<Competition> competitions = competitionService.findAll();
                objectMapper.writeValue(response.getWriter(), competitions);
            } else if (pathInfo.startsWith("/status/")) {
                // Get competitions by status
                String status = pathInfo.substring("/status/".length());
                List<Competition> competitions = competitionService.findByStatus(status);
                objectMapper.writeValue(response.getWriter(), competitions);
            } else {
                // Get competition by id
                String competitionIdStr = pathInfo.substring(1);
                int competitionId = Integer.parseInt(competitionIdStr);
                competitionService.findById(competitionId).ifPresent(competition -> {
                    try {
                        objectMapper.writeValue(response.getWriter(), competition);
                    } catch (IOException e) {
                        logger.error("Error writing competition to response", e);
                    }
                });
            }
        } catch (Exception e) {
            logger.error("Error processing API request", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

