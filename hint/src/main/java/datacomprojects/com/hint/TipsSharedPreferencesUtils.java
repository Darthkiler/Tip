package datacomprojects.com.hint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;


@SuppressLint("CommitPrefEdits")
public class TipsSharedPreferencesUtils {

    private static TipsSharedPreferencesUtils instance;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private TipsSharedPreferencesUtils(Context context){
        sharedPreferences = context.getSharedPreferences(context.getPackageName() + "hints", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static void removeFile(Context context) {
        context.getSharedPreferences(context.getPackageName() + "hints", Context.MODE_PRIVATE).edit().clear().apply();
    }

    public void apply(){
        editor.apply();
    }

    public SharedPreferences.Editor putBoolean(String key, boolean value){
        editor.putBoolean(key, value);
        return editor;
    }

    public SharedPreferences.Editor putInt(String key, int value){
        editor.putInt(key, value);
        return editor;
    }

    public SharedPreferences.Editor putLong(String key, long value){
        editor.putLong(key, value);
        return editor;
    }

    public SharedPreferences.Editor putFloat(String key, float value){
        editor.putFloat(key, value);
        return editor;
    }

    public SharedPreferences.Editor putString(String key, String value){
        editor.putString(key, value);
        return editor;
    }


    public boolean getBoolean(String key, boolean defaultValue){
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public int getInt(String key, int defaultValue){
        return sharedPreferences.getInt(key, defaultValue);
    }

    public long getLong(String key, long defaultValue){
        return sharedPreferences.getLong(key, defaultValue);
    }

    public float getFloat(String key, float defaultValue){
        return sharedPreferences.getFloat(key, defaultValue);
    }

    public String getString(String key, String defaultValue){
        return sharedPreferences.getString(key, defaultValue);
    }

    public static TipsSharedPreferencesUtils getInstance(Context context) {
        if(instance==null)
            instance = new TipsSharedPreferencesUtils(context);
        return instance;
    }
}
