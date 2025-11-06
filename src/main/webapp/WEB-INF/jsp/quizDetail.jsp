<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html>
<head>
    <title>Détail Quiz</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css"/>
    <style>
        .meta { display:grid; grid-template-columns: 180px 1fr; gap:8px 16px; margin-bottom:16px; }
        .q-item { padding:12px 14px; border:1px solid rgba(255,255,255,0.08); border-radius:12px; margin-bottom:10px; }
        .badge { display:inline-block; padding:2px 8px; border-radius:999px; font-size:12px; background:rgba(255,255,255,0.08); }
        .choices { margin-top:6px; padding-left:16px; }
        .ok { opacity:.95; }
    </style>
</head>
<body>
<div class="container">
    <div class="card">
        <div style="display:flex; align-items:center; justify-content:space-between;">
            <h1><c:out value="${quiz.title}"/></h1>
            <div style="display:flex; gap:8px;">
                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/quizzes">← Retour</a>
                <a class="btn" href="${pageContext.request.contextPath}/quizzes/new">+ Nouveau quiz</a>
            </div>
        </div>

        <div class="meta">
            <div>Propriétaire</div><div><c:out value="${quiz.owner.username}"/></div>
            <div>Description</div><div><c:out value="${quiz.description}"/></div>
            <div>Temps/question</div><div>${quiz.timePerQuestionSec} s</div>
            <div>Nombre de questions</div><div>${quiz.questions.size()}</div>
        </div>

        <h2>Questions</h2>
        <c:if test="${empty quiz.questions}">
            <div class="small">Aucune question.</div>
        </c:if>

        <c:forEach items="${quiz.questions}" var="q">
            <div class="q-item">
                <div style="display:flex; justify-content:space-between; align-items:center;">
                    <div>
                        <span class="badge">#${q.orderIndex}</span>
                        <strong style="margin-left:8px;"><c:out value="${q.label}"/></strong>
                    </div>
                    <div class="small" style="opacity:.8">
                        Points: ${q.points} • Limite: ${q.timeLimitSeconds}s
                    </div>
                </div>

                <!-- Type spécifique -->
                <c:choose>
                    <%-- True/False --%>
                    <c:when test="${q.type == 'TF'}">
                        <div class="small" style="margin-top:6px;">
                            Réponse correcte :
                            <c:choose>
                                <c:when test="${q.correct}"><b>Vrai</b></c:when>
                                <c:otherwise><b>Faux</b></c:otherwise>
                            </c:choose>
                        </div>
                    </c:when>

                    <%-- Short text --%>
                    <c:when test="${q.type == 'SHORT'}">
                        <div class="small" style="margin-top:6px;">
                            Regex attendue :
                            <code><c:out value="${q.expectedRegex}"/></code>
                        </div>
                    </c:when>

                    <%-- MCQ --%>
                    <c:when test="${q.type == 'MCQ'}">
                        <div class="small" style="margin-top:6px;">
                            Multi-select : ${q.multiSelect}
                        </div>
                        <ul class="choices">
                            <c:forEach items="${q.choices}" var="c">
                                <li>• <c:out value="${c.text}"/>
                                    <c:if test="${c.correctAnswer}"><span class="ok">(✔)</span></c:if>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:when>

                    <c:otherwise>
                        <div class="small" style="margin-top:6px;">Type de question inconnu.</div>
                    </c:otherwise>
                </c:choose>

            </div>
        </c:forEach>
    </div>
</div>
</body>
</html>
