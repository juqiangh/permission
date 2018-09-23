package com.learning.filter;

import com.learning.common.RequestHolder;
import com.learning.model.SysUser;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class LoginFilter implements Filter{

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        SysUser sysUser = (SysUser) httpServletRequest.getSession().getAttribute("user");
        if (sysUser == null) {
            String path = "/signin.jsp";
            httpServletResponse.sendRedirect(path);
            return;
        }
        RequestHolder.add(sysUser);
        RequestHolder.add(httpServletRequest);
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }

}
