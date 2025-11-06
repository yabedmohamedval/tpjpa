package web.api;

import jakarta.servlet.http.HttpServlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.webjars.WebJarAssetLocator;

import java.io.IOException;


@WebServlet(urlPatterns = {"/swagger-ui", "/swagger-ui/"}, loadOnStartup = 1)
public class SwaggerUiServlet extends HttpServlet {
    private static final WebJarAssetLocator locator = new WebJarAssetLocator();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String ctx = req.getContextPath();
        String full = locator.getFullPath("swagger-ui", "index.html");
        String webPath = "/" + full.substring("META-INF/resources/".length());

        String target = ctx + webPath + "?configUrl=" + ctx + "/swagger-config.json";
        resp.sendRedirect(target);
    }
}
