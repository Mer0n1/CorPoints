package com.example.restful.api;

import com.example.restful.Json.JsonConverter;
import com.example.restful.models.Group;

import java.util.List;

public class GroupsAPI {

    private final String GET_GROUPS;
    private final String CREATE_GROUP_URL;
    private final String LEAVE_FROM_GROUP;
    private final String SEND_SCORE_TO_GROUP;

    public GroupsAPI() {
        String mainIP = "http://corppoints.ru:49432";
        GET_GROUPS = mainIP + "/groups/getGroups";
        CREATE_GROUP_URL = mainIP + "/groups/create";
        LEAVE_FROM_GROUP = mainIP + "/groups/leave";
        SEND_SCORE_TO_GROUP = mainIP + "/groups/SendScoreToGroup";
    }


    public List<Group> getGroups() {
        String response = APIServer.getFromServer(GET_GROUPS);

        return JsonConverter.getObjects(response, Group.class);
    }

    public boolean create(Group group) {

        String json = JsonConverter.getJson(group);

        String response = APIServer.postToServer(CREATE_GROUP_URL,
                APIServer.TypeContent.json, json);

        return APIServer.itsOk(response);
    }

    public boolean leave() {
        String response = APIServer.patchToServer(LEAVE_FROM_GROUP,
                APIServer.TypeContent.form_url, "");

        System.out.println(response);
        return APIServer.itsOk(response);
    }

    public boolean SendScore(int score) {

        String body = "score=" + score;
        String response = APIServer.patchToServer(SEND_SCORE_TO_GROUP,
                APIServer.TypeContent.form_url, body);

        System.out.println(response);
        return APIServer.itsOk(response);
    }

}
