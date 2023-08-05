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
import com.example.corpoints.layer_server.DataCash;
import com.example.corpoints.layer_server.MainAPI;
import com.example.corpoints.utils.ScoreValidator;
import com.example.restful.models.Account;


public class BudgetGroupFragment extends Fragment {
    private FrameLayout main_layout;
    private Account myAccount;
    private ScoreValidator validator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main_layout = (FrameLayout) inflater.inflate(R.layout.fragment_budget_group, container, false);
        myAccount = DataCash.getMyAccount();
        validator = new ScoreValidator();

        View.OnClickListener SendScoreListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String scoreStr = ((EditText)main_layout.findViewById(R.id.et_scoregroup)).getText().toString();

                Toast.makeText(getActivity(), validator.CheckCorrectRequest
                        (scoreStr, myAccount, myAccount.getGroup().getName()), Toast.LENGTH_LONG).show();

                if (!validator.hasErrors()) {
                    MainAPI.SendScoreToGroup(Integer.valueOf(scoreStr));
                }
            }
        };

        View.OnClickListener ExitListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GroupActivity)getActivity()).ExitPFragment();
            }
        };

        TextView textView = main_layout.findViewById(R.id.scoreq);
        textView.setText(String.valueOf(myAccount.getGroup().getGroupScore()));
        main_layout.findViewById(R.id.SendScoreBudget).setOnClickListener(SendScoreListener);
        ((TextView)main_layout.findViewById(R.id.textScore)).setText((String.valueOf(myAccount.getScore())));
        main_layout.findViewById(R.id.back_budget).setOnClickListener(ExitListener);

        return main_layout;
    }
}
