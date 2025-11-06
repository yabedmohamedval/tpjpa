package web.api.exception;
import jakarta.ws.rs.core.*; import jakarta.ws.rs.ext.*;

@Provider
public class BadRequestMapper implements ExceptionMapper<IllegalArgumentException> {
    public record ErrorPayload(String code, String message) {}
    @Override public Response toResponse(IllegalArgumentException ex) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorPayload("BAD_REQUEST", ex.getMessage()))
                .type(MediaType.APPLICATION_JSON).build();
    }
}
