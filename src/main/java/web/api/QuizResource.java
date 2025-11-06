package web.api;

import jakarta.ws.rs.*; import jakarta.ws.rs.core.*;
import java.util.*;
import domain.Quiz;
import service.QuizService;
import web.api.dto.QuizCreateDto;

@Path("/quizzes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class QuizResource {

    private final QuizService svc = new QuizService();

    @GET
    public List<Map<String,Object>> list(@QueryParam("page") @DefaultValue("0") int page,
                                         @QueryParam("size") @DefaultValue("20") int size) {
        List<Quiz> res = svc.listPaged(page, size);
        List<Map<String,Object>> out = new ArrayList<>();
        for (Quiz q : res) {
            out.add(Map.of(
                    "id", q.getId(),
                    "title", q.getTitle(),
                    "description", q.getDescription(),
                    "timePerQuestionSec", q.getTimePerQuestionSec(),
                    "questionCount", q.getQuestions()!=null ? q.getQuestions().size() : 0
            ));
        }
        return out;
    }

    @GET @Path("/{id}")
    public Quiz getOne(@PathParam("id") long id) {
        return svc.findWithGraph(id);
    }

    @POST
    public Response create(QuizCreateDto dto, @Context UriInfo uri) {
        long id = svc.createFromDto(dto);
        return Response.created(uri.getAbsolutePathBuilder().path(Long.toString(id)).build())
                .entity(Map.of("id", id)).build();
    }

    @DELETE @Path("/{id}")
    public Response delete(@PathParam("id") long id) {
        svc.delete(id);
        return Response.noContent().build();
    }
}
