package com.totalizator.controller.admin;

import com.totalizator.model.Competition;
import com.totalizator.model.Role;
import com.totalizator.model.User;
import com.totalizator.service.BetService;
import com.totalizator.service.CompetitionService;
import com.totalizator.service.UserService;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@WebServlet(name = "adminController", urlPatterns = "/admin/*")
public class AdminController extends HttpServlet {
    private static final Logger logger = LogManager.getLogger();
    private ValidationUtil validationUtil;
    private CompetitionService competitionService;
    private UserService userService;


    @Override
    public void init() throws ServletException {
        super.init();
        ServiceFactory serviceFactory = ServiceFactory.getInstance();
        competitionService = serviceFactory.getCompetitionService();
        userService = serviceFactory.getUserService();
        validationUtil = new ValidationUtil();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!validationUtil.requireAuthentication(request, response)
                || !validationUtil.requireRole(request, response, "ADMIN")) {
            return;
        }
        
        User user = validationUtil.getUserFromSession(request);
        
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
                BetService betService = ServiceFactory.getInstance().getBetService();
                betService.processBetsForCompetition(competitionId);
                response.sendRedirect(request.getContextPath() + "/admin/competitions");
            } catch (Exception e) {
                logger.error("Error generating result", e);
                response.sendRedirect(request.getContextPath() + "/admin/competitions");
            }
        } else if (pathInfo.startsWith("/user/delete/")) {
            try {
                String userIdStr = pathInfo.substring("/user/delete/".length());
                int userId = Integer.parseInt(userIdStr);

                if (userId == user.getId()) {
                    logger.warn("Admin {} attempted to delete themselves", user.getUsername());
                    request.setAttribute("error", "You cannot delete your own account");
                    List<User> users = userService.findAll();
                    request.setAttribute("users", users);
                    request.setAttribute("user", user);
                    request.getRequestDispatcher("/pages/admin/users.jsp").forward(request, response);
                    return;
                }
                
                logger.info("Attempting to delete user with id: {}", userId);
                boolean deleted = userService.deleteUser(userId);
                if (deleted) {
                    logger.info("User with id {} deleted successfully", userId);
                } else {
                    logger.warn("Failed to delete user with id: {}", userId);
                }
                response.sendRedirect(request.getContextPath() + "/admin/users");
            } catch (NumberFormatException e) {
                logger.error("Invalid user ID format", e);
                response.sendRedirect(request.getContextPath() + "/admin/users");
            } catch (Exception e) {
                logger.error("Error deleting user", e);
                response.sendRedirect(request.getContextPath() + "/admin/users");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!validationUtil.requireAuthentication(request, response)
                || !validationUtil.requireRole(request, response, "ADMIN")) {
            return;
        }
        
        User user = validationUtil.getUserFromSession(request);
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo != null && pathInfo.equals("/competition/create")) {
            try {
                Competition competition = new Competition();
                generateCompetitionFromRequest(request, competition);
                competition.setStatus(Competition.CompetitionStatus.SCHEDULED);
                
                competitionService.createCompetition(competition);
                logger.info("Competition created successfully: {}", competition.getTitle());
                response.sendRedirect(request.getContextPath() + "/admin/competitions");
            } catch (Exception e) {
                logger.error("Error creating competition", e);
                request.setAttribute("error", "Error creating competition: " + e.getMessage());
                List<Competition> competitions = competitionService.findAll();
                request.setAttribute("competitions", competitions);
                request.setAttribute("user", user);
                request.getRequestDispatcher("/pages/admin/competitions.jsp").forward(request, response);
            }
        } else if (pathInfo != null && pathInfo.equals("/user/create")) {
            try {
                String username = request.getParameter("username");
                String email = request.getParameter("email");
                String password = request.getParameter("password");
                String firstName = request.getParameter("firstName");
                String lastName = request.getParameter("lastName");
                String roleName = request.getParameter("role");
                
                if (username == null || email == null || password == null || roleName == null ||
                    username.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {
                    request.setAttribute("error", "All required fields must be filled");
                    List<User> users = userService.findAll();
                    request.setAttribute("users", users);
                    request.setAttribute("user", user);
                    request.getRequestDispatcher("/pages/admin/users.jsp").forward(request, response);
                    return;
                }

                Role role = switch (roleName.toUpperCase()) {
                    case "ADMIN" -> new Role(1, "ADMIN", "Администратор системы");
                    case "BOOKMAKER" -> new Role(2, "BOOKMAKER", "Букмекер");
                    default -> new Role(3, "CLIENT", "Клиент");
                };

                User newUser = new User();
                newUser.setUsername(username);
                newUser.setEmail(email);
                newUser.setPassword(password);
                newUser.setFirstName(firstName);
                newUser.setLastName(lastName);
                newUser.setRole(role);
                newUser.setBalance(BigDecimal.ZERO);
                newUser.setActive(true);
                
                userService.register(newUser);
                logger.info("User created successfully by admin: {}", username);
                response.sendRedirect(request.getContextPath() + "/admin/users");
            } catch (Exception e) {
                logger.error("Error creating user", e);
                request.setAttribute("error", "Error creating user: " + e.getMessage());
                List<User> users = userService.findAll();
                request.setAttribute("users", users);
                request.setAttribute("user", user);
                request.getRequestDispatcher("/pages/admin/users.jsp").forward(request, response);
            }
        } else if (pathInfo != null && pathInfo.startsWith("/competition/edit/")) {
            try {
                String competitionIdStr = pathInfo.substring("/competition/edit/".length());
                int competitionId = Integer.parseInt(competitionIdStr);
                Optional<Competition> competitionOptional = competitionService.findById(competitionId);
                
                if (competitionOptional.isPresent()) {
                    Competition competition = competitionOptional.get();
                    request.setAttribute("competition", competition);
                    request.setAttribute("user", user);
                    request.getRequestDispatcher("/pages/admin/edit-competition.jsp").forward(request, response);
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/competitions");
                }
            } catch (Exception e) {
                logger.error("Error loading competition for edit", e);
                response.sendRedirect(request.getContextPath() + "/admin/competitions");
            }
        } else if (pathInfo != null && pathInfo.equals("/competition/update")) {
            try {
                int competitionId = Integer.parseInt(request.getParameter("id"));
                Optional<Competition> competitionOptional = competitionService.findById(competitionId);
                
                if (competitionOptional.isEmpty()) {
                    request.setAttribute("error", "Competition not found");
                    List<Competition> competitions = competitionService.findAll();
                    request.setAttribute("competitions", competitions);
                    request.setAttribute("user", user);
                    request.getRequestDispatcher("/pages/admin/competitions.jsp").forward(request, response);
                    return;
                }
                
                Competition competition = competitionOptional.get();
                generateCompetitionFromRequest(request, competition);

                competitionService.updateCompetition(competition);
                logger.info("Competition updated successfully: {}", competition.getTitle());
                response.sendRedirect(request.getContextPath() + "/admin/competitions");
            } catch (Exception e) {
                logger.error("Error updating competition", e);
                request.setAttribute("error", "Error updating competition: " + e.getMessage());
                List<Competition> competitions = competitionService.findAll();
                request.setAttribute("competitions", competitions);
                request.setAttribute("user", user);
                request.getRequestDispatcher("/pages/admin/competitions.jsp").forward(request, response);
            }
        }
    }

    private void generateCompetitionFromRequest(HttpServletRequest request, Competition competition) {
        competition.setTitle(request.getParameter("title"));
        competition.setDescription(request.getParameter("description"));
        competition.setSportType(request.getParameter("sportType"));

        String startDateStr = request.getParameter("startDate");
        if (startDateStr != null && !startDateStr.isEmpty()) {

            startDateStr = startDateStr.replace("T", " ");
            if (startDateStr.length() == 16) {
                startDateStr += ":00";
            }
            competition.setStartDate(LocalDateTime.parse(startDateStr,
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        competition.setTeam1(request.getParameter("team1"));
        competition.setTeam2(request.getParameter("team2"));
    }
}
