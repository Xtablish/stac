package com.stacit.stac.activities.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    //an instance of the built in SharedPreferences class
    private final SharedPreferences sharedPreferences;

    //default method to this class
    public PreferenceManager(Context context){
        sharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    //modifies a string
    public void putBoolean(String key, Boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    //returns the value of the string
    public Boolean getBoolean(String key){
        return sharedPreferences.getBoolean(key, false);
    }

    //inserts a string and it's boolean value
    public void putString(String key, String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    //gets only the string by returning a null value
    public String getString(String key){
        return sharedPreferences.getString(key, null);
    }

    //clears out everything (both the string and its value)
    public void clear(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
