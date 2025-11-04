package domain;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;


@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="answer")
public class Answer {


    @Id @GeneratedValue
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id")
    private Attempt  attempt;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    // un seul non-null selon le type de question :
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chosen_id")
    private Choice  chosen;   // MCQ
    private String  freeText;                                    // ShortText
    private Boolean boolChoice;                                  // True/False

    private Boolean correct;

    private Instant answeredAt = Instant.now();

}
