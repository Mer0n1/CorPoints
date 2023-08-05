package com.example.corpoints.ui;

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

import com.example.corpoints.R;
import com.example.corpoints.layer_server.DataCash;
import com.example.corpoints.layer_server.MainAPI;
import com.example.corpoints.utils.ScoreValidator;
import com.example.restful.api.AccountsAPI;
import com.example.restful.models.Account;

import java.util.List;


public class SenderMenuFragment extends Fragment {

    private FrameLayout main_layout;
    private Account myAccount;
    private ScoreValidator validator;

    private ArrayAdapter<String> AdapterAccounts;
    private ArrayAdapter<String> AdapterChooseScore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main_layout = (FrameLayout)inflater.inflate(R.layout.fragment_sender, container, false);

        DataCash.UpdateData();
        InitResources();
        setListUsers(DataCash.getAccounts());


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
                String UserGetter = ((TextView) (main_layout.findViewById(R.id.UserChoose))).getText().toString();
                String score = GetSend.getText().toString();

                Toast.makeText(getActivity(), validator.CheckCorrectRequest(score,
                        myAccount, UserGetter), Toast.LENGTH_SHORT).show();

                if (!validator.hasErrors()) {
                    int scoreInt = Integer.valueOf(score);
                    MainAPI.SendScoreTo(DataCash.getAccount(UserGetter), scoreInt);
                }
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

    private void setListUsers(List<Account> list) {
        AdapterAccounts.clear();
        for (int j = 0; j < list.size(); j++)
            AdapterAccounts.add(list.get(j).getUsername());
    }

    private void InitResources() {
        myAccount = DataCash.getMyAccount();
        validator = new ScoreValidator();

        AdapterChooseScore = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        AdapterAccounts    = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        AdapterChooseScore.add("10");
        AdapterChooseScore.add("50");
        AdapterChooseScore.add("100");
        AdapterChooseScore.add("200");
        AdapterChooseScore.add("500");
    }
}
