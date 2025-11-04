package web.dto;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class DraftQuiz {
    public String title;
    public String description;
    public Integer timePerQuestionSec = 20;
    public List<DraftQuestion> questions = new ArrayList<>();

}