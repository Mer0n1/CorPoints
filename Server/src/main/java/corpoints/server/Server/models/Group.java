package corpoints.server.Server.models;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "thegroup")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotEmpty(message = "Название не должно быть пустым")
    @Column(name = "name")
    private String name;

    @Min(value = 0, message = "Значение должно быть не меньше 0")
    @Column(name = "group_score")
    private int GroupScore;

    @OneToOne
    @JoinColumn(name = "id_admin", referencedColumnName = "id")
    @JsonIgnoreProperties("group")
    private Account admin;

    @OneToMany(mappedBy = "group")
    @JsonIgnoreProperties("group")
    private List<Account> users;

    @OneToMany(mappedBy = "group")
    @JsonIgnoreProperties("group")
    private List<RequestInGroup> req; //application for entry

    public Group () {
        users = new ArrayList<>();
        req   = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getGroupScore() {
        return GroupScore;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGroupScore(int groupScore) {
        GroupScore = groupScore;
    }

    public List<Account> getUsers() {
        return users;
    }

    public void setUsers(List<Account> users) {
        this.users = users;
    }

    public int getId() {
        return id;
    }

    public List<RequestInGroup> getReq() {
        return req;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setReq(List<RequestInGroup> req) {
        this.req = req;
    }

    public void addScore(int score) { this.GroupScore += score; }

    public Account getAdmin() {
        return admin;
    }

    public void setAdmin(Account admin) {
        this.admin = admin;
    }
}
