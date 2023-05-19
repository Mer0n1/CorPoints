package com.example.corpoints.cserver;

import android.widget.ArrayAdapter;

public class MyAccount {
    protected String name, password;
    protected int score;
    protected String myGroup;
    protected String NameAdminGroup;
    protected int ScoreGroup;

    //list users?
    public ArrayAdapter<String> adapter;
    public ArrayAdapter<String> AdapterUsersGroup;
    public ArrayAdapter<String> AdapterRequests;
    //list groups?

    MyAccount() {
        myGroup = null;
        name = null;
        score = -1;
        ScoreGroup = 0;
    }

    public int getScore() {return score;}
    public String getName() { return new String(name); }
    public String getNameGroup() { return new String(myGroup); }
    public boolean isAdmin() {
        if (name.equals(NameAdminGroup))
            return true;
        else
            return false;
    }
    public int getScoreGroup() { return ScoreGroup; }
}
