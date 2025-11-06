package web.api;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.Set;

@ApplicationPath("/api")
public class ApiApplication extends Application {
    @Override public Set<Class<?>> getClasses() {
        // Resteasy auto-scannera aussi via CDI-less initializer,
        // mais on peut laisser vide : nos ressources seront détectées.
        return Set.of();
    }
}
