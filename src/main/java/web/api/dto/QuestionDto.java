package web.api.dto;
import java.util.ArrayList; import java.util.List;
public class QuestionDto {
    public String type;           // "MCQ" | "TF" | "SHORT"
    public String label;
    public Integer orderIndex;    // optionnel
    public Boolean correct;       // pour TF
    public String expectedRegex;  // pour SHORT
    public Boolean multiSelect;   // pour MCQ
    public List<ChoiceDto> choices = new ArrayList<>();
}
