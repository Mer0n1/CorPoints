package com.example.restful.api;


import com.example.restful.Json.JsonConverter;
import com.example.restful.models.Account;

import java.util.List;


public class AccountsAPI {

    private final String LOGIN_URL;
    private final String GET_ACCOUNTS_URL;
    private final String GET_ACCOUNT;
    private final String SEND_SCORE_TO_ACCOUNT;
    private final String REGISTER;

    public AccountsAPI() {
        String mainIP = "http://corppoints.ru:49432"; //http://192.168.1.104:5556
        LOGIN_URL = mainIP + "/accounts/authentication";
        GET_ACCOUNTS_URL = mainIP + "/accounts/getAll";
        GET_ACCOUNT = mainIP + "/accounts/getAccount";
        SEND_SCORE_TO_ACCOUNT = mainIP + "/accounts/SendScore";
        REGISTER = mainIP + "/accounts/register";
    }

    public boolean authentication(Account account) {
        String json = "username=" + account.getUsername() + "&password=" + account.getPassword();

        String response = APIServer.postToServer(LOGIN_URL, APIServer.TypeContent.form_url, json);

        return APIServer.itsOk(response);
    }

    public boolean SendScoreTo(Account ToAccount, int score) {

        String body = "score=" + score + "&name=" +  ToAccount.getUsername();

        String response = APIServer.patchToServer(SEND_SCORE_TO_ACCOUNT,
                APIServer.TypeContent.form_url, body);

        return APIServer.itsOk(response);
    }

    public List<Account> getAccounts() {
        String response = APIServer.getFromServer(GET_ACCOUNTS_URL);

        return JsonConverter.getObjects(response, Account.class);
    }

    public Account getAccount(String name) {
        String body = "?name=" + name;

        String response = APIServer.getFromServer(GET_ACCOUNT + body);

        return JsonConverter.getObject(response, Account.class);
    }

    public boolean register(Account account) {
        String json = JsonConverter.getJson(account);
        String response = APIServer.postToServer(REGISTER, APIServer.TypeContent.json, json);

        return APIServer.itsOk(response);
    }

}
