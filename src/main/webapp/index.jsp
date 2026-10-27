<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="ru.itmo.web.model.PointResult" %>
<%
    @SuppressWarnings("unchecked")
    List<PointResult> results = (List<PointResult>) application.getAttribute("results");
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Лабораторная работа 3</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <table class="layout-table">
        <tr>
            <td id="header" colspan="2">
                Сивков Александр Владимирович | P3216 | Вариант: 8482
            </td>
        </tr>

        <tr>
            <td class="content-cell">
                <canvas id="graphCanvas" width="400" height="400" style="display: block; margin: 0 auto; cursor: crosshair;"></canvas>
                <div id="canvas-error" class="error" style="text-align: center; margin-top: 10px;">
                    Невозможно определить координаты точки: сначала выберите радиус R!
                </div>
            </td>

            <td class="content-cell">
                <form id="pointForm" method="GET" action="${pageContext.request.contextPath}/controller">
                    <div>
                        <strong>Изменение X (Checkbox):</strong>
                        <br>
                        <label><input type="checkbox" name="x" value="-2"> -2</label>
                        <label><input type="checkbox" name="x" value="-1.5"> -1.5</label>
                        <label><input type="checkbox" name="x" value="-1"> -1</label>
                        <label><input type="checkbox" name="x" value="-0.5"> -0.5</label>
                        <label><input type="checkbox" name="x" value="0"> 0</label>
                        <label><input type="checkbox" name="x" value="0.5"> 0.5</label>
                        <label><input type="checkbox" name="x" value="1"> 1</label>
                        <label><input type="checkbox" name="x" value="1.5"> 1.5</label>
                        <label><input type="checkbox" name="x" value="2"> 2</label>
                        <div id="x-error" class="error">Выберите хотя бы одно значение X!</div>
                    </div>
                    <br>

                    <div>
                        <strong>Изменение Y (-3 ... 3):</strong>
                        <br>
                        <input type="text" id="y-val" name="y" placeholder="Введите Y (от -3 до 3)" autocomplete="off" maxlength="15">
                        <div id="y-error" class="error">Введите корректное число в интервале (-3; 3)!</div>
                    </div>
                    <br>

                    <div>
                        <strong>Изменение R (Button):</strong>
                        <br>
                        <input type="hidden" id="r-val" name="r" value="">
                        <input type="button" class="r-btn" value="1" onclick="setR(1, this)">
                        <input type="button" class="r-btn" value="1.5" onclick="setR(1.5, this)">
                        <input type="button" class="r-btn" value="2" onclick="setR(2, this)">
                        <input type="button" class="r-btn" value="2.5" onclick="setR(2.5, this)">
                        <input type="button" class="r-btn" value="3" onclick="setR(3, this)">
                        <div id="r-error" class="error">Выберите значение радиуса R!</div>
                    </div>
                    <br>

                    <button type="submit" style="width: 90%; margin: 2%;">Проверить точку</button>
                    <button type="button" id="clearBtn" style="width: 90%; margin: 2%; background-color: #6c757d;">Очистить таблицу</button>
                </form>
            </td>
        </tr>

        <tr>
            <td colspan="2" class="content-cell">
                <table class="results-table" id="resultsTable">
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
                    <tbody id="results-body">
                    </tbody>
                </table>
            </td>
        </tr>
    </table>

    <script>
        const CONTEXT_PATH = "${pageContext.request.contextPath}";
        let SERVER_POINTS = [
            <% if (results != null) {
                   synchronized (application) {
                       for (int i = 0; i < results.size(); i++) {
                           PointResult pt = results.get(i); %>
            { x: <%= pt.getX() %>, y: <%= pt.getY() %>, r: <%= pt.getR() %>, hit: <%= pt.isHit() %>, time: "<%= pt.getCurrentTime() %>", exec_time: "<%= pt.getExecutionTime() %>" }<%= (i < results.size() - 1) ? "," : "" %>
            <%         }
                   }
               } %>
        ];
</script>
    <script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>
