package com.totalizator.controller.bookmaker;

import com.totalizator.controller.BaseController;
import com.totalizator.model.Competition;
import com.totalizator.model.User;
import com.totalizator.service.CompetitionService;
import com.totalizator.service.factory.ServiceFactory;
import com.totalizator.util.ConnectionPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;


@WebServlet(name = "bookmakerController", urlPatterns = "/bookmaker/*")
public class BookmakerController extends HttpServlet {
    private static final Logger logger = LogManager.getLogger();
    private final CompetitionService competitionService;

    public BookmakerController() {
        this.competitionService = ServiceFactory.getInstance().getCompetitionService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        BaseController baseController = new BaseController() {};
        if (!baseController.requireAuthentication(request, response) 
                || !baseController.requireRole(request, response, "BOOKMAKER")) {
            return;
        }
        
        User user = baseController.getUserFromSession(request);
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {

            List<Competition> competitions = competitionService.findAll();
            request.setAttribute("competitions", competitions);
            request.setAttribute("user", user);
            request.getRequestDispatcher("/pages/bookmaker/competitions.jsp").forward(request, response);
        } else if (pathInfo.startsWith("/competition/")) {
            String competitionIdStr = pathInfo.substring("/competition/".length());
            try {
                int competitionId = Integer.parseInt(competitionIdStr);
                java.util.Optional<Competition> competitionOptional = competitionService.findById(competitionId);
                
                if (competitionOptional.isPresent()) {
                    Competition competition = competitionOptional.get();

                    request.setAttribute("competition", competition);
                    request.setAttribute("user", user);
                    request.getRequestDispatcher("/pages/bookmaker/set-odds.jsp").forward(request, response);
                } else {
                    response.sendRedirect(request.getContextPath() + "/bookmaker/");
                }
            } catch (NumberFormatException e) {
                logger.error("Invalid competition ID", e);
                response.sendRedirect(request.getContextPath() + "/bookmaker/");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        BaseController baseController = new BaseController() {};
        if (!baseController.requireAuthentication(request, response) 
                || !baseController.requireRole(request, response, "BOOKMAKER")) {
            return;
        }
        
        User user = baseController.getUserFromSession(request);
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo != null && pathInfo.equals("/odds/update")) {
            try {
                int competitionId = Integer.parseInt(request.getParameter("competitionId"));

                BigDecimal winMultiplier = new BigDecimal(request.getParameter("win_multiplier"));
                BigDecimal drawMultiplier = new BigDecimal(request.getParameter("draw_multiplier"));
                BigDecimal lossMultiplier = new BigDecimal(request.getParameter("loss_multiplier"));
                BigDecimal exactScoreMultiplier = new BigDecimal(request.getParameter("exact_score_multiplier"));
                BigDecimal totalOverMultiplier = new BigDecimal(request.getParameter("total_over_multiplier"));
                BigDecimal totalUnderMultiplier = new BigDecimal(request.getParameter("total_under_multiplier"));

                ConnectionPool connectionPool = ConnectionPool.getInstance();
                Connection connection = connectionPool.getConnection();
                
                try {

                    String sql = "INSERT INTO competition_bet_types (competition_id, bet_type_id, multiplier) " +
                                "VALUES (?, (SELECT id FROM bet_types WHERE name = ?), ?) " +
                                "ON DUPLICATE KEY UPDATE multiplier = ?";
                    
                    try (PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setInt(1, competitionId);
                        statement.setString(2, "WIN");
                        statement.setBigDecimal(3, winMultiplier);
                        statement.setBigDecimal(4, winMultiplier);
                        statement.executeUpdate();

                        statement.setInt(1, competitionId);
                        statement.setString(2, "DRAW");
                        statement.setBigDecimal(3, drawMultiplier);
                        statement.setBigDecimal(4, drawMultiplier);
                        statement.executeUpdate();

                        statement.setInt(1, competitionId);
                        statement.setString(2, "LOSS");
                        statement.setBigDecimal(3, lossMultiplier);
                        statement.setBigDecimal(4, lossMultiplier);
                        statement.executeUpdate();

                        statement.setInt(1, competitionId);
                        statement.setString(2, "EXACT_SCORE");
                        statement.setBigDecimal(3, exactScoreMultiplier);
                        statement.setBigDecimal(4, exactScoreMultiplier);
                        statement.executeUpdate();

                        statement.setInt(1, competitionId);
                        statement.setString(2, "TOTAL_OVER");
                        statement.setBigDecimal(3, totalOverMultiplier);
                        statement.setBigDecimal(4, totalOverMultiplier);
                        statement.executeUpdate();

                        statement.setInt(1, competitionId);
                        statement.setString(2, "TOTAL_UNDER");
                        statement.setBigDecimal(3, totalUnderMultiplier);
                        statement.setBigDecimal(4, totalUnderMultiplier);
                        statement.executeUpdate();
                    }
                    
                    logger.info("Bookmaker {} updated odds for competition {}", user.getUsername(), competitionId);
                } finally {
                    connectionPool.releaseConnection(connection);
                }
                
                response.sendRedirect(request.getContextPath() + "/bookmaker/");
            } catch (Exception e) {
                logger.error("Error updating odds", e);
                response.sendRedirect(request.getContextPath() + "/bookmaker/");
            }
        }
    }
}

