package servlet;

import dao.QuizDao;
import domain.Quiz;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import jpa.EntityManagerHelper;
import jakarta.persistence.EntityManager;

@WebServlet(name="quizList", urlPatterns={"/quizzes"})
public class QuizListServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        EntityManager em = EntityManagerHelper.getEntityManager();
        List<Quiz> quizzes = new QuizDao(em).searchByTitle(""); // ou findAll()

        req.setAttribute("quizzes", quizzes);
        req.getRequestDispatcher("/WEB-INF/jsp/quizList.jsp").forward(req, resp);
    }
}
