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
    <title><fmt:message key="register.title" /></title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f5f5f5;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            margin: 0;
            padding: 20px;
        }
        .register-container {
            background-color: white;
            padding: 40px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            width: 100%;
            max-width: 400px;
        }
        h1 {
            text-align: center;
            color: #333;
            margin-bottom: 30px;
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            color: #333;
        }
        input[type="text"], input[type="email"], input[type="password"] {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
        .btn {
            width: 100%;
            padding: 10px;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
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
        .login-link {
            text-align: center;
            margin-top: 20px;
        }
        .login-link a {
            color: #4CAF50;
            text-decoration: none;
        }
    </style>
</head>
<body>
    <div class="register-container">
        <h1><fmt:message key="register.title" /></h1>
        
        <c:if test="${requestScope.error != null}">
            <div class="error">${requestScope.error}</div>
        </c:if>
        
        <form action="${pageContext.request.contextPath}/register" method="post" onsubmit="return validateForm()">
            <div class="form-group">
                <label><fmt:message key="register.username" /></label>
                <input type="text" name="username" id="username" required minlength="3" maxlength="50">
            </div>
            <div class="form-group">
                <label><fmt:message key="register.email" /></label>
                <input type="email" name="email" id="email" required>
            </div>
            <div class="form-group">
                <label><fmt:message key="register.password" /></label>
                <input type="password" name="password" id="password" required minlength="6">
            </div>
            <div class="form-group">
                <label><fmt:message key="register.confirmPassword" /></label>
                <input type="password" name="confirmPassword" id="confirmPassword" required>
            </div>
            <div class="form-group">
                <label><fmt:message key="register.firstName" /></label>
                <input type="text" name="firstName" id="firstName">
            </div>
            <div class="form-group">
                <label><fmt:message key="register.lastName" /></label>
                <input type="text" name="lastName" id="lastName">
            </div>
            <button type="submit" class="btn"><fmt:message key="register.submit" /></button>
        </form>
        
        <div class="login-link">
            <a href="${pageContext.request.contextPath}/login"><fmt:message key="nav.login" /></a>
        </div>
    </div>
    
    <script>
        function validateForm() {
            var password = document.getElementById("password").value;
            var confirmPassword = document.getElementById("confirmPassword").value;
            
            if (password !== confirmPassword) {
                alert("Passwords do not match");
                return false;
            }
            return true;
        }
    </script>
</body>
</html>

