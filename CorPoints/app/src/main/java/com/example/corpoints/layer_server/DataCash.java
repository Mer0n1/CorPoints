package com.example.corpoints.layer_server;

import com.example.restful.models.Account;
import com.example.restful.models.Group;
import com.example.restful.models.RequestInGroup;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс, отвечающий за хранение данных и их актуальность.
 * Делает запросы через RESTful сервис к серверу и получает актуальные данные.
 */
public class DataCash {
    private static List<Account> accounts;
    private static List<Group> groups;
    private static List<RequestInGroup> requests;
    private static Account myAccount;

    private DataCash() {}
    static { myAccount = new Account();}

    public static List<Account> getAccounts() {
        return accounts;
    }

    public static Account getAccount(String name) {
        return accounts.stream().filter(x->x.getUsername().equals(name)).findAny().orElse(null);
    }

    public static List<RequestInGroup> getRequests() {
        return requests;
    }

    public static List<RequestInGroup> getRequestsFromGroup(Group group) {
        List<RequestInGroup> reqGroup = new ArrayList<>();
        for (RequestInGroup request : requests)
            if (request.getGroup().getName().equals(group.getName()))
                reqGroup.add(request);
        return reqGroup;
    }
    public static void addRequest(RequestInGroup request) { requests.add(request); }

    public static List<Group> getGroups() {
        return groups;
    }

    public static Account getMyAccount() {
        return myAccount;
    }

    public static void setMyAccount(Account myAccount) {
        DataCash.myAccount = myAccount;
    }
    public static void initAccount(String username) { myAccount = MainAPI.getAccount(username);}

    public static void UpdateData() {
        accounts = MainAPI.getAccounts();
        groups   = MainAPI.getGroups();
        requests = MainAPI.getRequests();
        myAccount = accounts.stream().filter(x->x.getUsername()
                .equals(myAccount.getUsername())).findAny().orElse(null);
    }

}
