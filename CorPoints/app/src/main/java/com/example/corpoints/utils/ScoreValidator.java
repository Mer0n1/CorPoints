package com.example.corpoints.utils;

import android.widget.Toast;

import com.example.corpoints.layer_server.MainAPI;
import com.example.restful.models.Account;

public class ScoreValidator {
    private boolean Error;

    public String CheckCorrectRequest(String scoreStr, Account myAccount, String Getter) {
        Error = true;

        if (scoreStr.matches("[0-9]+") && !scoreStr.isEmpty()) {
            int score = Integer.valueOf(scoreStr);

            if (score <= myAccount.getScore()) {
                Error = false;
                return "Успешно";
            } else
                return "Недостаточно баллов";
        }
        if (Getter.isEmpty())
            return "Выберите получателя";

        return  "Ввод должен содержать только число";
    }

    public boolean hasErrors() {
        return Error;
    }
}
