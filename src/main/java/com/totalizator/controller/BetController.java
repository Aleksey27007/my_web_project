package com.totalizator.controller;

import com.totalizator.model.Bet;
import com.totalizator.model.BetType;
import com.totalizator.model.Competition;
import com.totalizator.model.User;
import com.totalizator.service.BetService;
import com.totalizator.service.CompetitionService;
import com.totalizator.dao.Dao;
import com.totalizator.service.factory.DaoFactory;
import com.totalizator.service.impl.BetServiceImpl;
import com.totalizator.service.impl.CompetitionServiceImpl;
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
import java.util.List;
import java.util.Optional;

/**
 * Controller for bet operations.
 * 
 * @author Totalizator Team
 * @version 1.0
 */
@WebServlet(name = "betController", urlPatterns = "/bets/*")
public class BetController extends HttpServlet {
    private static final Logger logger = LogManager.getLogger();
    private final BetService betService;
    private final CompetitionService competitionService;
    private final Dao<BetType, Integer> betTypeDao;

    public BetController() {
        this.betService = new BetServiceImpl();
        this.competitionService = new CompetitionServiceImpl();
        this.betTypeDao = DaoFactory.getInstance().getBetTypeDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            List<Bet> bets = betService.findByUserId(user.getId());
            request.setAttribute("bets", bets);
            request.setAttribute("user", user);
            request.getRequestDispatcher("/pages/bets.jsp").forward(request, response);
        } else if (pathInfo.startsWith("/create/")) {
            String competitionIdStr = pathInfo.substring("/create/".length());
            try {
                int competitionId = Integer.parseInt(competitionIdStr);
                Optional<Competition> competitionOptional = competitionService.findById(competitionId);
                if (!competitionOptional.isPresent()) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                
                List<BetType> betTypes = betTypeDao.findAll();
                request.setAttribute("competition", competitionOptional.get());
                request.setAttribute("betTypes", betTypes);
                request.setAttribute("user", user);
                request.getRequestDispatcher("/pages/create-bet.jsp").forward(request, response);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        String pathInfo = request.getPathInfo();
        
        if (pathInfo != null && pathInfo.equals("/create")) {
            try {
                int competitionId = Integer.parseInt(request.getParameter("competitionId"));
                int betTypeId = Integer.parseInt(request.getParameter("betTypeId"));
                BigDecimal amount = new BigDecimal(request.getParameter("amount"));
                String predictedValue = request.getParameter("predictedValue");
                
                Optional<Competition> competitionOptional = competitionService.findById(competitionId);
                Optional<BetType> betTypeOptional = betTypeDao.findById(betTypeId);
                
                if (!competitionOptional.isPresent() || !betTypeOptional.isPresent()) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                
                Bet bet = new Bet();
                bet.setUser(user);
                bet.setCompetition(competitionOptional.get());
                bet.setBetType(betTypeOptional.get());
                bet.setAmount(amount);
                bet.setPredictedValue(predictedValue);
                
                betService.placeBet(bet);
                logger.info("Bet placed by user {}: competition={}, amount={}", 
                        user.getUsername(), competitionId, amount);
                
                response.sendRedirect(request.getContextPath() + "/bets/");
            } catch (Exception e) {
                logger.error("Error placing bet", e);
                request.setAttribute("error", e.getMessage());
                request.getRequestDispatcher("/pages/create-bet.jsp").forward(request, response);
            }
        } else if (pathInfo != null && pathInfo.startsWith("/cancel/")) {
            try {
                int betId = Integer.parseInt(pathInfo.substring("/cancel/".length()));
                betService.cancelBet(betId);
                response.sendRedirect(request.getContextPath() + "/bets/");
            } catch (Exception e) {
                logger.error("Error cancelling bet", e);
                response.sendRedirect(request.getContextPath() + "/bets/");
            }
        }
    }
}
