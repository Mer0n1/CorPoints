package com.example.corpoints;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.corpoints.layer_server.DataCash;
import com.example.corpoints.layer_server.MainAPI;
import com.example.corpoints.utils.AuthValidator;
import com.example.restful.models.Account;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

/**start identefication activity */
public class StartIdentActivity extends Activity {
    public enum TypeIdent { aut, reg };
    private AuthValidator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_identefication);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        validator = new AuthValidator();
        initCheckBox();

        //Test Signin without server
        View.OnClickListener ClickTestSignin = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartIdentActivity.this, MainActivity.class);
                startActivity(intent);
            }
        };

        View.OnClickListener ClickIdent = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TypeIdent ident = TypeIdent.aut;
                if (view == findViewById(R.id.Signup))
                    ident = TypeIdent.reg;

                //Check соответствует ли логин и пароль требованиям
                String login = ((TextView)findViewById(R.id.et_login)).getText().toString();
                String password = ((TextView)findViewById(R.id.et_password)).getText().toString();

                if (!validate(login, password)) {

                    Account account = MainAPI.CreateAccount(login, password);
                    boolean itog = false;

                    if (ident == TypeIdent.aut)
                        itog = MainAPI.authentication(account);
                    else {
                        itog = MainAPI.register(account);

                        if (itog)
                            itog = MainAPI.authentication(account);
                    }
                    System.err.println("itog " + itog);

                    if (itog) {
                        DataCash.setMyAccount(account);

                        Intent intent = new Intent(StartIdentActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        };

        findViewById(R.id.Signup).setOnClickListener(ClickIdent);
        findViewById(R.id.Signin).setOnClickListener(ClickIdent);
        findViewById(R.id.TestSignin).setOnClickListener(ClickTestSignin);
    }

    /**Нужно создать класс для валидации отдельно */
    public boolean validate(String login, String password) {
        CheckBox checkBox = findViewById(R.id.CheckBox_ident);

        if (!checkBox.isChecked()) {
            Toast.makeText(StartIdentActivity.this, "Не нажата галочка", Toast.LENGTH_LONG).show();
            return false;
        }

        String textError = validator.validate(login, password);
        if (!textError.isEmpty())
            Toast.makeText(StartIdentActivity.this, validator.validate(login, password), Toast.LENGTH_LONG).show();

        return validator.hasErrors();
    }

    private void initCheckBox() {
        String text = "Принимаю <a href='https://mer0n1.github.io/CorPoints/'>политику конфиденциальности </a>";
        TextView textView = findViewById(R.id.CheckBox_ident);
        textView.setText(Html.fromHtml(text));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        ((CheckBox)findViewById(R.id.CheckBox_ident)).setChecked(true);
    }
}
