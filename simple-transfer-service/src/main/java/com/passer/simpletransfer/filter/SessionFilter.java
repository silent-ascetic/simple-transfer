package com.passer.simpletransfer.filter;


import com.passer.simpletransfer.config.STWebSocketConfigurer;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;

import java.io.IOException;

@WebFilter(filterName = "sessionFilter",urlPatterns = {"/websocket/chat/*"})
@Order(1)
public class SessionFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req= (HttpServletRequest) servletRequest;
        req.getSession().setAttribute(STWebSocketConfigurer.IP_ADDR,req.getRemoteHost());
        filterChain.doFilter(servletRequest,servletResponse);
    }
}
