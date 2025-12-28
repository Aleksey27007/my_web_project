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
    <title><fmt:message key="bet.create" /></title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 600px;
            margin: 0 auto;
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            color: #333;
        }
        input[type="text"], input[type="number"], select {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
        .btn {
            padding: 10px 20px;
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
        .error {
            color: #f44336;
            margin-bottom: 15px;
            padding: 10px;
            background-color: #ffebee;
            border-radius: 4px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1><fmt:message key="bet.create" /></h1>
        
        <c:if test="${requestScope.error != null}">
            <div class="error">${requestScope.error}</div>
        </c:if>
        
        <form action="${pageContext.request.contextPath}/bets/create" method="post">
            <input type="hidden" name="competitionId" value="${competition.id}">
            
            <div class="form-group">
                <label><fmt:message key="competition.title" /></label>
                <input type="text" value="${competition.title}" readonly>
            </div>
            
            <div class="form-group">
                <label><fmt:message key="bet.type" /></label>
                <select name="betTypeId" required>
                    <option value="">Select bet type</option>
                    <c:forEach var="betType" items="${betTypes}">
                        <option value="${betType.id}">${betType.name} - ${betType.description}</option>
                    </c:forEach>
                </select>
            </div>
            
            <div class="form-group">
                <label><fmt:message key="bet.amount" /></label>
                <input type="number" name="amount" step="0.01" min="0.01" required>
            </div>
            
            <div class="form-group">
                <label><fmt:message key="bet.predictedValue" /></label>
                <input type="text" name="predictedValue" required placeholder="e.g., TEAM1, DRAW, 2:1">
            </div>
            
            <button type="submit" class="btn"><fmt:message key="bet.place" /></button>
            <a href="${pageContext.request.contextPath}/" class="btn" style="background-color: #999;"><fmt:message key="common.cancel" /></a>
        </form>
    </div>
</body>
</html>

