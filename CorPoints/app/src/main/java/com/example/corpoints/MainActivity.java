package com.example.corpoints;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.corpoints.cserver.MyAccount;
import com.example.corpoints.cserver.Server;
import com.example.corpoints.ui.GroupRedactorCreatedFragment;
import com.example.corpoints.ui.ListGroupsFragment;
import com.example.corpoints.ui.ProfileFragment;
import com.example.corpoints.ui.SenderMenuFragment;


public class MainActivity extends AppCompatActivity {

    private MyAccount myAccount;

    private FragmentTransaction fragmentTransaction;
    private ProfileFragment profileFragment;
    private SenderMenuFragment senderMenuFragment;
    private ListGroupsFragment listGroupsFragment;

    private GroupRedactorCreatedFragment redactorCreatedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.app_bar_main); //пока без выплывающего меню
        getLayoutInflater().inflate(R.layout.fragment_profile, findViewById(R.id.layout_main_content));

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        profileFragment = new ProfileFragment();
        senderMenuFragment = new SenderMenuFragment();
        listGroupsFragment = new ListGroupsFragment();
        redactorCreatedFragment = new GroupRedactorCreatedFragment();

        //синхронизация
        Server.myAccount.adapter = senderMenuFragment.getArrayAdapter();
        Server.reader.setActivity(this);
        myAccount = Server.synchronizationMyAccount(); //ожидание обновление информации с сервера, синхронизация

        InitResources();
        UpdateProfile(); //show Profile fragment


        //Переключение меню
        View.OnClickListener onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FrameLayout frameLayout = findViewById(R.id.layout_main_content);
                frameLayout.removeAllViews();

                if (v == findViewById(R.id.image_profile)) {
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.layout_main_content, profileFragment);
                    fragmentTransaction.commit();

                    Server.UpdateInfoProtocolAccount();
                    profileFragment.UpdateScore(myAccount.getScore());
                    profileFragment.setNickname(myAccount.getName());
                }
                if (v == findViewById(R.id.image_sender)) {
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.layout_main_content, senderMenuFragment);
                    fragmentTransaction.commit();
                    Server.UpdateInfoProtocolAccounts();
                }
                if (v == findViewById(R.id.image_groups)) {

                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.layout_main_content, listGroupsFragment);
                    fragmentTransaction.commit();
                    Server.UpdateInfoProtocolGroups();
                }

                if (v == findViewById(R.id.image_news)) {
                    Toast.makeText(MainActivity.this, "В разработке...", Toast.LENGTH_LONG).show();
                }
            }
        };
        findViewById(R.id.image_profile).setOnClickListener(onClick);
        findViewById(R.id.image_sender).setOnClickListener(onClick);
        findViewById(R.id.image_groups).setOnClickListener(onClick);
    }


    public void setListUsers(String[] array) {
        senderMenuFragment.setListUsers(array);
    }
    public void setListGroups(String[] array, String[] score) {
        listGroupsFragment.setListGroups(array);
    }
    public void setUsersGroup(String[] array) {
        myAccount.AdapterUsersGroup.clear();
        for (int j = 0; j < array.length; j++)
            myAccount.AdapterUsersGroup.add(array[j]);
    }
    public void OpenRedCrGroup(ArrayAdapter array) {
        redactorCreatedFragment.setAdapterGroups(array);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.layout_main_content, redactorCreatedFragment);
        fragmentTransaction.commit();
    }

    public void UpdateScore(int score) {
        profileFragment.UpdateScore(score);
    }
    public void setNickname(String nickname) {
        profileFragment.setNickname(nickname);
    }

    private void UpdateProfile() {
        profileFragment.UpdateScore(myAccount.getScore());
        profileFragment.setNickname(myAccount.getName());
        ((FrameLayout)findViewById(R.id.layout_main_content)).removeAllViews();
        fragmentTransaction.replace(R.id.layout_main_content, profileFragment);
        fragmentTransaction.commit();
    }
    private void InitResources() {
        ((ImageView)findViewById(R.id.image_profile)).setImageResource(R.drawable.user);
        ((ImageView)findViewById(R.id.image_sender)).setImageResource(R.drawable.transfer);
        ((ImageView)findViewById(R.id.image_news)).setImageResource(R.drawable.newspaper);
        ((ImageView)findViewById(R.id.image_groups)).setImageResource(R.drawable.group);
    }

    /*@Override
    protected void onDestroy() {
        System.out.println("Close");
        Server.Close();
        super.onDestroy();
    }*/

}
