<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="${sessionScope.locale != null ? sessionScope.locale : 'en'}" />
<fmt:setBundle basename="messages" />
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Set Odds - ${competition.title}</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        nav { background-color: #333; padding: 10px; margin-bottom: 20px; border-radius: 4px; }
        nav a { color: white; text-decoration: none; margin-right: 20px; padding: 8px 16px; display: inline-block; }
        nav a:hover { background-color: #555; border-radius: 4px; }
        .form-container { max-width: 600px; padding: 20px; background-color: #f9f9f9; border-radius: 4px; }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; font-weight: bold; }
        .form-group input { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }
        .btn { padding: 8px 16px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 4px; border: none; cursor: pointer; }
        .btn:hover { background-color: #45a049; }
        .btn-secondary { background-color: #999; }
        .btn-secondary:hover { background-color: #777; }
        .competition-info { margin-bottom: 20px; padding: 15px; background-color: #e3f2fd; border-radius: 4px; }
    </style>
</head>
<body>
    <nav>
        <a href="${pageContext.request.contextPath}/bookmaker/">Back to Competitions</a>
        <a href="${pageContext.request.contextPath}/logout">Logout</a>
    </nav>
    
    <div class="competition-info">
        <h2>${competition.title}</h2>
        <p><strong>Team 1:</strong> ${competition.team1}</p>
        <p><strong>Team 2:</strong> ${competition.team2}</p>
        <p><strong>Start Date:</strong> ${competition.startDate}</p>
    </div>
    
    <h1>Set Odds (Multipliers) for Bet Types</h1>
    
    <div class="form-container">
        <form action="${pageContext.request.contextPath}/bookmaker/odds/update" method="post">
            <input type="hidden" name="competitionId" value="${competition.id}">
            
            <div class="form-group">
                <label for="win_multiplier">WIN (Team 1 wins) Multiplier</label>
                <input type="number" id="win_multiplier" name="win_multiplier" step="0.01" min="1.0" value="2.00" required>
            </div>
            
            <div class="form-group">
                <label for="draw_multiplier">DRAW Multiplier</label>
                <input type="number" id="draw_multiplier" name="draw_multiplier" step="0.01" min="1.0" value="3.00" required>
            </div>
            
            <div class="form-group">
                <label for="loss_multiplier">LOSS (Team 2 wins) Multiplier</label>
                <input type="number" id="loss_multiplier" name="loss_multiplier" step="0.01" min="1.0" value="2.00" required>
            </div>
            
            <div class="form-group">
                <label for="exact_score_multiplier">EXACT_SCORE Multiplier</label>
                <input type="number" id="exact_score_multiplier" name="exact_score_multiplier" step="0.01" min="1.0" value="5.00" required>
            </div>
            
            <div class="form-group">
                <label for="total_over_multiplier">TOTAL_OVER Multiplier</label>
                <input type="number" id="total_over_multiplier" name="total_over_multiplier" step="0.01" min="1.0" value="1.80" required>
            </div>
            
            <div class="form-group">
                <label for="total_under_multiplier">TOTAL_UNDER Multiplier</label>
                <input type="number" id="total_under_multiplier" name="total_under_multiplier" step="0.01" min="1.0" value="1.80" required>
            </div>
            
            <button type="submit" class="btn">Update Odds</button>
            <a href="${pageContext.request.contextPath}/bookmaker/" class="btn btn-secondary">Cancel</a>
        </form>
    </div>
</body>
</html>

