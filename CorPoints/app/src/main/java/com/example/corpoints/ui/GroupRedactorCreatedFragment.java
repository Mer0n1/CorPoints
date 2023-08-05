package com.example.corpoints.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.corpoints.R;
import com.example.corpoints.layer_server.DataCash;
import com.example.corpoints.layer_server.MainAPI;
import com.example.corpoints.utils.GroupCreatedValidator;
import com.example.restful.models.Account;
import com.example.restful.models.Group;
import com.fasterxml.jackson.core.JsonProcessingException;


public class GroupRedactorCreatedFragment extends Fragment {
    private FrameLayout main_layout;
    private Account myAccount;
    private GroupCreatedValidator validator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main_layout = (FrameLayout) inflater.inflate(R.layout.fragment_redactor_group, container, false);
        myAccount = DataCash.getMyAccount();
        validator = new GroupCreatedValidator();

        View.OnClickListener ClickCreateGroup = new View.OnClickListener() { //создание группы
            @Override
            public void onClick(View v) {

                EditText text = main_layout.findViewById(R.id.editTextTextPersonName);
                String name = text.getText().toString();

                //Validate
                Toast.makeText(getActivity(), validator.CheckCreate(myAccount, name), Toast.LENGTH_SHORT).show();

                if (!validator.hasErrors())
                    MainAPI.CreateGroupAndAppend(name);
            }
        };
        main_layout.findViewById(R.id.CreateGroup).setOnClickListener(ClickCreateGroup);

        return main_layout;
    }

}
