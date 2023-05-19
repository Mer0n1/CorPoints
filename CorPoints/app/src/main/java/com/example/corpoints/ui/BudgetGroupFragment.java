package com.example.corpoints.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.corpoints.R;
import com.example.corpoints.cserver.MyAccount;
import com.example.corpoints.cserver.Server;

public class BudgetGroupFragment extends Fragment {
    private FrameLayout main_layout;
    private MyAccount myAccount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main_layout = (FrameLayout) inflater.inflate(R.layout.fragment_budget_group, container, false);
        myAccount = Server.myAccount;

        View.OnClickListener SendScoreListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String scoreStr = ((EditText)main_layout.findViewById(R.id.et_scoregroup)).getText().toString();
                if (scoreStr.matches("[0-9]+") && !scoreStr.isEmpty()) {
                    int score = Integer.valueOf(scoreStr);

                    if (score <= myAccount.getScore()) {
                        Server.ProtocolSendScoreToGroup(score);
                        Toast.makeText(getActivity(), "Успешно", Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(getActivity(), "Недостаточно баллов", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(getActivity(), "Ввод должен содержать только число", Toast.LENGTH_LONG).show();
            }
        };

        View.OnClickListener ExitListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GroupActivity)getActivity()).ExitPFragment();

            }
        };

        TextView textView = main_layout.findViewById(R.id.scoreq);
        textView.setText(String.valueOf(myAccount.getScoreGroup()));
        main_layout.findViewById(R.id.SendScoreBudget).setOnClickListener(SendScoreListener);
        ((TextView)main_layout.findViewById(R.id.textScore)).setText((String.valueOf(myAccount.getScore())));
        main_layout.findViewById(R.id.back_budget).setOnClickListener(ExitListener);

        return main_layout;
    }
}
