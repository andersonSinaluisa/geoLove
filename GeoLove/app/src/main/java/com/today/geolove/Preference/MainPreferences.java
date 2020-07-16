package com.today.geolove.Preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.today.geolove.R;

public class MainPreferences {

    public static void userdata(Context c, String email, String username, String provider, String id){


        SharedPreferences sharedPref= c.getSharedPreferences(c.getString(R.string.app_name), c.MODE_PRIVATE);
        SharedPreferences.Editor edit =sharedPref.edit();
        edit.putString("email",email);
        edit.putString("username",username);
        edit.putString("provider",provider);
        edit.putString("id_usuario",id);
        edit.commit();

    }

    public static void dataGeo(Context c, String id){
        SharedPreferences sharedPref= c.getSharedPreferences(c.getString(R.string.app_name), c.MODE_PRIVATE);
        SharedPreferences.Editor edit =sharedPref.edit();
        edit.putString("id",id);
        edit.commit();
    }

    public static String email(Context c){
        SharedPreferences sharedPref= c.getSharedPreferences(c.getString(R.string.app_name), c.MODE_PRIVATE);
        return sharedPref.getString("email","");
    }

    public static String id_geo(Context c){
        SharedPreferences sharedPref= c.getSharedPreferences(c.getString(R.string.app_name), c.MODE_PRIVATE);
        return sharedPref.getString("id","");
    }
}
