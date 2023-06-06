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
import com.example.corpoints.cserver.MyAccount;
import com.example.corpoints.cserver.Server;

public class GroupRedactorCreatedFragment extends Fragment {
    private FrameLayout main_layout;
    private MyAccount myAccount;
    private ArrayAdapter<String> AdapterGroups;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main_layout = (FrameLayout) inflater.inflate(R.layout.fragment_redactor_group, container, false);
        myAccount = Server.myAccount;

        View.OnClickListener ClickCreateGroup = new View.OnClickListener() { //создание группы
            @Override
            public void onClick(View v) {

                EditText text = main_layout.findViewById(R.id.editTextTextPersonName);
                String name = text.getText().toString();


                if (!myAccount.getNameGroup().equals("null")) {
                    Toast.makeText(getActivity(), "Вы уже состоите в группе",Toast.LENGTH_SHORT).show();
                    return;
                }
                for (int j = 0; j < AdapterGroups.getCount(); j++)
                    if (name.equals(AdapterGroups.getItem(j))) {
                        Toast.makeText(getActivity(), "Группа с таким названием уже существует", Toast.LENGTH_SHORT).show();
                        return;
                    }
                if (!(name.matches("[a-zA-Z0-9]+") && name.length() > 3 && name.length() < 14)) {
                    Toast.makeText(getActivity(), "Только буквы и цифры\nДолжен состоять от 3 до 14 символов", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(getActivity(), "Успешно",Toast.LENGTH_SHORT).show();

                Server.ProtocolCreateGroup(name);
            }
        };
        main_layout.findViewById(R.id.CreateGroup).setOnClickListener(ClickCreateGroup);

        return main_layout;
    }

    public void setAdapterGroups(ArrayAdapter adapterGroups) {
        AdapterGroups = adapterGroups;
    }
}
