package corpoints.server.Server.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import corpoints.server.Server.models.Account;
import corpoints.server.Server.models.RequestInGroup;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

public class GroupDTO {

    @NotEmpty(message = "Название не должно быть пустым")
    @Column(name = "name")
    private String name;

    @Min(value = 0, message = "Значение должно быть не меньше 0")
    @Column(name = "group_score")
    private int GroupScore;

    @OneToMany(mappedBy = "group")
    @JsonIgnore//чтобы не возникала рекурсия
    private List<Account> users;

    @OneToMany(mappedBy = "group")
    @JsonIgnore
    private List<RequestInGroup> req; //application for entry

    public GroupDTO () {
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

    public List<RequestInGroup> getReq() {
        return req;
    }

    public void setReq(List<RequestInGroup> req) {
        this.req = req;
    }

    public void addScore(int score) { this.GroupScore += score; }

}
