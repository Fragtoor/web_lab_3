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
    @SuppressWarnings("unchecked")
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

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html><html lang='ru'><head><meta charset='UTF-8'><title>Результат</title>");
        out.println("<link rel='stylesheet' href='" + request.getContextPath() + "/css/style.css'>");
        out.println("</head><body>");
        out.println("<table class='layout-table'><tr><td id='header'>Результат проверки попадания точки в область</td></tr>");
        out.println("<tr><td class='content-cell'>");

        if (errorMessage != null) {
            out.println("<p style='color: red; text-align: center; font-weight: bold;'>" + errorMessage + "</p>");
        } else {
            out.println("<table class='results-table'>");
            out.println("<thead><tr><th>X</th><th>Y</th><th>R</th><th>Результат</th><th>Текущее время</th><th>Время выполнения</th></tr></thead><tbody>");
            for (PointResult pt : currentBatch) {
                String color = pt.isHit() ? "green" : "red";
                String text = pt.isHit() ? "Попадание" : "Промах";
                out.println("<tr>");
                out.println("<td>" + pt.getX() + "</td>");
                out.println("<td>" + pt.getY() + "</td>");
                out.println("<td>" + pt.getR() + "</td>");
                out.println("<td style='color: " + color + "; font-weight: bold;'>" + text + "</td>");
                out.println("<td class='local-time' data-utc='" + pt.getCurrentTime() + "'></td>");
                out.println("<td>" + pt.getExecutionTime() + "</td>");
                out.println("</tr>");
            }
            out.println("</tbody></table>");
        }

        out.println("<div style='text-align: center; margin-top: 25px;'>");
        out.println("<a href='" + request.getContextPath() + "/controller' class='back-link'>Вернуться к веб-форме</a>");
        out.println("</div></td></tr></table>");

        out.println("<script>");
        out.println("document.querySelectorAll('.local-time').forEach(el => {");
        out.println("  const utcTime = el.getAttribute('data-utc');");
        out.println("  const date = new Date(utcTime);");
        out.println("  if (!isNaN(date)) {");
        out.println("    el.textContent = new Intl.DateTimeFormat('ru-RU', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', second: '2-digit' }).format(date);");
        out.println("  } else { el.textContent = utcTime; }");
        out.println("});");
        out.println("</script>");

        out.println("</body></html>");
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
