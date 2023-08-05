package corpoints.server.Server.models;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Component;

import java.util.List;

@Entity
@Table(name = "Account")
public class Account {
    @Id
    @Column(name = "id")
    private int id;

    @NotEmpty(message = "Поле имени не должно быть пустым")
    @Size(min = 3, max = 30, message = "Ошибка в размере")
    @Column(name = "username")
    private String username;

    @NotEmpty(message = "Поле пароля не должно быть пустым")
    @Column(name = "password")
    private String password;

    @Column(name = "score")
    private int score;

    @ManyToOne
    @JoinColumn(name = "id_group", referencedColumnName = "id")
    @JsonIgnoreProperties("users")//чтобы не возникала рекурсия
    private Group group;

    /*@JsonIgnore
    @OneToOne(mappedBy = "admin")
    private Group group2;*/

    @OneToMany(mappedBy = "owner")
    @JsonIgnore
    private List<RequestInGroup> requestInGroup;


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getScore() {
        return score;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public void addScore(int score) { this.score += score; }

    public int getId() {
        return id;
    }

    public List<RequestInGroup> getRequestInGroup() {
        return requestInGroup;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRequestInGroup(List<RequestInGroup> requestInGroup) {
        this.requestInGroup = requestInGroup;
    }

    /*public Group getGroup2() {
        return group2;
    }

    public void setGroup2(Group group2) {
        this.group2 = group2;
    }*/
}
