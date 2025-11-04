package domain;


import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="q_type", discriminatorType = DiscriminatorType.STRING)
@Table(name="question", uniqueConstraints = {
        @UniqueConstraint(name="uk_question_quiz_order", columnNames = {"quiz_id", "orderIndex"})
})
public abstract class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    protected String  label;
    @Column(nullable = false)
    protected Integer orderIndex;
    private int timeLimitSeconds = 20;
    @Column(nullable = false)
    protected Integer points = 100;

    @ManyToOne(optional=false,  fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;


}
