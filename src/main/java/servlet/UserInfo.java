package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name="userinfo", urlPatterns={"/UserInfo"})
public class UserInfo extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("""
            <html><body>
            <h1>Récapitulatif</h1>
            <ul>
              <li>Nom: """ + request.getParameter("name") + """
              <li>Prénom: """ + request.getParameter("firstname") + """
              <li>Age: """ + request.getParameter("age") + """
            </ul>
            </body></html>
          """);
        }
    }
}
