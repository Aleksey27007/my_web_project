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
        .btn-success { background-color: #4CAF50; }
        .form-container { margin-bottom: 30px; padding: 20px; background-color: #f9f9f9; border-radius: 4px; }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; font-weight: bold; }
        .form-group input, .form-group select { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }
        .error { color: #f44336; margin-bottom: 15px; padding: 10px; background-color: #ffebee; border-radius: 4px; }
        .success { color: #4CAF50; margin-bottom: 15px; padding: 10px; background-color: #e8f5e9; border-radius: 4px; }
    </style>
</head>
<body>
    <nav>
        <a href="${pageContext.request.contextPath}/admin/"><fmt:message key="nav.home" /></a>
        <a href="${pageContext.request.contextPath}/logout"><fmt:message key="nav.logout" /></a>
    </nav>
    <h1><fmt:message key="admin.users" /></h1>
    
    <c:if test="${requestScope.error != null}">
        <div class="error">${requestScope.error}</div>
    </c:if>
    <c:if test="${requestScope.success != null}">
        <div class="success">${requestScope.success}</div>
    </c:if>
    
    <div class="form-container">
        <h2>Create New User</h2>
        <form action="${pageContext.request.contextPath}/admin/user/create" method="post">
            <div class="form-group">
                <label for="username">Username *</label>
                <input type="text" id="username" name="username" required>
            </div>
            <div class="form-group">
                <label for="email">Email *</label>
                <input type="email" id="email" name="email" required>
            </div>
            <div class="form-group">
                <label for="password">Password *</label>
                <input type="password" id="password" name="password" required>
            </div>
            <div class="form-group">
                <label for="firstName">First Name</label>
                <input type="text" id="firstName" name="firstName">
            </div>
            <div class="form-group">
                <label for="lastName">Last Name</label>
                <input type="text" id="lastName" name="lastName">
            </div>
            <div class="form-group">
                <label for="role">Role *</label>
                <select id="role" name="role" required>
                    <option value="CLIENT">CLIENT</option>
                    <option value="BOOKMAKER">BOOKMAKER</option>
                    <option value="ADMIN">ADMIN</option>
                </select>
            </div>
            <button type="submit" class="btn btn-success">Create User</button>
        </form>
    </div>
    
    <h2>Existing Users</h2>
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

