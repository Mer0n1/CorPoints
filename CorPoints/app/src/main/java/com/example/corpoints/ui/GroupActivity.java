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
import com.example.corpoints.layer_server.DataCash;
import com.example.corpoints.layer_server.MainAPI;
import com.example.corpoints.utils.GroupValidator;
import com.example.restful.models.Account;
import com.example.restful.models.Group;
import com.example.restful.models.RequestInGroup;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.net.URISyntaxException;
import java.util.List;


public class GroupActivity extends AppCompatActivity {
    private Account myAccount;
    private ArrayAdapter<String> AdapterUsersGroup;
    private GroupValidator validator;

    private String typeGroup;
    private Group CurrentGroup;

    private FrameLayout main_layout;

    private FragmentTransaction fragmentTransaction;
    private BudgetGroupFragment budgetGroupFragment;
    private RequestsFragment requestsFragment;

    private View.OnClickListener SendRequestListener;
    private View.OnClickListener LeaveGroupListener;
    private View.OnClickListener BudgetGroupListener;
    private View.OnClickListener MenuRequestsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        DataCash.UpdateData();
        InitResources();
        initIntent();
        initializeAdapter();

        LeaveGroupListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainAPI.leave();
                finish();
            }
        };
        BudgetGroupListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenFragment(budgetGroupFragment, main_layout.getId());
            }
        };

        MenuRequestsListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenFragment(requestsFragment, main_layout.getId());
            }
        };
        SendRequestListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(GroupActivity.this, validator.CheckGroupForRequest(myAccount), Toast.LENGTH_SHORT).show();
                if (!validator.hasErrors())
                    MainAPI.SendRequestToGroup(new RequestInGroup(myAccount, CurrentGroup));
            }
        };

        ((ListView)findViewById(R.id.ListUsersGroup)).setAdapter(AdapterUsersGroup);

        initTypeGroup();
    }

    private void initializeAdapter() {
        AdapterUsersGroup = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        //Group group = DataCash.getGroups().stream().filter(x->x.getName().equals(nameGroup)).findAny().orElse(null);
        List<Account> users = CurrentGroup.getUsers();

        for (Account user : users)
            AdapterUsersGroup.add(user.getUsername());
    }

    public void ExitPFragment() {
        finish();
    }

    private void initIntent() {
        Intent intent = getIntent();
        String nameGroup = intent.getStringExtra("nameGroup");
        CurrentGroup = DataCash.getGroups().stream()
                .filter(x->x.getName().equals(nameGroup)).findAny().orElse(null);
        if (CurrentGroup == null) finish();
    }

    private void initTypeGroup() {
        if (CurrentGroup.getAdmin().getUsername().equals(myAccount.getUsername()))
            typeGroup = "Administrator";
        else if (CurrentGroup.getName().equals(myAccount.getGroup().getName()))
            typeGroup = "Member";
        else
            typeGroup = "Observer";


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
    }
    public Group getGroup() { return CurrentGroup; }

    private void InitResources() {
        validator = new GroupValidator();

        main_layout = findViewById(R.id.group_window);

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        budgetGroupFragment = new BudgetGroupFragment();
        requestsFragment    = new RequestsFragment();

        myAccount = DataCash.getMyAccount();
    }

    private void OpenFragment(Fragment fragment, int id_layout) {
        main_layout.removeAllViews();

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(id_layout, fragment);
        fragmentTransaction.commit();
    }
}
