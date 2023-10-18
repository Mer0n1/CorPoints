package com.example.corpoints.ui;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.corpoints.R;
import com.example.corpoints.layer_server.DataCash;
import com.example.restful.models.Account;

public class ProfileFragment extends Fragment {

    private Account myAccount;
    private FrameLayout main_layout;
    private String name;
    private String score;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DataCash.UpdateData();
        main_layout = (FrameLayout)inflater.inflate(R.layout.fragment_profile, container, false);
        myAccount = DataCash.getMyAccount();

        name = myAccount.getUsername();
        score = String.valueOf(myAccount.getScore());

        //Check name group
        String name_group = "";
        if (myAccount.getGroup() != null)
            name_group = myAccount.getGroup().getName();
        else
            name_group = "Вы не состоите в группе";

        View.OnClickListener onClickExit = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        };
        main_layout.findViewById(R.id.Exit).setOnClickListener(onClickExit);

        ((TextView)main_layout.findViewById(R.id.textNickname)).setText(name);
        ((TextView)main_layout.findViewById(R.id.textView3)).setText(String.valueOf(score));
        ((TextView)main_layout.findViewById(R.id.textNameGroup)).setText(name_group);

        return main_layout;
    }



}
