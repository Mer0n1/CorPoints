package com.example.corpoints.ui;

import android.accounts.Account;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.corpoints.MainActivity;
import com.example.corpoints.R;
import com.example.corpoints.cserver.MyAccount;
import com.example.corpoints.cserver.Server;

public class SenderMenuFragment extends Fragment {

    private FrameLayout main_layout;
    private MyAccount myAccount;

    private ArrayAdapter<String> AdapterAccounts;
    private ArrayAdapter<String> AdapterChooseScore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        main_layout = (FrameLayout)inflater.inflate(R.layout.fragment_sender, container, false);
        myAccount = Server.myAccount;

        AdapterChooseScore = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        AdapterAccounts    = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        AdapterChooseScore.add("10");
        AdapterChooseScore.add("50");
        AdapterChooseScore.add("100");
        AdapterChooseScore.add("200");
        AdapterChooseScore.add("500");


        AdapterView.OnItemClickListener ListClick = new AdapterView.OnItemClickListener() { //list users click
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = main_layout.findViewById(R.id.UserChoose);
                textView.setText(parent.getItemAtPosition(position).toString());
            }
        };
        AdapterView.OnItemClickListener ListClickPoints = new AdapterView.OnItemClickListener() { //list score click
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EditText text = main_layout.findViewById(R.id.EditGetPointsSend);
                text.setText(parent.getItemAtPosition(position).toString());
            }
        };

        View.OnClickListener onClickSendPoints = new View.OnClickListener() { //click Button send
            @Override
            public void onClick(View v) {
                EditText GetSend = main_layout.findViewById(R.id.EditGetPointsSend);
                String col = GetSend.getText().toString();
                if (col.matches("[0-9]+")) {
                    int qu = Integer.valueOf(col);

                    if (qu > myAccount.getScore()) {
                        Toast.makeText(getActivity(), "Недостаточно баллов", Toast.LENGTH_SHORT).show();
                        return;
                    } else
                        Toast.makeText(getActivity(), "Отправлено", Toast.LENGTH_LONG).show();

                    String UserGetter = ((TextView) (main_layout.findViewById(R.id.UserChoose))).getText().toString();
                    Server.ProtocolSendScore(UserGetter, qu);
                } else
                    Toast.makeText(getActivity(), "Запрос должен содержать только цифры", Toast.LENGTH_SHORT).show();
            }
        };

        ListView v1 = main_layout.findViewById(R.id.ListAccounts);
        v1.setAdapter(AdapterAccounts);

        ListView v2 = main_layout.findViewById(R.id.ListChooseScore);
        v2.setAdapter(AdapterChooseScore);

        //setOnClick handler
        main_layout.findViewById(R.id.send).setOnClickListener(onClickSendPoints);
        ((ListView)main_layout.findViewById(R.id.ListAccounts)).setOnItemClickListener(ListClick);
        ((ListView)main_layout.findViewById(R.id.ListChooseScore)).setOnItemClickListener(ListClickPoints);

        return main_layout;
    }

    public ArrayAdapter getArrayAdapter() { return AdapterAccounts; }

    public void setListUsers(String[] array) {
        AdapterAccounts.clear();
        for (int j = 0; j < array.length; j++)
            AdapterAccounts.add(array[j]);
    }
}
