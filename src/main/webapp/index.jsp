<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.totalizator.service.CompetitionService" %>
<%@ page import="com.totalizator.service.impl.CompetitionServiceImpl" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    // Load competitions if not already set in request
    if (request.getAttribute("competitions") == null) {
        try {
            CompetitionService competitionService = new CompetitionServiceImpl();
            List competitions = competitionService.findAll();
            if (competitions == null) {
                competitions = new java.util.ArrayList();
            }
            request.setAttribute("competitions", competitions);
            
            // Set default locale if not set
            if (session.getAttribute("locale") == null) {
                session.setAttribute("locale", java.util.Locale.ENGLISH);
            }
        } catch (Exception e) {
            request.setAttribute("competitions", new java.util.ArrayList());
            request.setAttribute("errorMessage", "Error loading competitions: " + e.getMessage());
        }
    }
%>
<fmt:setLocale value="${sessionScope.locale != null ? sessionScope.locale : 'en'}" />
<fmt:setBundle basename="messages" />
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><fmt:message key="app.title" /></title>
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
        .locale-selector {
            float: right;
            color: white;
        }
        .locale-selector a {
            color: #ccc;
            font-size: 0.9em;
        }
        h1 {
            color: #333;
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
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
        }
        .btn:hover {
            background-color: #45a049;
        }
    </style>
</head>
<body>
    <div class="container">
        <nav>
            <a href="${pageContext.request.contextPath}/"><fmt:message key="nav.home" /></a>
            <c:choose>
                <c:when test="${sessionScope.user == null}">
                    <a href="${pageContext.request.contextPath}/login"><fmt:message key="nav.login" /></a>
                    <a href="${pageContext.request.contextPath}/register"><fmt:message key="nav.register" /></a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/bets/"><fmt:message key="nav.bets" /></a>
                    <c:if test="${sessionScope.user.role.name == 'ADMIN'}">
                        <a href="${pageContext.request.contextPath}/admin/"><fmt:message key="nav.admin" /></a>
                    </c:if>
                    <c:if test="${sessionScope.user.role.name == 'BOOKMAKER'}">
                        <a href="${pageContext.request.contextPath}/bookmaker/">Bookmaker</a>
                    </c:if>
                    <a href="${pageContext.request.contextPath}/logout"><fmt:message key="nav.logout" /></a>
                </c:otherwise>
            </c:choose>
            <div class="locale-selector">
                <a href="${pageContext.request.contextPath}/locale?lang=en">EN</a> |
                <a href="${pageContext.request.contextPath}/locale?lang=be">BE</a> |
                <a href="${pageContext.request.contextPath}/locale?lang=de">DE</a>
            </div>
        </nav>
        
        <h1><fmt:message key="home.title" /></h1>
        
        <h2><fmt:message key="home.competitions" /></h2>
        
        <c:if test="${errorMessage != null}">
            <div style="background-color: #ffebee; color: #c62828; padding: 10px; border-radius: 4px; margin-bottom: 20px;">
                <strong>Error:</strong> ${errorMessage}
            </div>
        </c:if>
        
        <c:choose>
            <c:when test="${competitions != null && competitions.size() > 0}">
                <p style="color: green; margin-bottom: 10px;">Found ${competitions.size()} competition(s)</p>
                <table>
                    <thead>
                        <tr>
                            <th><fmt:message key="competition.title" /></th>
                            <th><fmt:message key="competition.team1" /></th>
                            <th><fmt:message key="competition.team2" /></th>
                            <th><fmt:message key="competition.startDate" /></th>
                            <th><fmt:message key="competition.status" /></th>
                            <c:if test="${sessionScope.user != null}">
                                <th><fmt:message key="common.action" /></th>
                            </c:if>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="competition" items="${competitions}" varStatus="loop">
                            <tr>
                                <td><c:out value="${competition.title}" default="N/A" /></td>
                                <td><c:out value="${competition.team1}" default="N/A" /></td>
                                <td><c:out value="${competition.team2}" default="N/A" /></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${competition.startDate != null}">
                                            ${competition.startDate}
                                        </c:when>
                                        <c:otherwise>N/A</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${competition.status != null}">
                                            <c:set var="statusName" value="${competition.status.name()}" />
                                            <c:choose>
                                                <c:when test="${statusName == 'SCHEDULED'}"><fmt:message key="status.SCHEDULED" /></c:when>
                                                <c:when test="${statusName == 'IN_PROGRESS'}"><fmt:message key="status.IN_PROGRESS" /></c:when>
                                                <c:when test="${statusName == 'FINISHED'}"><fmt:message key="status.FINISHED" /></c:when>
                                                <c:when test="${statusName == 'CANCELLED'}"><fmt:message key="status.CANCELLED" /></c:when>
                                                <c:otherwise>${statusName}</c:otherwise>
                                            </c:choose>
                                        </c:when>
                                        <c:otherwise>N/A</c:otherwise>
                                    </c:choose>
                                </td>
                                <c:if test="${sessionScope.user != null}">
                                    <td>
                                        <c:if test="${competition.status != null && competition.status.name() == 'SCHEDULED'}">
                                            <a href="${pageContext.request.contextPath}/bets/create/${competition.id}" class="btn">
                                                <fmt:message key="competition.bet" />
                                            </a>
                                        </c:if>
                                    </td>
                                </c:if>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:when test="${competitions != null && empty competitions}">
                <p style="color: orange;"><fmt:message key="home.no.competitions" /></p>
                <p style="color: #666; font-size: 0.9em;">There are no competitions available at the moment. Please check back later.</p>
            </c:when>
            <c:otherwise>
                <p style="color: red;">Error loading competitions. Please try again later.</p>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>

