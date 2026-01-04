package com.totalizator.controller;

import com.totalizator.model.Role;
import com.totalizator.model.User;
import com.totalizator.service.UserService;
import com.totalizator.service.factory.ServiceFactory;

import com.totalizator.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;


@WebServlet(name = "registerController", urlPatterns = "/register")
public class RegisterController extends HttpServlet {
    private final UserService userService = ServiceFactory.getInstance().getUserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/pages/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        
        if (!ValidationUtil.areAllNotEmpty(username, email, password)) {
            request.setAttribute("error", "All required fields must be filled");
            request.getRequestDispatcher("/pages/register.jsp").forward(request, response);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match");
            request.getRequestDispatcher("/pages/register.jsp").forward(request, response);
            return;
        }
        
        try {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setBalance(BigDecimal.ZERO);
            user.setRole(new Role(3, "CLIENT", "Клиент"));
            
            userService.register(user);

            response.sendRedirect(request.getContextPath() + "/login");
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/pages/register.jsp").forward(request, response);
        }
    }
}
