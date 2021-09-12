package ir.fanap.samplesdk.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import ir.fanap.samplesdk.R;
import ir.fanap.samplesdk.ui.login.AuthorizationFragment;
import ir.fanap.samplesdk.ui.login.SwitchFragment;

public class LoginActivity extends AppCompatActivity implements SwitchFragment {

    public Boolean isVerifyState = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        if ( MyPreference.getInstance(this).getAccessToken().length()==0) {
//            if (savedInstanceState == null)
//                switchFragment(AuthorizationFragment.newInstance());
//        } else {
            goToMainActivity();
//        }

    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    private void switchFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, fragment, fragment.getTag())
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (isVerifyState) {
            switchFragment(AuthorizationFragment.newInstance());
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onSwitch(Fragment fragment) {
        switchFragment(fragment);
    }

}
