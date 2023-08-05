package com.example.restful.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestInGroup {
    private int id;
    private Account owner;
    private Group group;
    private Boolean answer;

    public RequestInGroup(Account owner, Group group) {
        this.owner = owner;
        this.group = group;
    }

    public RequestInGroup() {

    }

    public int getId() {
        return id;
    }

    public Account getOwner() {
        return owner;
    }

    public Group getGroup() {
        return group;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOwner(Account owner) {
        this.owner = owner;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Boolean getAnswer() {
        return answer;
    }

    public void setAnswer(Boolean answer) {
        this.answer = answer;
    }
}
