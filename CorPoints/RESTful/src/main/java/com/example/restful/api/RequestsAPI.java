package com.example.restful.api;


import com.example.restful.Json.JsonConverter;
import com.example.restful.models.RequestInGroup;

import java.util.List;


public class RequestsAPI {

    private final String GET_REQUESTS;
    private final String SEND_REQUEST;
    private final String RESULT_REQUEST;

    public RequestsAPI() {
        String mainIP = "http://corppoints.ru:49432";
        GET_REQUESTS = mainIP + "/requests/getRequests";
        SEND_REQUEST = mainIP + "/requests/join";
        RESULT_REQUEST = mainIP + "/requests/ResultRequest";
    }

    public List<RequestInGroup> getRequests() {
        String response = APIServer.getFromServer(GET_REQUESTS);

        return JsonConverter.getObjects(response, RequestInGroup.class);
    }

    public boolean SendRequestToGroup(RequestInGroup requestInGroup)  {
        return SendRequest(requestInGroup, SEND_REQUEST);
    }

    public boolean SendResultRequest(RequestInGroup requestInGroup, boolean result)  {
        requestInGroup.setAnswer(result);
        return SendRequest(requestInGroup, RESULT_REQUEST);
    }

    private boolean SendRequest(RequestInGroup requestInGroup, String URL)  {

        String json = JsonConverter.getJson(requestInGroup);
        String response = APIServer.postToServer(URL, APIServer.TypeContent.json, json);

        return APIServer.itsOk(response);
    }
}
