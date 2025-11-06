package web.api.dto;
import java.util.ArrayList; import java.util.List;
public class QuizCreateDto {
    public String title;
    public String description;
    public Integer timePerQuestionSec;
    public String ownerUsername; // ex: "teacher1" (optionnel)
    public List<QuestionDto> questions = new ArrayList<>();
}
