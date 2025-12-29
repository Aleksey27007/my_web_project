package com.totalizator.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;


public class EncodingFilter implements Filter {
    private static final Logger logger = LogManager.getLogger();
    private static final String ENCODING = "UTF-8";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            logger.info("EncodingFilter initializing...");
            logger.info("EncodingFilter initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize EncodingFilter", e);
            throw new ServletException("Failed to initialize EncodingFilter", e);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        request.setCharacterEncoding(ENCODING);
        response.setCharacterEncoding(ENCODING);
        response.setContentType("text/html;charset=" + ENCODING);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        logger.info("EncodingFilter destroyed");
    }
}

