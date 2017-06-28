package in.errorlabs.spotface.Activities.Register;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import in.errorlabs.spotface.MainMenu;
import in.errorlabs.spotface.R;

public class TrainSuccess extends AppCompatActivity {

    Button back2pic,mainmenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_success);
        back2pic= (Button) findViewById(R.id.back_to_pic);
        mainmenu= (Button) findViewById(R.id.mainmenu);
        back2pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Capture.class));
                finish();
            }
        });
        mainmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainMenu.class));
                finish();
            }
        });
    }
    @Override
    public void onBackPressed(){
        startActivity(new Intent(getApplicationContext(),MainMenu.class));
        finish();
        super.onBackPressed();
    }

}
