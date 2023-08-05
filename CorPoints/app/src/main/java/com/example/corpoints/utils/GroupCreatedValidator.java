package com.example.corpoints.utils;

import android.widget.Toast;

import com.example.corpoints.layer_server.DataCash;
import com.example.restful.models.Account;
import com.example.restful.models.Group;

import java.util.List;

public class GroupCreatedValidator {
    private boolean Error;

    public String CheckCreate(Account account, String name) {
        List<Group> groupList = DataCash.getGroups();
        Error = true;

        if (account.getGroup() != null)
            return "Вы уже состоите в группе";

        for (int j = 0; j < groupList.size(); j++)
            if (name.equals(groupList.get(j).getName()))
                return "Группа с таким названием уже существует";

        if (!(name.matches("[a-zA-Z0-9]+") &&
              name.length() > 3 && name.length() < 14))
            return "Только буквы и цифры\nДолжен состоять от 3 до 14 символов";

        Error = false;
        return "Успешно";
    }

    public boolean hasErrors() {
        return Error;
    }
}
