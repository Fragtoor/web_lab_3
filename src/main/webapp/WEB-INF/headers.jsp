<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.concurrent.ConcurrentHashMap" %>
<%@ page import="java.util.concurrent.atomic.AtomicInteger" %>
<%
    ConcurrentHashMap<String, AtomicInteger> headerStats =
            (ConcurrentHashMap<String, AtomicInteger>) application.getAttribute("headerStats");
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Статистика HTTP-заголовков</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .stats-table { width: 60%; margin: 20px auto; border-collapse: collapse; background: white; }
        .stats-table th, .stats-table td { padding: 12px; border: 1px solid #ddd; text-align: left; }
        .stats-table th { background-color: #3399FF; color: white; }
    </style>
</head>
<body>
    <table class="layout-table">
        <tr>
            <td id="header">Статистика перехваченных HTTP-заголовков</td>
        </tr>
        <tr>
            <td class="content-cell">
                <% if (headerStats == null || headerStats.isEmpty()) { %>
                    <p style="text-align: center;">Статистика пока пуста. Сделайте несколько запросов.</p>
                <% } else { %>
                    <table class="stats-table">
                        <thead>
                            <tr>
                                <th>Имя заголовка</th>
                                <th>Количество упоминаний</th>
                            </tr>
                        </thead>
                        <tbody>
                        <% for (Map.Entry<String, AtomicInteger> entry : headerStats.entrySet()) { %>
                            <tr>
                                <td><%= entry.getKey() %></td>
                                <td><%= entry.getValue().get() %></td>
                            </tr>
                        <% } %>
                        </tbody>
                    </table>
                <% } %>
                <div style="text-align: center; margin-top: 20px;">
                    <a href="${pageContext.request.contextPath}/controller"
                       style="text-decoration:none; padding:10px 15px; background-color:#6c757d; color:white; border-radius:4px;">
                       Вернуться на главную
                    </a>
                </div>
            </td>
        </tr>
    </table>
</body>
</html>
