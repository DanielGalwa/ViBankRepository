package pl.vibank.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.vibank.security.model.enums.Role;
import pl.vibank.security.model.entity.Code;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")//ponieważ istnieje już taka tablica w MYSQL
//implements Serializable podpowiedział CHAT-GPT bo REDIS
//https://docs.spring.io/spring-session/reference/guides/boot-redis.html#boot-sample
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String pid;

    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private int tries;

    @Enumerated(EnumType.STRING)
    private Role role;


    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, optional = true)
    private Code code;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Account> accounts;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", pid='" + pid + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email +
                '}';
    }
}
