package in.errorlabs.spotface.Activities.Authenticate;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import in.errorlabs.spotface.Utils.Connection;
import in.errorlabs.spotface.MainMenu;
import in.errorlabs.spotface.R;
import in.errorlabs.spotface.Utils.Constants;
import okhttp3.OkHttpClient;

public class ConfirmRecPicture extends AppCompatActivity {

    ImageView img;
    Button yes,clickagain;
    byte[] bytes;
    Bitmap bmp;
    String b64_img,encodedImage;
    OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build();
    byte[] finalByteArray;
    LoadToast loadToast;
    Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_rec_picture);
        img = (ImageView) findViewById(R.id.img_view);
        Bundle extras = getIntent().getExtras();
        byte[] byteArray = extras.getByteArray("img");
        int cam = extras.getInt("cam");
        loadToast=new LoadToast(this);
        loadToast.setText("Loading...");
        connection=new Connection(this);
        if (cam==0){
            Bitmap bmpp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            bmp = Bitmap.createBitmap(bmpp, 0, 0, bmpp.getWidth(), bmpp.getHeight(), matrix, true);
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
            bmp = Bitmap.createBitmap(bmpp, 0, 0, bmpp.getWidth(), bmpp.getHeight(), matrix, true);
            try {
                bytes =save(bmp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            img.setImageBitmap(bmp);
        }
        b64_img= getStringImage(bmp);

        yes= (Button) findViewById(R.id.yes);
        clickagain= (Button) findViewById(R.id.click_again);
        clickagain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Recognition.class));
                finish();
            }
        });
        finalByteArray = bytes;
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (connection.isInternet()){
                        recognize(b64_img,"errorlabs-spotface");
                    }else {
                        Toast.makeText(getApplicationContext(),"No Internet",Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private byte[] save(Bitmap bits) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bits.compress(Bitmap.CompressFormat.JPEG,100,stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    public void recognize(String base64,String gallaryname) throws JSONException {
        loadToast.show();
        JSONObject obj = new JSONObject();
        obj.put("image",base64);
        obj.put("gallery_name",gallaryname);
        AndroidNetworking.post(Constants.recognize)
                .addHeaders("Content-Type","application/json")
                .addHeaders("app_id", Constants.app_id)
                .addHeaders("app_key",Constants.app_key)
                .addJSONObjectBody(obj)
                .setPriority(Priority.HIGH)
                .setOkHttpClient(okHttpClient)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadToast.success();
                        Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_SHORT);
                        Log.e("LOGGG",response.toString());
                        if (!response.has("Errors")){
                            if (response.has("images")){
                                String status,confidence,subjectid,msg;
                                JSONArray images =null;
                                JSONObject index=null;
                                JSONObject transaction=null;
                                try {
                                    images=response.getJSONArray("images");
                                    index=images.getJSONObject(0);
                                    transaction=index.getJSONObject("transaction");
                                    status=transaction.getString("status");
                                    if (status.equals("success")){
                                        subjectid=transaction.getString("subject_id");
                                        confidence=transaction.getString("confidence");
                                        Intent i = new Intent(getApplicationContext(),Result.class);
                                        i.putExtra("imgg", finalByteArray);
                                        i.putExtra("name",subjectid);
                                        i.putExtra("conf",confidence);
                                        startActivity(i);
                                        finish();
                                    }else if(status.equals("failure")){
                                        msg=transaction.getString("message");
                                        if (msg.equals("No match found")){
                                            Intent i = new Intent(getApplicationContext(),Result.class);
                                            i.putExtra("imgg", finalByteArray);
                                            i.putExtra("name","No match found");
                                            i.putExtra("conf","0.0");
                                            startActivity(i);
                                            finish();
                                        }else {
                                            Toast.makeText(getApplicationContext(), "Failed, Try again later", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), MainMenu.class));
                                            finish();
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }else {
                                Toast.makeText(getApplicationContext(), "Try again later", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainMenu.class));
                                finish();
                            }
                        }else {
                            JSONArray errors =null;
                            JSONObject ind=null;
                            try {
                                errors=response.getJSONArray("Errors");
                                ind = errors.getJSONObject(0);
                                String errorcode=ind.getString("ErrCode");
                                if (errorcode.equals("5004")){
                                    Toast.makeText(getApplicationContext(), "Train data and try again", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(getApplicationContext(),Result.class);
                                    i.putExtra("imgg", finalByteArray);
                                    i.putExtra("name","No match found");
                                    i.putExtra("conf","0.0");
                                    startActivity(i);
                                    finish();
                                }else if(errorcode.equals("5002")) {
                                    Toast.makeText(getApplicationContext(), "No faces found in the image", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), MainMenu.class));
                                    finish();
                                }else {
                                    Toast.makeText(getApplicationContext(), "Try again later", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), MainMenu.class));
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("LOGGG",anError.toString());
                        loadToast.error();
                        Toast.makeText(getApplicationContext(), "Try again later", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainMenu.class));
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
