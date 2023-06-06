package com.example.corpoints.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.corpoints.MainActivity;
import com.example.corpoints.R;
import com.example.corpoints.cserver.MyAccount;
import com.example.corpoints.cserver.Server;

public class ListGroupsFragment extends Fragment {
    private FrameLayout main_layout;
    private MyAccount myAccount;

    private ArrayAdapter<String> AdapterGroups;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main_layout = (FrameLayout)inflater.inflate(R.layout.fragment_listgroups, container, false);
        myAccount = Server.myAccount;

        AdapterGroups = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);


        View.OnClickListener ClickGoToMyGroup = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myAccount.getNameGroup().equals("null")) {
                    Toast.makeText(getActivity(), "Вы не состоите в группе", Toast.LENGTH_SHORT).show();
                    return;
                }
                OpenGroup(myAccount.getNameGroup());
            }
        };
        View.OnClickListener ClickCreateGroup = new View.OnClickListener() { //создание группы
            @Override
            public void onClick(View v) {

                ((MainActivity)getActivity()).OpenRedCrGroup(AdapterGroups);
                /*String name = "FirstGroup";

                if (myAccount.getNameGroup() != null) {
                    Toast.makeText(getActivity(), "Вы уже состоите в группе",Toast.LENGTH_SHORT).show();
                    return;
                }
                for (int j = 0; j < AdapterGroups.getCount(); j++)
                    if (!name.equals(AdapterGroups.getItem(j))) {
                        Toast.makeText(getActivity(), "Группа с таким названием уже существует",Toast.LENGTH_SHORT).show();
                        return;
                    }
                Server.ProtocolCreateGroup(name);*/
            }
        };
        AdapterView.OnItemClickListener ListClickGroups = new AdapterView.OnItemClickListener() { //list score click
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OpenGroup(parent.getItemAtPosition(position).toString());
            }
        };



        ListView v1 = main_layout.findViewById(R.id.ListGroups);
        v1.setAdapter(AdapterGroups);

        main_layout.findViewById(R.id.MyGroupButton).setOnClickListener(ClickGoToMyGroup);
        main_layout.findViewById(R.id.RedactorGroupButton).setOnClickListener(ClickCreateGroup);
        ((ListView)main_layout.findViewById(R.id.ListGroups)).setOnItemClickListener(ListClickGroups);

        return main_layout;
    }

    public void setListGroups(String[] array) {
        AdapterGroups.clear();
        for (int j = 0; j < array.length; j++)
            AdapterGroups.add(array[j]);
    }


    private void OpenGroup(String nameGroup) {
        String type = "Observer"; //who i am?

        if (nameGroup.equals(myAccount.getNameGroup())) {
            if (myAccount.isAdmin())
                type = "Administrator";
            else
                type = "Member";
        }

        Intent intent = new Intent(getActivity(), GroupActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("nameGroup", nameGroup);
        startActivity(intent);

    }
}
