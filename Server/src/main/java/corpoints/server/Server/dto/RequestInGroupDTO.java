package corpoints.server.Server.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import corpoints.server.Server.models.Account;
import corpoints.server.Server.models.Group;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

@Component
public class RequestInGroupDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotNull
    @JsonIgnoreProperties("req")
    @ManyToOne
    @JoinColumn(name = "id_group", referencedColumnName = "id")
    private Group group;

    @JsonIgnoreProperties("requestInGroup")
    @ManyToOne
    @JoinColumn(name = "id_owner", referencedColumnName = "id")
    private Account owner;

    @Transient
    private Boolean answer; //состояние запроса. null - нейтральное. true - принятый

    public int getId() {
        return id;
    }

    public Group getGroup() {
        return group;
    }

    public Account getOwner() {
        return owner;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public void setOwner(Account owner) {
        this.owner = owner;
    }

    public Boolean getAnswer() {
        return answer;
    }

    public void setAnswer(Boolean answer) {
        this.answer = answer;
    }
}
