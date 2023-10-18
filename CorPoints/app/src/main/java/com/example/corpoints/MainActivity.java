package com.example.corpoints;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.corpoints.layer_server.DataCash;
import com.example.corpoints.ui.GroupRedactorCreatedFragment;
import com.example.corpoints.ui.ListGroupsFragment;
import com.example.corpoints.ui.ProfileFragment;
import com.example.corpoints.ui.SenderMenuFragment;


public class MainActivity extends AppCompatActivity {

    private FragmentTransaction fragmentTransaction;
    private ProfileFragment profileFragment;
    private SenderMenuFragment senderMenuFragment;
    private ListGroupsFragment listGroupsFragment;

    private GroupRedactorCreatedFragment redactorCreatedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.app_bar_main); //пока без выплывающего меню
        getLayoutInflater().inflate(R.layout.fragment_profile, findViewById(R.id.layout_main_content));

        //DataCash.UpdateData();
        InitResources();
        OpenFragment(profileFragment, R.id.layout_main_content);

        //Переключение меню
        View.OnClickListener onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (v == findViewById(R.id.image_profile))
                    OpenFragment(profileFragment, R.id.layout_main_content);

                if (v == findViewById(R.id.image_sender))
                    OpenFragment(senderMenuFragment, R.id.layout_main_content);

                if (v == findViewById(R.id.image_groups))
                    OpenFragment(listGroupsFragment, R.id.layout_main_content);

                if (v == findViewById(R.id.image_news))
                    Toast.makeText(MainActivity.this, "В разработке...", Toast.LENGTH_LONG).show();

            }
        };
        findViewById(R.id.image_profile).setOnClickListener(onClick);
        findViewById(R.id.image_sender).setOnClickListener(onClick);
        findViewById(R.id.image_groups).setOnClickListener(onClick);
    }

    public void OpenFragment(Fragment fragment, int id_layout) {
        FrameLayout frameLayout = findViewById(id_layout);
        frameLayout.removeAllViews();

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(id_layout, fragment);
        fragmentTransaction.commit();
    }

    public void OpenRedCrGroup() {
        OpenFragment(redactorCreatedFragment, R.id.layout_main_content);
    }

    private void InitResources() {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        profileFragment = new ProfileFragment();
        senderMenuFragment = new SenderMenuFragment();
        listGroupsFragment = new ListGroupsFragment();
        redactorCreatedFragment = new GroupRedactorCreatedFragment();

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

