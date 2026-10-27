package ru.itmo.web.servlet;

import ru.itmo.web.model.PointResult;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@WebServlet(name = "AreaCheckServlet", urlPatterns = {"/area-check"})
public class AreaCheckServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!Boolean.TRUE.equals(request.getAttribute("fromController"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Прямой доступ запрещен");
            return;
        }

        long startTime = System.nanoTime();
        String[] xParams = request.getParameterValues("x");
        String yParam = request.getParameter("y");
        String rParam = request.getParameter("r");

        List<PointResult> currentBatch = new ArrayList<>();
        String errorMessage = null;

        try {
            double y = Double.parseDouble(yParam.trim().replace(',', '.'));
            double r = Double.parseDouble(rParam.trim().replace(',', '.'));

            if (y <= -3.0 || y >= 3.0 || !isValidR(r)) {
                throw new IllegalArgumentException("Неверные параметры");
            }

            ServletContext context = getServletContext();
            List<PointResult> results;
            synchronized (context) {
                results = (List<PointResult>) context.getAttribute("results");
                if (results == null) {
                    results = Collections.synchronizedList(new ArrayList<>());
                    context.setAttribute("results", results);
                }
            }

            for (String xStr : xParams) {
                double x = Double.parseDouble(xStr.trim().replace(',', '.'));
                boolean hit = checkHit(x, y, r);
                double execTimeMs = (System.nanoTime() - startTime) / 1_000_000.0;
                String execTimeStr = String.format(Locale.US, "%.4f мс", execTimeMs);

                String nowStr = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);

                PointResult pt = new PointResult(x, y, r, hit, nowStr, execTimeStr);
                results.add(pt);
                currentBatch.add(pt);
            }
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }

        request.setAttribute("currentBatch", currentBatch);
        request.setAttribute("errorMessage", errorMessage);
        request.getRequestDispatcher("/WEB-INF/result.jsp").forward(request, response);
    }

    private boolean isValidR(double r) {
        double[] allowed = {1.0, 1.5, 2.0, 2.5, 3.0};
        for (double a : allowed) {
            if (Math.abs(r - a) < 1e-6) return true;
        }
        return false;
    }

    private boolean checkHit(double x, double y, double r) {
        if (x >= 0 && y >= 0) return (x * x + y * y) <= (r / 2.0) * (r / 2.0);
        if (x <= 0 && y >= 0) return x >= -r && y <= r;
        if (x <= 0 && y <= 0) return (x + y) >= -r;
        return false;
    }
}
