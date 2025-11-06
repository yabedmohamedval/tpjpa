package web.api.exception;
import jakarta.ws.rs.core.*; import jakarta.ws.rs.ext.*; import jakarta.persistence.NoResultException;

@Provider
public class NotFoundMapper implements ExceptionMapper<NoResultException> {
    public record ErrorPayload(String code, String message) {}
    @Override public Response toResponse(NoResultException ex) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorPayload("NOT_FOUND", ex.getMessage()))
                .type(MediaType.APPLICATION_JSON).build();
    }
}
