package com.example.corpoints.utils;

import android.widget.CheckBox;
import android.widget.Toast;

import com.example.corpoints.R;
import com.example.corpoints.StartIdentActivity;

public class AuthValidator {
    private boolean Error;

    public String validate(String login, String password) {
        Error = true;

        if (!login.matches("[a-zA-Z0-9]+") ||
            !password.matches("[a-zA-Z0-9]+"))
            return "Ошибка. Только буквы и цифры";
        else {
            if (!(login.length() >= 3 && login.length() <= 14))
                return "Логин должен быть от 3 до 14 символов";
            if (!(password.length() >= 6 && password.length() <= 8))
                return "Пароль должен быть от 6 до 8 символов";
        }

        Error = false;
        return "";
    }

    public boolean hasErrors() { return Error; }
}
