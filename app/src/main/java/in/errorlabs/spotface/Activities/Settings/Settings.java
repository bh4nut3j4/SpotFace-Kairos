package in.errorlabs.spotface.Activities.Settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
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

import java.util.concurrent.TimeUnit;

import in.errorlabs.spotface.MainMenu;
import in.errorlabs.spotface.R;
import in.errorlabs.spotface.Utils.Connection;
import in.errorlabs.spotface.Utils.Constants;
import in.errorlabs.spotface.Utils.SharedPrefs;
import okhttp3.OkHttpClient;

public class Settings extends AppCompatActivity {

    Button reset,back,changebtn,editbtn,ok,cncl;
    OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build();
    LoadToast loadToast;
    Connection connection;
    SharedPrefs sharedPrefs;
    RelativeLayout rel1,rel2;
    EditText fre;
    TextView per,prgstxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        reset= (Button) findViewById(R.id.reset_btn);
        back= (Button) findViewById(R.id.back);
        changebtn= (Button) findViewById(R.id.changebtn);
        editbtn= (Button) findViewById(R.id.editbtn);
        rel1= (RelativeLayout) findViewById(R.id.rel1);
        rel2= (RelativeLayout) findViewById(R.id.rel2);
        ok= (Button) findViewById(R.id.btnok);
        cncl= (Button) findViewById(R.id.cancl);
        fre= (EditText) findViewById(R.id.thrfreq);
        per = (TextView) findViewById(R.id.tt1);
        loadToast=new LoadToast(this);
        loadToast.setText("Resetting...");
        connection=new Connection(this);
        sharedPrefs = new SharedPrefs(this);
        int val = sharedPrefs.getCam_prefs();
        if (val==0){
            changebtn.setText("Back");
        }else {
            changebtn.setText("Front");
        }
        per.setText(sharedPrefs.getPer_prefs()+" %");
        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rel1.setVisibility(View.GONE);
                rel2.setVisibility(View.VISIBLE);
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String f =fre.getText().toString();
                int i = Integer.parseInt(String.valueOf(f));
                if (i==0 || i>100 || i<40){
                    fre.setText(null);
                    fre.setError("Invalid Input");
                }else {
                    sharedPrefs.setPer_prefs(f);
                    String s = String.valueOf(sharedPrefs.getPer_prefs()+" %");
                    per.setText(f);
                    rel1.setVisibility(View.VISIBLE);
                    rel2.setVisibility(View.GONE);
                }
            }
        });

        changebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPrefs.getCam_prefs()==0){
                    sharedPrefs.setCam_prefs();
                    changebtn.setText("Front");
                }else {
                    sharedPrefs.setCam_prefs();
                    changebtn.setText("Back");
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainMenu.class));
                finish();
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                builder.setTitle(String.format("%1$s","RESET TRAIN DATA"));
                builder.setMessage("This action will delete all the trained data !\n" +
                        "Proceed ?\n" +
                        "Recommended: No");
                builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if (connection.isInternet()){
                                resettraindata("errorlabs-spotface");
                            }else {
                                Toast.makeText(getApplicationContext(),"No Internet",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("No",null);
                builder.setIcon(R.drawable.lotte);
                AlertDialog welcomeAlert = builder.create();
                welcomeAlert.show();
                ((TextView) welcomeAlert.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
            }
        });
    }

    public void resettraindata(String galleryname) throws JSONException {
        loadToast.show();
        JSONObject obj = new JSONObject();
        obj.put("gallery_name",galleryname);
        AndroidNetworking.post(Constants.reset)
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
                        String status = null;
                        try {
                            if (!response.has("Errors")){
                                status = response.getString("status");
                                if (status.equals("Complete")){
                                    Toast.makeText(getApplicationContext(),"Reset Successfull",Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(),MainMenu.class));
                                    finish();
                                }else {
                                    Toast.makeText(getApplicationContext(),"Reset Failed",Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(),MainMenu.class));
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
                                        Toast.makeText(getApplicationContext(),"Reset Successfull",Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(),MainMenu.class));
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        loadToast.error();
                        Toast.makeText(getApplicationContext(),"Reset failed, Try again later",Toast.LENGTH_SHORT).show();
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
