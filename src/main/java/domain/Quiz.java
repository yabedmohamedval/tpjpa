package domain;


import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;


@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@NamedQuery(
        name = "Quiz.searchByTitle",
        query = "select q from Quiz q where lower(q.title) like lower(concat('%', :kw, '%'))"
)
@Entity
@Table(name = "quiz")
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
    @Column(length=1000)
    private String description;
    @Column(nullable=false)
    private Integer timePerQuestionSec = 20;

    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    private AppUser owner;

    @OneToMany(mappedBy = "quiz",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true)
    @OrderBy("orderIndex asc")
    private List<Question> questions = new ArrayList<>();

    public void addQuestion(Question q) {
        q.setQuiz(this);            // côté propriétaire (ManyToOne)
        this.questions.add(q);      // côté inverse (OneToMany)
    }

    public void removeQuestion(Question q) {
        this.questions.remove(q);   // enlève du côté inverse
        q.setQuiz(null);            // coupe la FK (orphanRemoval => delete)
    }
}
