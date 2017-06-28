package in.errorlabs.spotface.Activities.Authenticate;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import in.errorlabs.spotface.MainMenu;
import in.errorlabs.spotface.R;

public class Result extends AppCompatActivity {

    ImageView img;
    Button cancel;
    TextView name,confidence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        img = (ImageView) findViewById(R.id.img_view);
        cancel = (Button) findViewById(R.id.cancel);
        name= (TextView) findViewById(R.id.name);
        confidence= (TextView) findViewById(R.id.conf);
        Bundle extras = getIntent().getExtras();
        final byte[] byteArray = extras.getByteArray("imgg");
        String name_id=extras.getString("name");
        String conf=extras.getString("conf");
        float p = Float.parseFloat(conf);
        float fp = p*100;
        String v = String.valueOf(fp);

        Toast.makeText(getApplicationContext(),v+" %",Toast.LENGTH_SHORT).show();
        if (conf.equals("0.0")){
            confidence.setVisibility(View.GONE);
            name.setTextColor(getResources().getColor(R.color.red));
        }
        name.setText(name_id);
        confidence.setText("Confidence: "+v+" %");
        Bitmap bmpp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        Bitmap bmp = Bitmap.createBitmap(bmpp, 0, 0, bmpp.getWidth(), bmpp.getHeight(), null, true);
        img.setImageBitmap(bmp);
        cancel.setOnClickListener(new View.OnClickListener() {
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
