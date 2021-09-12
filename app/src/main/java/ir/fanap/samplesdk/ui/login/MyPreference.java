package ir.fanap.samplesdk.ui.login;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPreference {

    private static final String ACCESS_TOKEN = "access_token";

    private static MyPreference instance = null;
    private SharedPreferences sharedPrefManager;

    public static MyPreference getInstance(Context context) {
        if (instance == null) {
            instance = new MyPreference(context);
        }
        return instance;
    }

    public MyPreference(Context context) {
        sharedPrefManager = context.getSharedPreferences(
                context.getPackageName(),
                Context.MODE_PRIVATE
        );
    }



    public void setAccessToken(String token) {
        sharedPrefManager.edit().putString(ACCESS_TOKEN, token)
                .apply();
    }

    public String getAccessToken() {
        return sharedPrefManager.getString(ACCESS_TOKEN, "");
    }

}
