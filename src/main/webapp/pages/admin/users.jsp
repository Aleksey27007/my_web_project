<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="${sessionScope.locale != null ? sessionScope.locale : 'en'}" />
<fmt:setBundle basename="messages" />
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><fmt:message key="admin.users" /></title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        nav { background-color: #333; padding: 10px; margin-bottom: 20px; border-radius: 4px; }
        nav a { color: white; text-decoration: none; margin-right: 20px; padding: 8px 16px; display: inline-block; }
        nav a:hover { background-color: #555; border-radius: 4px; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 10px; border: 1px solid #ddd; }
        th { background-color: #4CAF50; color: white; }
        .btn { padding: 8px 16px; background-color: #f44336; color: white; text-decoration: none; border-radius: 4px; }
    </style>
</head>
<body>
    <nav>
        <a href="${pageContext.request.contextPath}/admin/"><fmt:message key="nav.home" /></a>
        <a href="${pageContext.request.contextPath}/logout"><fmt:message key="nav.logout" /></a>
    </nav>
    <h1><fmt:message key="admin.users" /></h1>
    <c:if test="${requestScope.error != null}">
        <div style="color: #f44336; margin-bottom: 15px; padding: 10px; background-color: #ffebee; border-radius: 4px;">
            ${requestScope.error}
        </div>
    </c:if>
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Username</th>
                <th>Email</th>
                <th>Role</th>
                <th>Balance</th>
                <th><fmt:message key="common.action" /></th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="user" items="${users}">
                <tr>
                    <td>${user.id}</td>
                    <td>${user.username}</td>
                    <td>${user.email}</td>
                    <td>${user.role.name}</td>
                    <td><fmt:formatNumber value="${user.balance}" minFractionDigits="2" maxFractionDigits="2" /></td>
                    <td>
                        <a href="${pageContext.request.contextPath}/admin/user/delete/${user.id}" class="btn">
                            <fmt:message key="common.delete" />
                        </a>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>

