package in.errorlabs.spotface.Activities.ViewAll;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import in.errorlabs.spotface.MainMenu;
import in.errorlabs.spotface.R;
import in.errorlabs.spotface.Utils.Connection;
import in.errorlabs.spotface.Utils.Constants;
import okhttp3.OkHttpClient;

public class ViewAll extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    Context context;
    LoadToast loadToast;
    ViewAdapter adapter;
    OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build();
    View_Model model;
    ArrayList<View_Model> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        recyclerView= (RecyclerView) findViewById(R.id.names_recyclerview);
        loadToast = new LoadToast(this);
        loadToast.setText("Loading...");
        loadToast.show();
        model= new View_Model();
        list = new ArrayList<>();
        Connection connection = new Connection(this);
        Boolean checkinternet =(connection.isInternet());
        if (checkinternet) {
            Log.d("TAG","online startted");
            try {
                getData("errorlabs-spotface");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),MainMenu.class));
        }
    }


    public void getData(String gallaryname) throws JSONException {
        loadToast.show();
        JSONObject obj = new JSONObject();
        obj.put("gallery_name",gallaryname);
        AndroidNetworking.post(Constants.viewall)
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
                            if (!response.has("Errors")){
                                JSONArray array;
                                try {
                                    array=response.getJSONArray("subject_ids");
                                    for(int i=0;i<=array.length();i++){
                                        model= new View_Model();
                                        model.setNames(String.valueOf(array.get(i)));
                                        list.add(model);
                                    }
                                    recyclerView.setAdapter(adapter);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                layoutManager = new LinearLayoutManager(ViewAll.this);
                                recyclerView.setLayoutManager(layoutManager);
                                recyclerView.setHasFixedSize(true);
                                adapter = new ViewAdapter(list,ViewAll.this);
                                recyclerView.setAdapter(adapter);

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
                                    Toast.makeText(getApplicationContext(), "No Registrations Found", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(getApplicationContext(),MainMenu.class);
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
