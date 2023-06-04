package com.example.corpoints.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.corpoints.R;
import com.example.corpoints.cserver.MyAccount;
import com.example.corpoints.cserver.Server;

public class GroupActivity extends AppCompatActivity {
    private MyAccount myAccount;
    private ArrayAdapter<String> AdapterUsersGroup;

    private String typeGroup;
    private String nameGroup;

    private FrameLayout main_layout;

    private FragmentTransaction fragmentTransaction;
    private BudgetGroupFragment budgetGroupFragment;
    private RequestsFragment requestsFragment;

    private View.OnClickListener SendRequestListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        main_layout = findViewById(R.id.group_window);

        AdapterUsersGroup = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        myAccount = Server.myAccount;
        myAccount.AdapterUsersGroup = AdapterUsersGroup;
        Server.reader.setGroupActivity(this);

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        budgetGroupFragment = new BudgetGroupFragment();
        requestsFragment    = new RequestsFragment();

        Intent intent = getIntent();
        typeGroup = intent.getStringExtra("type");
        nameGroup = intent.getStringExtra("nameGroup");

        View.OnClickListener LeaveGroupListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Server.ProtocolLeaveGroup();

                //Server.UpdateInfoProtocolGroups(); //?*
                finish();
            }
        };
        View.OnClickListener BudgetGroupListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main_layout.removeAllViews();

                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(main_layout.getId(), budgetGroupFragment);
                fragmentTransaction.commit();
            }
        };

        View.OnClickListener MenuRequestsListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main_layout.removeAllViews();

                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(main_layout.getId(), requestsFragment);
                fragmentTransaction.commit();

                Server.ProtocolInfoRequests();
            }
        };
        SendRequestListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myAccount.getNameGroup() != null) {
                    Server.ProtocolJoinToGroup(nameGroup);
                    Toast.makeText(GroupActivity.this, "Заявка отправлена", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(GroupActivity.this, "Вы уже состоите в группе", Toast.LENGTH_SHORT).show();
            }
        };

        for (int j = 0; j < main_layout.getChildCount(); j++)
            main_layout.getChildAt(j).setVisibility(View.VISIBLE); //set visible all views


        if (typeGroup.equals("Administrator")) { //if im owner this group
            Button button = findViewById(R.id.SigninGroup);
            button.setText("Заявки");

            button.setOnClickListener(MenuRequestsListener);
            findViewById(R.id.LeaveGroup).setOnClickListener(LeaveGroupListener);
            findViewById(R.id.BudgetGroup).setOnClickListener(BudgetGroupListener);
        } else if (typeGroup.equals("Member")) { //if its my group
            Button button = findViewById(R.id.SigninGroup);
            button.setVisibility(View.INVISIBLE);

            findViewById(R.id.LeaveGroup).setOnClickListener(LeaveGroupListener);
            findViewById(R.id.BudgetGroup).setOnClickListener(BudgetGroupListener);
        } else if (typeGroup.equals("Observer")) {

            Button button = findViewById(R.id.SigninGroup);
            button.setText("Отправить заявку на вступление");
            button.setOnClickListener(SendRequestListener);

            Button button1 = findViewById(R.id.LeaveGroup);
            Button button2 = findViewById(R.id.BudgetGroup);
            button1.setVisibility(View.INVISIBLE);
            button2.setVisibility(View.INVISIBLE);
        }
        ((ListView)findViewById(R.id.ListUsersGroup)).setAdapter(AdapterUsersGroup);
        Server.UpdateInfoProtocolUsersGroup(nameGroup);
    }



    public void setRequests(String[] array) {
        requestsFragment.setRequests(array);
    }

    public void ExitPFragment() {
        finish();
        //setContentView(R.layout.activity_group);
        //((ListView)findViewById(R.id.ListUsersGroup)).setAdapter(AdapterUsersGroup);
    }
}
