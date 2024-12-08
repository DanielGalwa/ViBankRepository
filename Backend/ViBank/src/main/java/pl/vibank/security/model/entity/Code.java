package pl.vibank.security.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.vibank.model.entity.User;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "code")
public class Code {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String codeValue;

    @OneToOne
    @JoinColumn(name = "user_id",  nullable = true)
    private User user;

    @Column(name = "expiration_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationDate;

    public Code(String codeValue, User user, Date expirationDate) {
        this.codeValue = codeValue;
        this.user = user;
        this.expirationDate = expirationDate;
    }
}
