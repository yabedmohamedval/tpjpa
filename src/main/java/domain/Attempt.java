package domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="attempt")
public class Attempt {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY) private Quiz quiz;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) private AppUser player;

    @Column(nullable = false)
    private Instant startedAt = Instant.now();

    private Instant finishedAt;
    private Integer finalScore;

    @OneToMany(mappedBy = "attempt",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true)
    private List<Answer> answers = new ArrayList<>();

    public void addAnswer(Answer a) {
        a.setAttempt(this);
        this.answers.add(a);
    }
    public void removeAnswer(Answer a) {
        this.answers.remove(a);
        a.setAttempt(null);
    }
}
