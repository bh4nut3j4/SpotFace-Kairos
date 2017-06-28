package in.errorlabs.spotface.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by root on 5/8/17.
 */

public class SharedPrefs {
    Context context;
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    public String cam_prefs="0";
    public String per_prefs="60";

    public SharedPrefs(Context context) {
        this.context = context;
        sharedPrefs = context.getSharedPreferences("camprefs", Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();
    }

    public void setCam_prefs(){
        if (getCam_prefs()==0){
            editor.putInt(cam_prefs,1);
        }else if (getCam_prefs()==1){
            editor.putInt(cam_prefs,0);
        }
        editor.commit();
    }
    public int getCam_prefs(){
        return sharedPrefs.getInt(cam_prefs,1);
    }

    public void setPer_prefs(String val){
        editor.putString(per_prefs,val);
        editor.commit();
    }
    public String  getPer_prefs(){
        return sharedPrefs.getString(per_prefs,"60");
    }
}

