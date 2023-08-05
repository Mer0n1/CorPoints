package com.example.corpoints.utils;

import android.widget.Toast;

import com.example.corpoints.layer_server.MainAPI;
import com.example.corpoints.ui.GroupActivity;
import com.example.restful.models.Account;
import com.example.restful.models.RequestInGroup;

public class GroupValidator {
    private boolean Error;

    public String CheckGroup(Account myAccount) {
        Error = true;
        if (myAccount.getGroup() == null)
            return "Вы не состоите в группе";

        Error = false;
        return "";
    }
    public String CheckGroupForRequest(Account myAccount) {
        Error = true;
        if (myAccount.getGroup() != null)
            return "Вы уже состоите в группе";

        Error = false;
        return "Заявка отправлена";
    }

    public boolean hasErrors() {
        return Error;
    }

}
