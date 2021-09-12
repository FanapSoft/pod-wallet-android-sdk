package ir.fanap.samplesdk.ui.login;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import ir.fanap.samplesdk.R;
import ir.fanap.samplesdk.ui.LoginActivity;

public class VerifyFragment extends Fragment {

    private CountDownTimer timer;
    private MutableLiveData<Long> cdTime = new MutableLiveData<Long>();
    private LoginActivity loginActivity;
    private View submit_btn;
    private View back_btn;
    private View retry;
    private TextView countDownTimer;
    private View progress;
    private EditText verifyCode;
    private String identity;

    public static VerifyFragment newInstance(String identity, Long exp) {
        VerifyFragment fragment = new VerifyFragment();
        Bundle bundle = new Bundle();
        bundle.putString("IDENTITY", identity);
        bundle.putLong("EXP", exp);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verify, container, false);
        submit_btn = view.findViewById(R.id.submit_btn);
        back_btn = view.findViewById(R.id.back_btn);
        retry = view.findViewById(R.id.retry);
        countDownTimer = view.findViewById(R.id.countDownTimer);
        progress = view.findViewById(R.id.progress);
        verifyCode = view.findViewById(R.id.verifyCode);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() == null)
            return;

        identity = getArguments().getString("IDENTITY");
        long exp = getArguments().getLong("EXP");
        loginActivity = (LoginActivity) getActivity();
        loginActivity.isVerifyState = true;
        initialize(identity, exp);
    }

    private void initialize(final String identity, Long exp) {
        cdTime.postValue(exp);
        startTimer(exp);

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitClickListener(verifyCode.getText().toString(), identity);
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackClickListener();
            }
        });

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRetryClickListener(identity);
            }
        });

        cdTime.observe(this,
                new Observer<Long>() {
                    @Override
                    public void onChanged(Long aLong) {
                        countDownTimer.setText(new StringBuilder()
                                .append(aLong / 60).append(":").append(aLong % 60)
                                .append(" تا ارسال مجدد"));
                    }
                });
    }

    private void onRetryClickListener(String identity) {
//        MyApplication.otp.authorization(identity, null);
        progress.setVisibility(View.VISIBLE);
    }

    private void onBackClickListener() {
        AuthorizationFragment identityFragment =
                AuthorizationFragment.newInstance();
        loginActivity.onSwitch(identityFragment);
    }


    private void startTimer(Long exp) {
        retry.setVisibility(View.INVISIBLE);
        countDownTimer.setVisibility(View.VISIBLE);
        timer = new CountDownTimer(exp * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                cdTime.postValue(cdTime.getValue());
            }

            @Override
            public void onFinish() {
                submit_btn.setEnabled(true);
                retry.setVisibility(View.VISIBLE);
                countDownTimer.setVisibility(View.INVISIBLE);
            }
        };

    }

    private void onSubmitClickListener(String code, String identity) {
//        MyApplication.otp.verify(code, identity, new VerifyCallBack() {
//            @Override
//            public void onVerifySuccess(TokenData token) {
//                progress.setVisibility(View.GONE);
////                MyPreference.getInstance(getContext()).setRefreshToken(token.refresh_token)
////                Toast.makeText(loginActivity22, "success", Toast.LENGTH_SHORT).show();
//                loginActivity.startActivity(new Intent(getContext(), MainActivity.class));
//
//            }
//
//            @Override
//            public void onVerifyError(int errorCode, String message) {
//                Toast.makeText(loginActivity, message, Toast.LENGTH_SHORT).show();
//                progress.setVisibility(View.GONE);
//                submit_btn.setEnabled(true);
//                verifyCode.setEnabled(true);
//                progress.setVisibility(View.GONE);
//                submit_btn.setVisibility(View.VISIBLE);
//
//            }
//        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        timer.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }


}
