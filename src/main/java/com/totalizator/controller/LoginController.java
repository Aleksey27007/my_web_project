package com.totalizator.controller;

import com.totalizator.model.User;
import com.totalizator.service.UserService;
import com.totalizator.service.factory.ServiceFactory;

import com.totalizator.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;


@WebServlet(name = "loginController", urlPatterns = "/login")
public class LoginController extends HttpServlet {
    private final UserService userService = ServiceFactory.getInstance().getUserService();


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        
        request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        if (!ValidationUtil.areAllNotEmpty(username, password)) {
            request.setAttribute("error", "Username and password are required");
            request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
            return;
        }
        
        Optional<User> userOptional = userService.authenticate(username, password);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            HttpSession session = request.getSession();
            session.setAttribute("user", user);

            response.sendRedirect(request.getContextPath() + "/");
        } else {
            request.setAttribute("error", "Invalid username or password");
            request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
        }
    }
}
