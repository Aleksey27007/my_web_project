package com.totalizator.controller.admin;

import com.totalizator.dao.BookmakerDao;
import com.totalizator.model.Competition;
import com.totalizator.model.User;
import com.totalizator.service.CompetitionService;
import com.totalizator.service.factory.DaoFactory;
import com.totalizator.service.factory.ServiceFactory;
import com.totalizator.util.ValidationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@WebServlet(name = "bookmakerController", urlPatterns = "/bookmaker/*")
public class BookmakerController extends HttpServlet {
    private static final Logger logger = LogManager.getLogger();
    private CompetitionService competitionService;
    private BookmakerDao bookmakerDao;
    private ValidationUtil validationUtil;

    @Override
    public void init() throws ServletException {
        super.init();
        ServiceFactory serviceFactory = ServiceFactory.getInstance();
        competitionService = serviceFactory.getCompetitionService();
        bookmakerDao = DaoFactory.getInstance().getBookmakerDao();
        validationUtil = new ValidationUtil();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!validationUtil.requireAuthentication(request, response)
                || !validationUtil.requireRole(request, response, "BOOKMAKER")) {
            return;
        }

        User user = validationUtil.getUserFromSession(request);

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
                Optional<Competition> competitionOptional = competitionService.findById(competitionId);

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
            throws IOException {
        if (!validationUtil.requireAuthentication(request, response)
                || !validationUtil.requireRole(request, response, "BOOKMAKER")) {
            return;
        }

        User user = validationUtil.getUserFromSession(request);

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

                bookmakerDao.updateOddsCompetition(competitionId, winMultiplier, drawMultiplier,
                        lossMultiplier, exactScoreMultiplier, totalOverMultiplier, totalUnderMultiplier);

                logger.info("Bookmaker {} updated odds for competition {}", user.getUsername(), competitionId);


                response.sendRedirect(request.getContextPath() + "/bookmaker/");
            } catch (Exception e) {
                logger.error("Error updating odds", e);
                response.sendRedirect(request.getContextPath() + "/bookmaker/");
            }
        }
    }
}

