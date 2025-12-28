<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="${sessionScope.locale != null ? sessionScope.locale : 'en'}" />
<fmt:setBundle basename="messages" />
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><fmt:message key="admin.title" /></title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        nav { background-color: #333; padding: 10px; margin-bottom: 20px; border-radius: 4px; }
        nav a { color: white; text-decoration: none; margin-right: 20px; padding: 8px 16px; display: inline-block; }
        nav a:hover { background-color: #555; border-radius: 4px; }
        .section { margin-top: 30px; }
        .btn { padding: 8px 16px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 4px; }
    </style>
</head>
<body>
    <nav>
        <a href="${pageContext.request.contextPath}/"><fmt:message key="nav.home" /></a>
        <a href="${pageContext.request.contextPath}/admin/competitions"><fmt:message key="admin.competitions" /></a>
        <a href="${pageContext.request.contextPath}/admin/users"><fmt:message key="admin.users" /></a>
        <a href="${pageContext.request.contextPath}/logout"><fmt:message key="nav.logout" /></a>
    </nav>
    
    <h1><fmt:message key="admin.title" /></h1>
    
    <div class="section">
        <h2><fmt:message key="admin.competitions" /></h2>
        <a href="${pageContext.request.contextPath}/admin/competitions" class="btn"><fmt:message key="admin.competitions" /></a>
    </div>
    
    <div class="section">
        <h2><fmt:message key="admin.users" /></h2>
        <a href="${pageContext.request.contextPath}/admin/users" class="btn"><fmt:message key="admin.users" /></a>
    </div>
</body>
</html>

