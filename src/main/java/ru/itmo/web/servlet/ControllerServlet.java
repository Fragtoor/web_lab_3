package ru.itmo.web.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "ControllerServlet", urlPatterns = {"/controller"})
public class ControllerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if ("clear".equals(request.getParameter("action"))) {
            ServletContext context = getServletContext();
            synchronized (context) {
                List<?> results = (List<?>) context.getAttribute("results");
                if (results != null) {
                    results.clear();
                }
            }

            response.sendRedirect(request.getContextPath() + "/controller");
            return;
        }

        String[] xParams = request.getParameterValues("x");
        String yParam = request.getParameter("y");
        String rParam = request.getParameter("r");

        if (xParams != null && xParams.length > 0 && yParam != null && rParam != null
                && !yParam.trim().isEmpty() && !rParam.trim().isEmpty()) {
            request.setAttribute("fromController", Boolean.TRUE);
            request.getRequestDispatcher("/area-check").forward(request, response);
        } else {
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }
}
