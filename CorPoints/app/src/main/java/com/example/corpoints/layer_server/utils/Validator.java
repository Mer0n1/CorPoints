package com.example.corpoints.layer_server.utils;

import com.example.corpoints.layer_server.DataCash;
import com.example.restful.models.Account;
import com.example.restful.models.Group;
import com.example.restful.models.RequestInGroup;

import java.util.List;

/**
 * Класс, проверяющий валидность моделей
 */
public class Validator {

    /**
     * Проверить запрос на схожесть с существующими.
     * Заявка не должна повторяться.
     */
    public boolean CheckRequestForSimilar(RequestInGroup requestInGroup) {

        if (!CheckRequest(requestInGroup))
            return true;

        //Check if there is already such a request in the list
        List<RequestInGroup> list = DataCash.getRequests();
        for (RequestInGroup request : list)
            if (request.getGroup().getName().equals(requestInGroup.getGroup().getName()) &&
                request.getOwner().getUsername().equals(requestInGroup.getOwner().getUsername()))
                return false;

        return true;
    }

    /** Проверка заявки на валидность */
    public boolean CheckRequest(RequestInGroup requestInGroup) {
        if (requestInGroup.getOwner() == null ||
                requestInGroup.getGroup() == null)
            return false;
        return true;
    }

    public boolean CheckAccount(Account account) {
        if (account.getUsername().isEmpty() ||
            account.getPassword().isEmpty())
            return false;
        return true;
    }

    public boolean CheckGroup(Group group) {
        if (group == null)
            return false;
        if (group.getName().isEmpty())
            return false;
        return true;
    }

}
