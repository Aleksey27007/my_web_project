<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="${sessionScope.locale != null ? sessionScope.locale : 'en'}" />
<fmt:setBundle basename="messages" />
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><fmt:message key="admin.competitions" /></title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        nav { background-color: #333; padding: 10px; margin-bottom: 20px; border-radius: 4px; }
        nav a { color: white; text-decoration: none; margin-right: 20px; padding: 8px 16px; display: inline-block; }
        nav a:hover { background-color: #555; border-radius: 4px; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 10px; border: 1px solid #ddd; }
        th { background-color: #4CAF50; color: white; }
        .btn { padding: 8px 16px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 4px; }
    </style>
</head>
<body>
    <nav>
        <a href="${pageContext.request.contextPath}/admin/"><fmt:message key="nav.home" /></a>
        <a href="${pageContext.request.contextPath}/logout"><fmt:message key="nav.logout" /></a>
    </nav>
    <h1><fmt:message key="admin.competitions" /></h1>
    <table>
        <thead>
            <tr>
                <th><fmt:message key="competition.title" /></th>
                <th><fmt:message key="competition.team1" /></th>
                <th><fmt:message key="competition.team2" /></th>
                <th><fmt:message key="competition.status" /></th>
                <th><fmt:message key="common.action" /></th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="competition" items="${competitions}">
                <tr>
                    <td>${competition.title}</td>
                    <td>${competition.team1}</td>
                    <td>${competition.team2}</td>
                    <td>${competition.status}</td>
                    <td>
                        <c:if test="${competition.status.name() == 'SCHEDULED'}">
                            <a href="${pageContext.request.contextPath}/admin/competition/generate/${competition.id}" class="btn">
                                <fmt:message key="admin.generate.result" />
                            </a>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>

