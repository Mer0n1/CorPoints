package com.example.corpoints.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.corpoints.MainActivity;
import com.example.corpoints.R;
import com.example.corpoints.layer_server.DataCash;
import com.example.corpoints.utils.GroupValidator;
import com.example.restful.models.Account;
import com.example.restful.models.Group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ListGroupsFragment extends Fragment {
    private FrameLayout main_layout;
    private Account myAccount;
    private SimpleAdapter AdapterGroups;
    private GroupValidator validator;

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DataCash.UpdateData();
        main_layout = (FrameLayout)inflater.inflate(R.layout.fragment_listgroups, container, false);
        myAccount = DataCash.getMyAccount();
        validator = new GroupValidator();

        UpdateListAdapter(DataCash.getGroups());


        View.OnClickListener ClickGoToMyGroup = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String check = validator.CheckGroup(myAccount);
                if (!check.isEmpty())
                    Toast.makeText(getActivity(), check, Toast.LENGTH_SHORT).show();

                if (!validator.hasErrors())
                    OpenGroup(myAccount.getGroup().getName());
            }
        };
        View.OnClickListener ClickCreateGroup = new View.OnClickListener() { //menu for create group
            @Override
            public void onClick(View v) {

                /*ArrayAdapter<String> adapterGroups = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
                for (int j = 0; j < AdapterGroups.getCount(); j++) {
                    HashMap<String, Object> itemHashMap = (HashMap<String, Object>) AdapterGroups.getItem(j);
                    adapterGroups.add(itemHashMap.get("First").toString());
                }*/
                ((MainActivity)getActivity()).OpenRedCrGroup();
            }
        };
        AdapterView.OnItemClickListener ListClickGroups = new AdapterView.OnItemClickListener() { //list score click
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, Object> itemHashMap = (HashMap<String, Object>) parent.getItemAtPosition(position);
                OpenGroup(itemHashMap.get("First").toString());
            }
        };

        ListView v1 = main_layout.findViewById(R.id.ListGroups);
        v1.setAdapter(AdapterGroups);

        main_layout.findViewById(R.id.MyGroupButton).setOnClickListener(ClickGoToMyGroup);
        main_layout.findViewById(R.id.RedactorGroupButton).setOnClickListener(ClickCreateGroup);
        ((ListView)main_layout.findViewById(R.id.ListGroups)).setOnItemClickListener(ListClickGroups);

        return main_layout;
    }


    private void OpenGroup(String nameGroup) {
        Intent intent = new Intent(getActivity(), GroupActivity.class);
        intent.putExtra("nameGroup", nameGroup);
        startActivity(intent);
    }

    private void UpdateListAdapter(List<Group> groups) {

        HashMap<String, String> hashMap = new HashMap<>();

        for (int j = 0; j < groups.size(); j++)
            hashMap.put(groups.get(j).getName(), groups.get(j).getGroupScore() + " balls");

        List<HashMap<String, String>> listItems = new ArrayList<>();
        AdapterGroups = new SimpleAdapter(getActivity(), listItems, R.layout.list_item,
                new String[]{"First", "Second"},
                new int[]{R.id.text1, R.id.text2});

        Iterator it = hashMap.entrySet().iterator();
        while (it.hasNext())
        {
            HashMap<String, String> resultsMap = new HashMap<>();
            Map.Entry pair = (Map.Entry)it.next();
            resultsMap.put("First", pair.getKey().toString());
            resultsMap.put("Second", pair.getValue().toString());
            listItems.add(resultsMap);
        }

        ListView resultsListView = (ListView) main_layout.findViewById(R.id.ListGroups);
        resultsListView.setAdapter(AdapterGroups);
    }
}
