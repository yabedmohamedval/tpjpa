package web.api;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.Set;
import java.util.HashSet;

// Swagger Core JAX-RS (expose /openapi)
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource;
import io.swagger.v3.jaxrs2.integration.resources.BaseOpenApiResource;
import io.swagger.v3.jaxrs2.SwaggerSerializers;

@ApplicationPath("/api") // tes endpoints REST: /api/...
public class RestApp extends Application {


    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> s = new HashSet<>();

        // === Tes endpoints REST (ajoute toutes tes resources) ===
        s.add(QuizResource.class);

        // === Endpoints OpenAPI (fourni par Swagger Core) ===
        s.add(OpenApiResource.class);            // /api/openapi  +  /api/openapi.yaml
        s.add(AcceptHeaderOpenApiResource.class);
        s.add(SwaggerSerializers.class);

        return s;
    }
}
