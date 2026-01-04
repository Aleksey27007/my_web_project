<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="${sessionScope.locale != null ? sessionScope.locale : 'en'}" />
<fmt:setBundle basename="messages" />
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Bookmaker - Competitions</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        nav { background-color: #333; padding: 10px; margin-bottom: 20px; border-radius: 4px; }
        nav a { color: white; text-decoration: none; margin-right: 20px; padding: 8px 16px; display: inline-block; }
        nav a:hover { background-color: #555; border-radius: 4px; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 10px; border: 1px solid #ddd; }
        th { background-color: #4CAF50; color: white; }
        .btn { padding: 8px 16px; background-color: #2196F3; color: white; text-decoration: none; border-radius: 4px; }
        .btn:hover { background-color: #1976D2; }
    </style>
</head>
<body>
    <nav>
        <a href="${pageContext.request.contextPath}/">Home</a>
        <a href="${pageContext.request.contextPath}/logout">Logout</a>
    </nav>
    <h1>Bookmaker - Set Odds for Competitions</h1>
    
    <table>
        <thead>
            <tr>
                <th>Title</th>
                <th>Team 1</th>
                <th>Team 2</th>
                <th>Start Date</th>
                <th>Status</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="competition" items="${competitions}">
                <tr>
                    <td>${competition.title}</td>
                    <td>${competition.team1}</td>
                    <td>${competition.team2}</td>
                    <td>${competition.startDate}</td>
                    <td>${competition.status}</td>
                    <td>
                        <c:if test="${competition.status.name() == 'SCHEDULED'}">
                            <a href="${pageContext.request.contextPath}/bookmaker/competition/${competition.id}" class="btn">
                                Set Odds
                            </a>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>

