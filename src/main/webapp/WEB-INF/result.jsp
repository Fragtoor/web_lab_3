<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, ru.itmo.web.model.PointResult" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Результат проверки</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <table class="layout-table">
        <tr>
            <td id="header">Результат проверки попадания точки в область</td>
        </tr>
        <tr>
            <td class="content-cell">
                <%
                    String error = (String) request.getAttribute("errorMessage");
                    if (error != null) {
                %>
                    <p style="color: red; text-align: center; font-weight: bold;"><%= error %></p>
                <%
                    } else {
                        List<PointResult> batch = (List<PointResult>) request.getAttribute("currentBatch");
                %>
                    <table class="results-table">
                        <thead>
                            <tr>
                                <th>X</th>
                                <th>Y</th>
                                <th>R</th>
                                <th>Результат</th>
                                <th>Текущее время</th>
                                <th>Время выполнения</th>
                            </tr>
                        </thead>
                        <tbody>
                        <%
                            if (batch != null) {
                                for (PointResult pt : batch) {
                                    String color = pt.isHit() ? "green" : "red";
                                    String status = pt.isHit() ? "Попадание" : "Промах";
                        %>
                            <tr>
                                <td><%= pt.getX() %></td>
                                <td><%= pt.getY() %></td>
                                <td><%= pt.getR() %></td>
                                <td style="color: <%= color %>; font-weight: bold;"><%= status %></td>
                                <td class="local-time" data-utc="<%= pt.getCurrentTime() %>"></td>
                                <td><%= pt.getExecutionTime() %></td>
                            </tr>
                        <%
                                }
                            }
                        %>
                        </tbody>
                    </table>
                <%  } %>

                <div style="text-align: center; margin-top: 25px;">
                    <a href="${pageContext.request.contextPath}/controller" class="back-link">Вернуться к веб-форме</a>
                </div>
            </td>
        </tr>
    </table>

    <script>
        document.querySelectorAll('.local-time').forEach(el => {
            const utcTime = el.getAttribute('data-utc');
            const date = new Date(utcTime);
            if (!isNaN(date.getTime())) {
                el.textContent = new Intl.DateTimeFormat('ru-RU', {
                    year: 'numeric', month: '2-digit', day: '2-digit',
                    hour: '2-digit', minute: '2-digit', second: '2-digit'
                }).format(date);
            } else {
                el.textContent = utcTime;
            }
        });
    </script>
</body>
</html>
