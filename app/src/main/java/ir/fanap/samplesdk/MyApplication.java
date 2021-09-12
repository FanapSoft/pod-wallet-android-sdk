package ir.fanap.samplesdk;

import android.app.Application;
import android.content.Context;

import androidx.core.content.res.ResourcesCompat;
import androidx.multidex.MultiDex;

import java.util.ArrayList;
import java.util.Objects;


public class MyApplication extends Application {
    static String BASE_URL = "http://192.168.212.58:8000/api/oauth2/otp/";

    @Override
    public void onCreate() {
        super.onCreate();

//        ArrayList<String> scopes = new ArrayList<String>();
//        scopes.add("profile");
//        scopes.add("wallet_balance");
//        scopes.add("wallet_bill");
//        scopes.add("wallet_settlement");

//        otp = new PodOtp.Builder()
//                .with(getApplicationContext())
//                .setBaseUrl(MyApplication.BASE_URL)
//                .setAccessScope(scopes)
//                .build();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
