package ru.itmo.web.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@WebFilter("/*")
public class HeaderFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext context = filterConfig.getServletContext();
        ConcurrentHashMap<String, AtomicInteger> headerStats = new ConcurrentHashMap<>();
        context.setAttribute("headerStats", headerStats);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        ServletContext context = req.getServletContext();

        ConcurrentHashMap<String, AtomicInteger> headerStats =
                (ConcurrentHashMap<String, AtomicInteger>) context.getAttribute("headerStats");

        if (headerStats != null) {
            Enumeration<String> headerNames = req.getHeaderNames();
            while (headerNames != null && headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement().toLowerCase();
                headerStats.computeIfAbsent(headerName, k -> new AtomicInteger(0)).incrementAndGet();
            }
        }

        chain.doFilter(request, response);
    }
}
