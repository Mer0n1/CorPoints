import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Group {
    /**
     * Есть 2 варианта сделать группу
     * 1 - сделать бюджет и скидывание баллов +
     * 2 - сделать среднее количество баллов участников */
    private List<InfoAccount> users;
    private List<String> req; //application for entry
    private InfoAccount admin;
    private int GroupScore;
    private String name;

    Group(InfoAccount creater, String name) {
        users = new ArrayList<>();
        req   = new ArrayList<>();
        this.name = name;

        admin = creater;
        addUser(creater);
        GroupScore = 0;
    }

    public void addUser(InfoAccount account)    { users.add(account);}
    public void addRequest(String nickname)     { req.add(nickname); }
    public void removeUser(InfoAccount account) {
        users.remove(account);
    }
    public void removeRequest(String nickname)  { req.remove(nickname); }
    public int getGroupScore()                  { return GroupScore; }
    public void addScore(int score)             { GroupScore += score; }
    public List<InfoAccount> getUsersCopy()     { return new ArrayList<>(users); }
    //public List<String> getRequests()           { return new ArrayList<>(req); }
    public String getName()                     { return new String(name);}
    public String getNameAdmin()                { return new String(admin.nickname); }
    public void DeleteGroup() {
        for (int j = 0; j < users.size(); j++) { //delete all users
            users.get(j).group = null;
            users.remove(j);
        }
        admin = null;
    }

    String getListJSONUsersGroup() {
        JSONArray array = new JSONArray();

        for (int n = 0; n < users.size(); n++) {
            JSONObject obj = new JSONObject();
            obj.put("name", users.get(n).nickname);
            array.add(obj);
        }

        return array.toString();
    }
    String getListJSONRequests() {
        JSONArray array = new JSONArray();

        for (int n = 0; n < req.size(); n++) {
            JSONObject obj = new JSONObject();
            obj.put("name", req.get(n));
            array.add(obj);
        }
        return array.toString();
    }

}
