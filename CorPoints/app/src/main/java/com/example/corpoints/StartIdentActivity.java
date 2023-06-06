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

import com.example.corpoints.cserver.Server;

/**start identefication activity */
public class StartIdentActivity extends Activity {
    public enum TypeIdent { aut, reg };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.acitivity_identefication);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Test Signin without server
        View.OnClickListener ClickTestSignin = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartIdentActivity.this, MainActivity.class);
                startActivity(intent);
            }
        };
        findViewById(R.id.TestSignin).setOnClickListener(ClickTestSignin);
        //
        String text = "Принимаю <a href='https://mer0n1.github.io/CorPoints/'>политику конфиденциальности </a>";
        TextView textView = findViewById(R.id.CheckBox_ident);
        textView.setText(Html.fromHtml(text));
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        
        View.OnClickListener ClickIdent = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TypeIdent ident = TypeIdent.aut;
                if (view == findViewById(R.id.Signup))
                    ident = TypeIdent.reg;

                //Check соответствует ли логин и пароль требованиям
                CheckBox checkBox = findViewById(R.id.CheckBox_ident);
                if (!checkBox.isChecked()) {
                    Toast.makeText(StartIdentActivity.this, "Не нажата галочка", Toast.LENGTH_LONG).show();
                    return;
                }

                String login = ((TextView)findViewById(R.id.et_login)).getText().toString();
                String password = ((TextView)findViewById(R.id.et_password)).getText().toString();
                if (!login.matches("[a-zA-Z0-9]+") || !password.matches("[a-zA-Z0-9]+")) {
                    Toast.makeText(StartIdentActivity.this, "Ошибка. Только буквы и цифры", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    if (!(login.length() >= 3 && login.length() <= 14)) {
                        Toast.makeText(StartIdentActivity.this, "Логин должен быть от 3 до 14 символов", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (!(password.length() >= 6 && password.length() <= 8)) {
                        Toast.makeText(StartIdentActivity.this, "Пароль должен быть от 6 до 8 символов", Toast.LENGTH_LONG).show();
                        return;
                    }
                }


                boolean itog = Server.StartProtocolIdentefication(
                        login, password, ident, StartIdentActivity.this);
                if (itog) {
                    Intent intent = new Intent(StartIdentActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        };
        findViewById(R.id.Signup).setOnClickListener(ClickIdent);
        findViewById(R.id.Signin).setOnClickListener(ClickIdent);
    }

}
