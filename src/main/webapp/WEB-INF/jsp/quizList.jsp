<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html>
<head>
    <title>Quizzes</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css"/>
</head>
<body>
<div class="container">
    <div class="card">
        <h1>Mes Quizzes</h1>

        <ul class="clean">
            <c:forEach items="${quizzes}" var="q">
                <li class="quiz-item">
                    <div>
                        <span class="badge">${q.questions.size()} Q</span>
                        <strong><c:out value="${q.title}"/></strong>
                        <div class="small"><c:out value="${q.description}"/></div>
                    </div>
                    <div class="actions">
                        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/quizzes/new">Créer un quiz</a>
                    </div>
                </li>
            </c:forEach>
            <c:if test="${empty quizzes}">
                <li class="quiz-item">
                    <div class="small">Aucun quiz pour l’instant.</div>
                    <a class="btn" href="${pageContext.request.contextPath}/quizzes/new">Créer mon premier quiz</a>
                </li>
            </c:if>
        </ul>
    </div>
</div>
</body>
</html>
