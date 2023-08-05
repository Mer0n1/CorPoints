package corpoints.server.Server.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import corpoints.server.Server.models.Group;
import corpoints.server.Server.models.RequestInGroup;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class AccountDTO {

    @NotEmpty(message = "Поле имени не должно быть пустым")
    @Size(min = 3, max = 30, message = "Ошибка в размере")
    @Column(name = "username")
    private String username;

    @Column(name = "score")
    private int score;

    @ManyToOne
    @JoinColumn(name = "id_group", referencedColumnName = "id")
    @JsonIgnoreProperties("users")//чтобы не возникала рекурсия
    private Group group;

    @OneToMany(mappedBy = "owner")
    @JsonIgnore
    private List<RequestInGroup> requestInGroup;


    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public List<RequestInGroup> getRequestInGroup() {
        return requestInGroup;
    }

    public void setRequestInGroup(List<RequestInGroup> requestInGroup) {
        this.requestInGroup = requestInGroup;
    }

}
