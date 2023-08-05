package com.example.corpoints.layer_server;


import com.example.corpoints.layer_server.utils.Validator;
import com.example.restful.api.AccountsAPI;
import com.example.restful.api.GroupsAPI;
import com.example.restful.api.RequestsAPI;
import com.example.restful.models.Account;
import com.example.restful.models.Group;
import com.example.restful.models.RequestInGroup;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Класс, отвечающий за доступ к RESTful сервису.
 * Это некоторая прослойка между Android и RESTful сервисом
 *
 * MainAPI заботится о том, чтобы создать модель, проверить модель на валидность,
 * отправить, а также принять ее.
 */
public class MainAPI {
    private static AccountsAPI accountsAPI;
    private static GroupsAPI groupsAPI;
    private static RequestsAPI requestsAPI;
    private static Validator validator;

    static {
        validator = new Validator();
        accountsAPI = new AccountsAPI();
        groupsAPI = new GroupsAPI();
        requestsAPI = new RequestsAPI();
    }


    public static List<Account> getAccounts() { return accountsAPI.getAccounts(); }

    public static List<Group> getGroups() { return groupsAPI.getGroups(); }

    public static List<RequestInGroup> getRequests() { return requestsAPI.getRequests(); }

    /**
     * Аутентификация с последующей обработкой ошибок.
     * Исключения должны быть обработаны и показаны в поле ошибок в Android окне
     */
    public static boolean authentication(Account account) {
        if (!validator.CheckAccount(account))
            return false;

        return accountsAPI.authentication(account);
    }
    public static boolean register(Account account) {
        if (!validator.CheckAccount(account))
            return false;

        return accountsAPI.register(account);
    }

    public static boolean SendScoreTo(Account account, int score) {
        return accountsAPI.SendScoreTo(account, score);
    }
    public static boolean leave() {
        return groupsAPI.leave();
    }
    public static boolean SendScoreToGroup(int score) {
        return groupsAPI.SendScore(score);
    }
    public static boolean CreateGroupAndAppend(String name) {
        Group group = CreateGroup(name);

        if (!validator.CheckGroup(group))
            return false;

        return groupsAPI.create(group);
    }


    public static boolean SendRequestToGroup(RequestInGroup requestInGroup)  {
        if (!validator.CheckRequestForSimilar(requestInGroup))
            return false;

        //Для избежания нагрузки на сервер будем обновлять кэш сразу же.
        if (requestsAPI.SendRequestToGroup(requestInGroup)) {
            DataCash.addRequest(requestInGroup);
            return true;
        } else
            return false;
    }

    public static boolean SendResultRequest(RequestInGroup request, boolean result) {
        if (!validator.CheckRequest(request))
            return false;

        return requestsAPI.SendResultRequest(request, result);
    }




    public static Account CreateAccount(String username, String password) {
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);

        return account;
    }

    public static Group CreateGroup(String name) {
        Group group = new Group();
        group.setName(name);
        return group;
    }

    public static RequestInGroup CreateRequestInGroup(Account account, Group group) {
        RequestInGroup request = new RequestInGroup();
        request.setOwner(account);
        request.setGroup(group);
        return request;
    }
}
