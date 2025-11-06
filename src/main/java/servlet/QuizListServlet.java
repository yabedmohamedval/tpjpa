package servlet;

import dao.QuizDao;
import domain.Quiz;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import jpa.EntityManagerHelper;
import jakarta.persistence.EntityManager;
import web.view.QuizRow;

@WebServlet(name="quizList", urlPatterns={"/quizzes"})
public class QuizListServlet extends HttpServlet {
    /* @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        EntityManager em = EntityManagerHelper.getEntityManager();
        List<Quiz> quizzes = new QuizDao(em).searchByTitle(""); // ou findAll()

        req.setAttribute("quizzes", quizzes);
        req.getRequestDispatcher("/WEB-INF/jsp/quizList.jsp").forward(req, resp);
    }*/


    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        EntityManager em = EntityManagerHelper.getEntityManager();

        // 1 requête : ID, titre, auteur, COUNT(questions)
        List<QuizRow> rows = em.createQuery(
                "select new web.view.QuizRow(q.id, q.title, u.username, count(distinct qu.id)) " +
                        "from Quiz q " +
                        "join q.owner u " +
                        "left join q.questions qu " +
                        "group by q.id, q.title, u.username " +
                        "order by q.id asc",
                QuizRow.class
        ).getResultList();

        req.setAttribute("rows", rows);
        req.getRequestDispatcher("/WEB-INF/jsp/quizList.jsp").forward(req, resp);
    }
}
