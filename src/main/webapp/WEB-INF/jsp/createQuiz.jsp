<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html>
<head>
    <title>Nouveau Quiz</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css"/>
</head>
<body>
<div class="container">
    <div class="card">
        <h1>Nouveau Quiz</h1>

        <!-- Message flash d'erreur éventuel -->
        <c:if test="${not empty sessionScope.FLASH_ERR}">
            <div style="margin:10px 0; padding:10px; border-radius:8px; background:#3f1d1d; color:#fca5a5;">
                    ${sessionScope.FLASH_ERR}
            </div>
            <c:remove var="FLASH_ERR" scope="session"/>
        </c:if>

        <h2>Étape 1 — Infos Quiz</h2>
        <form method="post" action="${pageContext.request.contextPath}/quizzes/new">
            <input type="hidden" name="action" value="start"/>
            <div class="form-grid">
                <label for="title">Titre</label>
                <input id="title" name="title" required value="<c:out value='${draft.title}'/>"/>
                <span></span>

                <label for="desc">Description</label>
                <input id="desc" name="description" value="<c:out value='${draft.description}'/>"/>
                <span></span>

                <label for="tps">Temps/question (s)</label>
                <input id="tps" type="number" name="tps"
                       value="${draft.timePerQuestionSec != null ? draft.timePerQuestionSec : 20}"/>
                <button class="btn" type="submit">Enregistrer étape 1</button>
            </div>
        </form>

        <h2>Étape 2 — Ajouter des questions</h2>

        <!-- MCQ -->
        <form method="post" action="${pageContext.request.contextPath}/quizzes/new">
            <input type="hidden" name="action" value="addMcq"/>
            <!-- auto-save métas -->
            <input type="hidden" name="title" value="<c:out value='${draft.title}'/>"/>
            <input type="hidden" name="description" value="<c:out value='${draft.description}'/>"/>
            <input type="hidden" name="tps" value="${draft.timePerQuestionSec != null ? draft.timePerQuestionSec : 20}"/>

            <fieldset>
                <legend>MCQ</legend>
                <div class="form-grid">
                    <label>Label</label>
                    <input name="label" placeholder="Intitulé de la question" required/>
                    <span></span>

                    <label>Ordre</label>
                    <input name="orderIndex" type="number" placeholder="auto si vide"/>
                    <label>Multi-select <input name="multi" type="checkbox" value="true"/></label>
                </div>
                <div class="form-grid">
                    <label>Choix 1</label>
                    <input name="c1" placeholder="ex: Réponse A"/>
                    <label>Correct <input type="checkbox" name="c1ok" value="true"/></label>

                    <label>Choix 2</label>
                    <input name="c2" placeholder="ex: Réponse B"/>
                    <label>Correct <input type="checkbox" name="c2ok" value="true"/></label>
                </div>
                <button class="btn" type="submit">Ajouter MCQ</button>
            </fieldset>
        </form>

        <!-- TF -->
        <form method="post" action="${pageContext.request.contextPath}/quizzes/new">
            <input type="hidden" name="action" value="addTf"/>
            <!-- auto-save métas -->
            <input type="hidden" name="title" value="<c:out value='${draft.title}'/>"/>
            <input type="hidden" name="description" value="<c:out value='${draft.description}'/>"/>
            <input type="hidden" name="tps" value="${draft.timePerQuestionSec != null ? draft.timePerQuestionSec : 20}"/>

            <fieldset>
                <legend>True/False</legend>
                <div class="form-grid">
                    <label>Label</label>
                    <input name="label" placeholder="Intitulé" required/>
                    <label>Ordre</label>
                    <input name="orderIndex" type="number" placeholder="auto si vide"/>
                    <label>Vrai ? <input name="correct" type="checkbox" value="true"/></label>
                </div>
                <button class="btn" type="submit">Ajouter TF</button>
            </fieldset>
        </form>

        <!-- SHORT -->
        <form method="post" action="${pageContext.request.contextPath}/quizzes/new">
            <input type="hidden" name="action" value="addShort"/>
            <!-- auto-save métas -->
            <input type="hidden" name="title" value="<c:out value='${draft.title}'/>"/>
            <input type="hidden" name="description" value="<c:out value='${draft.description}'/>"/>
            <input type="hidden" name="tps" value="${draft.timePerQuestionSec != null ? draft.timePerQuestionSec : 20}"/>

            <fieldset>
                <legend>Short text</legend>
                <div class="form-grid">
                    <label>Label</label>
                    <input name="label" placeholder="Intitulé" required/>
                    <label>Ordre</label>
                    <input name="orderIndex" type="number" placeholder="auto si vide"/>
                    <label>Regex attendue</label>
                    <input name="regex" placeholder="(?i)hibernate"/>
                </div>
                <button class="btn" type="submit">Ajouter SHORT</button>
            </fieldset>
        </form>

        <div class="draft">
            <h2>Questions en brouillon</h2>
            <ol>
                <c:forEach items="${draft.questions}" var="q" varStatus="st">
                    <li>
                        <span class="badge">${q.type}</span>
                        <strong><c:out value="${q.label}"/></strong>
                        <span class="small">(ordre ${q.orderIndex})</span>
                        <c:if test="${q.type == 'MCQ'}">
                            <ul class="clean small" style="margin-top:6px">
                                <c:forEach items="${q.choices}" var="c">
                                    <li>• <c:out value="${c.text}"/> <c:if test="${c.correct}">(✔)</c:if></li>
                                </c:forEach>
                            </ul>
                        </c:if>
                        <c:if test="${q.type == 'TF'}"> — <span class="small">correct=${q.correct}</span></c:if>
                        <c:if test="${q.type == 'SHORT'}"> — <span class="small">regex=<c:out value="${q.expectedRegex}"/></span></c:if>
                    </li>
                </c:forEach>
                <c:if test="${empty draft.questions}">
                    <li class="small">Ajoute au moins une question pour créer le quiz.</li>
                </c:if>
            </ol>
        </div>

        <div class="actions">
            <!-- FINISH -->
            <form method="post" action="${pageContext.request.contextPath}/quizzes/new">
                <input type="hidden" name="action" value="finish"/>
                <!-- on reposte aussi les métas pour un auto-save -->
                <input type="hidden" name="title" value="<c:out value='${draft.title}'/>"/>
                <input type="hidden" name="description" value="<c:out value='${draft.description}'/>"/>
                <input type="hidden" name="tps" value="${draft.timePerQuestionSec != null ? draft.timePerQuestionSec : 20}"/>
                <button class="btn" type="submit">Créer le quiz en base</button>
            </form>

            <!-- RESET -->
            <form method="post" action="${pageContext.request.contextPath}/quizzes/new">
                <input type="hidden" name="action" value="reset"/>
                <button class="btn btn-secondary" type="submit">Réinitialiser le brouillon</button>
            </form>
        </div>
    </div>
</div>
</body>
</html>
