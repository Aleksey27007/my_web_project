<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="${sessionScope.locale != null ? sessionScope.locale : 'en'}" />
<fmt:setBundle basename="messages" />
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><fmt:message key="bet.title" /></title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        nav {
            background-color: #333;
            padding: 10px;
            margin-bottom: 20px;
            border-radius: 4px;
        }
        nav a {
            color: white;
            text-decoration: none;
            margin-right: 20px;
            padding: 8px 16px;
            display: inline-block;
        }
        nav a:hover {
            background-color: #555;
            border-radius: 4px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #4CAF50;
            color: white;
        }
        tr:hover {
            background-color: #f5f5f5;
        }
        .btn {
            padding: 8px 16px;
            background-color: #f44336;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
        }
        .btn:hover {
            background-color: #da190b;
        }
    </style>
</head>
<body>
    <div class="container">
        <nav>
            <a href="${pageContext.request.contextPath}/"><fmt:message key="nav.home" /></a>
            <a href="${pageContext.request.contextPath}/logout"><fmt:message key="nav.logout" /></a>
        </nav>
        
        <h1><fmt:message key="bet.title" /></h1>
        
        <c:choose>
            <c:when test="${bets != null && !empty bets}">
                <table>
                    <thead>
                        <tr>
                            <th><fmt:message key="bet.competition" /></th>
                            <th><fmt:message key="bet.type" /></th>
                            <th><fmt:message key="bet.amount" /></th>
                            <th><fmt:message key="bet.predictedValue" /></th>
                            <th><fmt:message key="bet.status" /></th>
                            <th><fmt:message key="bet.winAmount" /></th>
                            <th><fmt:message key="common.action" /></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="bet" items="${bets}">
                            <tr>
                                <td>${bet.competition.title}</td>
                                <td>${bet.betType.name}</td>
                                <td><fmt:formatNumber value="${bet.amount}" minFractionDigits="2" maxFractionDigits="2" /></td>
                                <td>${bet.predictedValue}</td>
                                <td>${bet.status}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${bet.winAmount != null}">
                                            <fmt:formatNumber value="${bet.winAmount}" minFractionDigits="2" maxFractionDigits="2" />
                                        </c:when>
                                        <c:otherwise>-</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:if test="${bet.status.name() == 'PENDING'}">
                                        <a href="${pageContext.request.contextPath}/bets/cancel/${bet.id}" class="btn">
                                            <fmt:message key="bet.cancel" />
                                        </a>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <p><fmt:message key="bet.no.bets" /></p>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>

