package com.dollapi.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class WebLoginFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String account = httpServletRequest.getSession().getAttribute("account") == null ? null : httpServletRequest.getSession().getAttribute("account").toString();
        String url = httpServletRequest.getRequestURI();
        if (url.contains("admin/user/login") || url.contains("admin/user/webLogin")) {

        } else {
            if (account == null || !account.equals("efun")) {
                HttpServletResponse response = (HttpServletResponse) servletResponse;
                response.sendRedirect("/admin/user/login");
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
