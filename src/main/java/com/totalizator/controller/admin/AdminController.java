package com.totalizator.controller.admin;

import com.totalizator.controller.RoleController;
import com.totalizator.model.Competition;
import com.totalizator.model.User;
import com.totalizator.service.CompetitionService;
import com.totalizator.service.UserService;
import com.totalizator.service.impl.CompetitionServiceImpl;
import com.totalizator.service.impl.UserServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Admin controller for managing competitions and users.
 * Implements RoleController for role-based access.
 * 
 * @author Totalizator Team
 * @version 1.0
 */
@WebServlet(name = "adminController", urlPatterns = "/admin/*")
public class AdminController extends HttpServlet implements RoleController {
    private static final Logger logger = LogManager.getLogger();
    private final CompetitionService competitionService;
    private final UserService userService;

    public AdminController() {
        this.competitionService = new CompetitionServiceImpl();
        this.userService = new UserServiceImpl();
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
        if (!user.getRole().getName().equals("ADMIN")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            List<Competition> competitions = competitionService.findAll();
            List<User> users = userService.findAll();
            
            request.setAttribute("competitions", competitions);
            request.setAttribute("users", users);
            request.setAttribute("user", user);
            request.getRequestDispatcher("/pages/admin/dashboard.jsp").forward(request, response);
        } else if (pathInfo.startsWith("/competitions")) {
            List<Competition> competitions = competitionService.findAll();
            request.setAttribute("competitions", competitions);
            request.setAttribute("user", user);
            request.getRequestDispatcher("/pages/admin/competitions.jsp").forward(request, response);
        } else if (pathInfo.startsWith("/users")) {
            List<User> users = userService.findAll();
            request.setAttribute("users", users);
            request.setAttribute("user", user);
            request.getRequestDispatcher("/pages/admin/users.jsp").forward(request, response);
        } else if (pathInfo.startsWith("/competition/generate/")) {
            String competitionIdStr = pathInfo.substring("/competition/generate/".length());
            try {
                int competitionId = Integer.parseInt(competitionIdStr);
                competitionService.generateRandomResult(competitionId);
                com.totalizator.service.BetService betService = 
                        new com.totalizator.service.impl.BetServiceImpl();
                betService.processBetsForCompetition(competitionId);
                response.sendRedirect(request.getContextPath() + "/admin/competitions");
            } catch (Exception e) {
                logger.error("Error generating result", e);
                response.sendRedirect(request.getContextPath() + "/admin/competitions");
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
        if (!user.getRole().getName().equals("ADMIN")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo != null && pathInfo.equals("/competition/create")) {
            try {
                Competition competition = new Competition();
                competition.setTitle(request.getParameter("title"));
                competition.setDescription(request.getParameter("description"));
                competition.setSportType(request.getParameter("sportType"));
                competition.setStartDate(LocalDateTime.parse(request.getParameter("startDate")));
                competition.setTeam1(request.getParameter("team1"));
                competition.setTeam2(request.getParameter("team2"));
                competition.setStatus(Competition.CompetitionStatus.SCHEDULED);
                
                competitionService.createCompetition(competition);
                response.sendRedirect(request.getContextPath() + "/admin/competitions");
            } catch (Exception e) {
                logger.error("Error creating competition", e);
                response.sendRedirect(request.getContextPath() + "/admin/competitions");
            }
        } else if (pathInfo != null && pathInfo.startsWith("/user/delete/")) {
            try {
                int userId = Integer.parseInt(pathInfo.substring("/user/delete/".length()));
                userService.deleteUser(userId);
                response.sendRedirect(request.getContextPath() + "/admin/users");
            } catch (Exception e) {
                logger.error("Error deleting user", e);
                response.sendRedirect(request.getContextPath() + "/admin/users");
            }
        }
    }
}
