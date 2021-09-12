package ir.fanap.samplesdk.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ir.fanap.samplesdk.R;
import ir.fanap.samplesdk.ui.LoginActivity;

public class AuthorizationFragment extends Fragment {


    private LoginActivity loginActivity22;
    private ProgressBar progress;
    private View submit;
    private EditText identity;

    public static AuthorizationFragment newInstance() {
        return new AuthorizationFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_authorization, container, false);
        progress = view.findViewById(R.id.progress);
        submit = view.findViewById(R.id.submit);
        identity = view.findViewById(R.id.identity);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loginActivity22 = (LoginActivity) getActivity();
        loginActivity22.isVerifyState = false;
        initialize();
    }


    private void initialize() {

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitClickListener();
            }
        });
    }


    private void onSubmitClickListener() {
        progress.setVisibility(View.VISIBLE);
//        MyApplication.otp.authorization(identity.getText().toString(), this);

    }

//    @Override
//    public void onSuccess(String identity, Long expireTime) {
//        progress.setVisibility(View.GONE);
//        loginActivity22.onSwitch(VerifyFragment.newInstance(identity, expireTime));
//    }
//
//    @Override
//    public void onError(int errorCode, String message) {
//        progress.setVisibility(View.GONE);
//        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
//    }
}
