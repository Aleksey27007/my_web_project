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
        .btn-danger { background-color: #f44336; }
        .form-container { margin-bottom: 30px; padding: 20px; background-color: #f9f9f9; border-radius: 4px; }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; font-weight: bold; }
        .form-group input, .form-group textarea, .form-group select { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }
        .form-group textarea { height: 80px; resize: vertical; }
        .error { color: #f44336; margin-bottom: 15px; padding: 10px; background-color: #ffebee; border-radius: 4px; }
        .success { color: #4CAF50; margin-bottom: 15px; padding: 10px; background-color: #e8f5e9; border-radius: 4px; }
    </style>
</head>
<body>
    <nav>
        <a href="${pageContext.request.contextPath}/admin/"><fmt:message key="nav.home" /></a>
        <a href="${pageContext.request.contextPath}/logout"><fmt:message key="nav.logout" /></a>
    </nav>
    <h1><fmt:message key="admin.competitions" /></h1>
    
    <c:if test="${requestScope.error != null}">
        <div class="error">${requestScope.error}</div>
    </c:if>
    <c:if test="${requestScope.success != null}">
        <div class="success">${requestScope.success}</div>
    </c:if>
    
    <div class="form-container">
        <h2><fmt:message key="admin.create.competition" /></h2>
        <form action="${pageContext.request.contextPath}/admin/competition/create" method="post">
            <div class="form-group">
                <label for="title"><fmt:message key="competition.title" /> *</label>
                <input type="text" id="title" name="title" required>
            </div>
            <div class="form-group">
                <label for="description">Description</label>
                <textarea id="description" name="description"></textarea>
            </div>
            <div class="form-group">
                <label for="sportType">Sport Type *</label>
                <input type="text" id="sportType" name="sportType" required placeholder="e.g., Football, Basketball">
            </div>
            <div class="form-group">
                <label for="team1"><fmt:message key="competition.team1" /> *</label>
                <input type="text" id="team1" name="team1" required>
            </div>
            <div class="form-group">
                <label for="team2"><fmt:message key="competition.team2" /> *</label>
                <input type="text" id="team2" name="team2" required>
            </div>
            <div class="form-group">
                <label for="startDate"><fmt:message key="competition.startDate" /> *</label>
                <input type="datetime-local" id="startDate" name="startDate" required>
            </div>
            <button type="submit" class="btn"><fmt:message key="admin.create.competition" /></button>
        </form>
    </div>
    
    <h2>Existing Competitions</h2>
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

