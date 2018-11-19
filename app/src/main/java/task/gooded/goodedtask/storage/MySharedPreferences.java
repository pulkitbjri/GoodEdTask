package task.gooded.goodedtask.storage;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Singleton class for sharedPreferences
 */
public class MySharedPreferences {
    //instance field
    private static SharedPreferences mSharedPreference;
    private static MySharedPreferences mInstance = null;
    private static Context mContext;

    //Shared Preference key
    private String KEY_PREFERENCE_NAME = "GoodEd";


    //private keyS
    public String KEY_DEFAULT = null;




    public MySharedPreferences() {
        mSharedPreference = mContext.getSharedPreferences(KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static MySharedPreferences getInstance(Context context) {
        mContext = context;
        if (mInstance == null) {
            mInstance = new MySharedPreferences();
        }
        return mInstance;
    }


    public void set(String id, String data){
        mSharedPreference.edit().putString(id,data).apply();
    }
    public String get(String id){
        return  mSharedPreference.getString(id,KEY_DEFAULT);
    }

    public Boolean chk (String key){
        return mSharedPreference.contains(key);
    }
    public void remove(String key)
    {
        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.remove(key);
        editor.apply();
    }
    public void removeall()
    {
        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.clear();
        editor.commit();
    }
}
