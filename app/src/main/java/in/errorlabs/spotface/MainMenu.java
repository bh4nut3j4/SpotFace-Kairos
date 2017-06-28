package in.errorlabs.spotface;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import in.errorlabs.spotface.Activities.Authenticate.Recognition;
import in.errorlabs.spotface.Activities.Register.Capture;
import in.errorlabs.spotface.Activities.Settings.Settings;
import in.errorlabs.spotface.Activities.ViewAll.ViewAll;

public class MainMenu extends AppCompatActivity {

    Button auth,register,settings,exit,view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        auth= (Button) findViewById(R.id.auth_with_sp_btn);
        register= (Button) findViewById(R.id.register_face_btn);
        settings = (Button) findViewById(R.id.settings_btn);
        exit= (Button) findViewById(R.id.exit);
        view= (Button) findViewById(R.id.view_btn);
        auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Recognition.class));
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Capture.class));
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Settings.class));
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ViewAll.class));
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    public void onBackPressed(){
        finish();
        super.onBackPressed();
    }
}
