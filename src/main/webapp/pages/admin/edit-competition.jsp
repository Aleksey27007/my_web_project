<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="${sessionScope.locale != null ? sessionScope.locale : 'en'}" />
<fmt:setBundle basename="messages" />
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Competition</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        nav { background-color: #333; padding: 10px; margin-bottom: 20px; border-radius: 4px; }
        nav a { color: white; text-decoration: none; margin-right: 20px; padding: 8px 16px; display: inline-block; }
        nav a:hover { background-color: #555; border-radius: 4px; }
        .form-container { max-width: 600px; padding: 20px; background-color: #f9f9f9; border-radius: 4px; }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; font-weight: bold; }
        .form-group input, .form-group textarea { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }
        .form-group textarea { height: 80px; resize: vertical; }
        .btn { padding: 8px 16px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 4px; border: none; cursor: pointer; }
        .btn:hover { background-color: #45a049; }
        .btn-secondary { background-color: #999; }
        .btn-secondary:hover { background-color: #777; }
        .error { color: #f44336; margin-bottom: 15px; padding: 10px; background-color: #ffebee; border-radius: 4px; }
    </style>
</head>
<body>
    <nav>
        <a href="${pageContext.request.contextPath}/admin/competitions">Back to Competitions</a>
        <a href="${pageContext.request.contextPath}/logout">Logout</a>
    </nav>
    <h1>Edit Competition</h1>
    
    <c:if test="${requestScope.error != null}">
        <div class="error">${requestScope.error}</div>
    </c:if>
    
    <div class="form-container">
        <form action="${pageContext.request.contextPath}/admin/competition/update" method="post">
            <input type="hidden" name="id" value="${competition.id}">
            
            <div class="form-group">
                <label for="title">Title *</label>
                <input type="text" id="title" name="title" value="${competition.title}" required>
            </div>
            
            <div class="form-group">
                <label for="description">Description</label>
                <textarea id="description" name="description">${competition.description}</textarea>
            </div>
            
            <div class="form-group">
                <label for="sportType">Sport Type *</label>
                <input type="text" id="sportType" name="sportType" value="${competition.sportType}" required>
            </div>
            
            <div class="form-group">
                <label for="team1">Team 1 *</label>
                <input type="text" id="team1" name="team1" value="${competition.team1}" required>
            </div>
            
            <div class="form-group">
                <label for="team2">Team 2 *</label>
                <input type="text" id="team2" name="team2" value="${competition.team2}" required>
            </div>
            
            <div class="form-group">
                <label for="startDate">Start Date *</label>
                <input type="datetime-local" id="startDate" name="startDate" 
                       value="${competition.startDate != null ? competition.startDate.toString().replace(' ', 'T').substring(0, 16) : ''}" 
                       required>
            </div>
            
            <button type="submit" class="btn">Update Competition</button>
            <a href="${pageContext.request.contextPath}/admin/competitions" class="btn btn-secondary">Cancel</a>
        </form>
    </div>
</body>
</html>

