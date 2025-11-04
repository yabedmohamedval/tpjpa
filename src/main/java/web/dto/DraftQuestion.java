package web.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DraftQuestion {
    public DraftQType type;
    public String label;
    public Integer orderIndex;
    public Boolean multiSelect;        // MCQ
    public Boolean correct;            // TF
    public String expectedRegex;       // SHORT
    public List<DraftChoice> choices = new ArrayList<>(); // MCQ
}
