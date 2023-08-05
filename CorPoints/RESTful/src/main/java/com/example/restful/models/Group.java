package com.example.restful.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Group {

    private String name;
    private List<Account> users;
    private int GroupScore;
    private List<RequestInGroup> req;
    private Account admin;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGroupScore() {
        return GroupScore;
    }

    public void setGroupScore(int groupScore) {
        GroupScore = groupScore;
    }

    public List<Account> getUsers() {
        return users;
    }

    public List<RequestInGroup> getReq() {
        return req;
    }

    public void setUsers(List<Account> users) {
        this.users = users;
    }

    public void setReq(List<RequestInGroup> req) {
        this.req = req;
    }

    public Account getAdmin() {
        return admin;
    }

    public void setAdmin(Account admin) {
        this.admin = admin;
    }
}
