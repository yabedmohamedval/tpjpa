package domain;


import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "choice")
public class Choice {

    @Id @GeneratedValue
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false) private String text;

    @Column(nullable = false)
    private boolean correctAnswer = false;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private MCQQuestion question;

}
