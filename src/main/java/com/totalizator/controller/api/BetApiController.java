package com.totalizator.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.totalizator.model.Bet;
import com.totalizator.model.User;
import com.totalizator.service.BetService;
import com.totalizator.service.impl.BetServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * REST API controller for bet operations.
 * Web Service implementation.
 * 
 * @author Totalizator Team
 * @version 1.0
 */
@WebServlet(name = "betApiController", urlPatterns = "/api/bets/*")
public class BetApiController extends HttpServlet {
    private static final Logger logger = LogManager.getLogger();
    private final BetService betService;
    private final ObjectMapper objectMapper;

    public BetApiController() {
        this.betService = new BetServiceImpl();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        User user = (User) session.getAttribute("user");
        String pathInfo = request.getPathInfo();
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Get all bets for current user
                List<Bet> bets = betService.findByUserId(user.getId());
                objectMapper.writeValue(response.getWriter(), bets);
            } else if (pathInfo.startsWith("/competition/")) {
                // Get bets for a competition
                String competitionIdStr = pathInfo.substring("/competition/".length());
                int competitionId = Integer.parseInt(competitionIdStr);
                List<Bet> bets = betService.findByCompetitionId(competitionId);
                objectMapper.writeValue(response.getWriter(), bets);
            } else {
                // Get bet by id
                String betIdStr = pathInfo.substring(1);
                int betId = Integer.parseInt(betIdStr);
                betService.findById(betId).ifPresent(bet -> {
                    try {
                        objectMapper.writeValue(response.getWriter(), bet);
                    } catch (IOException e) {
                        logger.error("Error writing bet to response", e);
                    }
                });
            }
        } catch (Exception e) {
            logger.error("Error processing API request", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

