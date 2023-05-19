package com.example.corpoints;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

        View.OnClickListener ClickIdent = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TypeIdent ident = TypeIdent.aut;
                if (view == findViewById(R.id.Signup))
                    ident = TypeIdent.reg;

                boolean itog = Server.StartProtocolIdentefication(
                        ((TextView)findViewById(R.id.et_login)).getText().toString(),
                        ((TextView)findViewById(R.id.et_password)).getText().toString(), ident);
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
