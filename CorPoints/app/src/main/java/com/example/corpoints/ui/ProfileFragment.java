package com.example.corpoints.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.corpoints.MainActivity;
import com.example.corpoints.R;
import com.example.corpoints.cserver.Server;

public class ProfileFragment extends Fragment {

    private FrameLayout main_layout;
    private String name, score;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main_layout = (FrameLayout)inflater.inflate(R.layout.fragment_profile, container, false);

        View.OnClickListener onClickExit = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Server.Close();
                getActivity().finish();
            }
        };
        main_layout.findViewById(R.id.Exit).setOnClickListener(onClickExit);

        ((TextView)main_layout.findViewById(R.id.textNickname)).setText(name);
        ((TextView)main_layout.findViewById(R.id.textView3)).setText(String.valueOf(score));
        ((TextView)main_layout.findViewById(R.id.textNameGroup)).setText(Server.myAccount.getNameGroup());

        return main_layout;
    }

    public void setNickname(String nickname) {
        name = nickname;
    }
    public void UpdateScore(int score) {
        this.score = String.valueOf(score);
    }

}
