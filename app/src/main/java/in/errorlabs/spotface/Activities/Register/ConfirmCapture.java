package in.errorlabs.spotface.Activities.Register;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import java.util.concurrent.TimeUnit;

import in.errorlabs.spotface.MainMenu;
import in.errorlabs.spotface.R;
import in.errorlabs.spotface.Utils.Connection;
import in.errorlabs.spotface.Utils.Constants;
import okhttp3.OkHttpClient;

public class ConfirmCapture extends AppCompatActivity {

    ImageView img;
    Button confirm,cancel;
    EditText name;
    OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build();
    String encodedImage,b64_img;
    LoadToast loadToast;
    Connection connection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_capture);
        AndroidNetworking.initialize(getApplicationContext());
        img = (ImageView) findViewById(R.id.img_view);
        confirm= (Button) findViewById(R.id.confirm);
        cancel = (Button) findViewById(R.id.cancel);
        name= (EditText) findViewById(R.id.name);
        Bundle extras = getIntent().getExtras();
        final byte[] byteArray = extras.getByteArray("imgg");
        Bitmap bmpp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        Bitmap bmp = Bitmap.createBitmap(bmpp, 0, 0, bmpp.getWidth(), bmpp.getHeight(), null, true);
        img.setImageBitmap(bmp);
        b64_img= getStringImage(bmp);
        Log.e("LOGGG","BASE64-"+b64_img);
        loadToast = new LoadToast(this);
        loadToast.setText("Loading...");
        connection = new Connection(this);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainMenu.class));
                finish();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nme= name.getText().toString();
                if (!nme.isEmpty()|| nme.length()!=0 || !nme.equals("")){
                    try {
                        if (connection.isInternet()){
                            enroll(nme,b64_img);
                        }else {
                            Toast.makeText(getApplicationContext(),"No Internet",Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    name.setText(null);
                    name.setError("Required");
                }
            }
        });
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public void enroll(String name,String base64_img) throws JSONException {
        loadToast.show();
        JSONObject obj = new JSONObject();
        obj.put("image",base64_img);
        obj.put("subject_id",name);
        obj.put("gallery_name","errorlabs-spotface");

        AndroidNetworking.post(Constants.enroll)
                .addHeaders("Content-Type","application/json")
                .addHeaders("app_id", Constants.app_id)
                .addHeaders("app_key",Constants.app_key)
                .addJSONObjectBody(obj)
                .setPriority(Priority.HIGH  )
                .setOkHttpClient(okHttpClient)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("LOGGG",response.toString());
                        loadToast.success();
                        if (!response.has("Errors")) {
                            if (response.has("images")) {
                                JSONArray images = null;
                                JSONObject index=null;
                                JSONObject attributes=null;
                                JSONObject gender=null;
                                JSONObject transactions=null;
                                String femaleconfidence,maleconfidence,type,status,confidence,subjectid;
                                try {
                                    images = response.getJSONArray("images");
                                    index = images.getJSONObject(0);
                                    attributes =index.getJSONObject("attributes");
                                    transactions=index.getJSONObject("transaction");
                                    status = transactions.getString("status");
                                    if (status.equals("success")){
                                        gender=attributes.getJSONObject("gender");
                                        femaleconfidence = gender.getString("femaleConfidence");
                                        type=gender.getString("type");
                                        maleconfidence=gender.getString("maleConfidence");
                                        confidence=transactions.getString("confidence");
                                        subjectid=transactions.getString("subject_id");
                                        //Toast.makeText(getApplicationContext(),gender+"\n"+femaleconfidence+"\n"+type+"\n"+maleconfidence+"\n"+confidence+"\n"+subjectid, Toast.LENGTH_SHORT).show();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmCapture.this);
                                        builder.setTitle(String.format("%1$s","Name: "+subjectid));
                                        builder.setMessage("ATTRIBUTES:\n\n\t" +
                                                "Gender:"+type+"\n\n\t" +
                                                "Male Confidence: "+maleconfidence+"\n\n\t" +
                                                "Female Confidence: "+femaleconfidence+"\n\n\n" +
                                                "CONFIDENCE: "+confidence+" ");
                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(getApplicationContext(),TrainSuccess.class));
                                                finish();
                                            }
                                        });
                                        builder.setIcon(R.drawable.lotte);
                                        AlertDialog welcomeAlert = builder.create();
                                        welcomeAlert.show();
                                        ((TextView) welcomeAlert.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
                                    }else {
                                        Toast.makeText(getApplicationContext(),"Failed", Toast.LENGTH_SHORT).show();
                                    }
                                    //Log.e("LOGGG","response"+images.toString()+"\n"+index.toString()+"\n"+attributes.toString()+"\n"+gender.toString()+"\n"+transactions.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                Toast.makeText(getApplicationContext(), "Try again later", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainMenu.class));
                                finish();
                            }
                        }else{

                            JSONArray errors =null;
                            JSONObject ind=null;
                            try {
                                errors=response.getJSONArray("Errors");
                                ind = errors.getJSONObject(0);
                                String errorcode=ind.getString("ErrCode");
                                if (errorcode.equals("5002")){
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
                        loadToast.error();
                        Log.e("LOGGG",""+anError.toString());
                        Toast.makeText(getApplicationContext(),"Failed, Try Again Later",Toast.LENGTH_SHORT).show();
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
