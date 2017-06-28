package in.errorlabs.spotface.Activities.Register;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import in.errorlabs.spotface.MainMenu;
import in.errorlabs.spotface.R;

public class ConfirmPicture extends AppCompatActivity {

    ImageView img;
    Button yes,clickagain;
    String filename;
    File file;
    byte[] bytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_picture);
        img = (ImageView) findViewById(R.id.img_view);
        Bundle extras = getIntent().getExtras();
        byte[] byteArray = extras.getByteArray("img");
        int cam = extras.getInt("cam");
        final File file = new File(Environment.getExternalStorageDirectory()+"/pic.jpg");

        if (cam==0){
            Bitmap bmpp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap bmp = Bitmap.createBitmap(bmpp, 0, 0, bmpp.getWidth(), bmpp.getHeight(), matrix, true);
            try {
                bytes =save(bmp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            img.setImageBitmap(bmp);

        }else if (cam==1){
            Bitmap bmpp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            Matrix matrix = new Matrix();
            matrix.postRotate(270);
            Bitmap bmp = Bitmap.createBitmap(bmpp, 0, 0, bmpp.getWidth(), bmpp.getHeight(), matrix, true);
            try {
                bytes =save(bmp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            img.setImageBitmap(bmp);
        }

        yes= (Button) findViewById(R.id.yes);
        clickagain= (Button) findViewById(R.id.click_again);
        clickagain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Capture.class));
                finish();
            }
        });
        final byte[] finalByteArray = bytes;
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),ConfirmCapture.class);
                i.putExtra("imgg", finalByteArray);
                startActivity(i);
                finish();
            }
        });
    }

    private byte[] save(Bitmap bits) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bits.compress(Bitmap.CompressFormat.JPEG,100,stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(getApplicationContext(),MainMenu.class));
        finish();
        super.onBackPressed();
    }
}
