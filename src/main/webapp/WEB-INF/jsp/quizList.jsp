<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html>
<head>
    <title>Quizzes</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css"/>
    <style>
        .toolbar { display:flex; gap:10px; justify-content:flex-end; margin-bottom:16px; }
        .table { width:100%; border-collapse:collapse; }
        .table th, .table td { padding:12px 14px; border-bottom:1px solid rgba(255,255,255,0.06); }
        .table th { text-align:left; opacity:.85; font-weight:600; }
        .table tr:hover { background: rgba(255,255,255,0.03); }
        .badge { display:inline-block; padding:3px 8px; border-radius:999px; font-size:12px; opacity:.9; background:rgba(255,255,255,0.08); }
        .right { text-align:right; }
    </style>
</head>
<body>
<div class="container">
    <div class="card">
        <div style="display:flex; align-items:center; justify-content:space-between;">
            <h1>Quizzes</h1>
            <div class="toolbar">
                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/logout">Logout</a>
                <a class="btn" href="${pageContext.request.contextPath}/quizzes/new">+ Nouveau quiz</a>
            </div>
        </div>

        <c:choose>
            <c:when test="${not empty rows}">
                <table class="table">
                    <thead>
                    <tr>
                        <th style="width:80px">ID</th>
                        <th>Titre</th>
                        <th style="width:220px">Auteur</th>
                        <th class="right" style="width:140px">Questions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${rows}" var="r">
                        <tr>
                            <td>${r.id}</td>
                            <td>
                                <a href="${pageContext.request.contextPath}/quizzes/view?id=${r.id}">
                                <strong><c:out value="${r.title}"/></strong>
                                </a>
                            </td>
                            <td><c:out value="${r.author}"/></td>
                            <td class="right"><span class="badge">${r.questionCount} items</span></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <div class="small" style="opacity:.8">Aucun quiz pour l’instant.</div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>
