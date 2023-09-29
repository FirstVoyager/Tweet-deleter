package limitless.tweetdeleter.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import limitless.tweetdeleter.Other.Constant;

public class SharePref {

    public static String showDeleteMessage = "showDeleteMessage";
    public static String showAds = "showAds";
    public static String last_ad_show_time = "action_for_ads";
    public static String delayDelete = "delayDelete";
    public static String userFilter = "useFilter";
    public static String showIntersitialStart = "showIntersitialStart";
    public static String themeIndex = "themeIndex";
    public static String changeTheme = "changeTheme";
    public static String ProVersion = "ProVersion";
    public static String action_count_per_day = "action_count_per_day";
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    public SharePref(Context context) {
        sharedPreferences = context.getSharedPreferences(Constant.share_pref_name, Context.MODE_PRIVATE);
    }

    @SuppressLint("CommitPrefEdits")
    private void getEditor() {
        editor = sharedPreferences.edit();
    }

    public void putString(String key, String text){
        getEditor();
        editor.putString(key, text).apply();
    }

    public String getString(String key, String def){
        return sharedPreferences.getString(key, def);
    }

    public void putBoolean(String key, boolean b){
        getEditor();
        editor.putBoolean(key, b).apply();
    }

    public boolean getBoolean(String key, boolean def){
        return sharedPreferences.getBoolean(key, def);
    }

    public void putInt(String key, int n){
        getEditor();
        editor.putInt(key, n).apply();
    }

    public int getInt(String key, int def){
        return sharedPreferences.getInt(key, def);
    }

    public void putLong(String key, long l) {
        getEditor();
        editor.putLong(key, l).apply();
    }

    public long getLong(String key, long def){
        return sharedPreferences.getLong(key, def);
    }


    public void clearAll() {
        getEditor();
        editor.clear().apply();
    }
}
