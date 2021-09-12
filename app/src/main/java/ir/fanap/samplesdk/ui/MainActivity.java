package ir.fanap.samplesdk.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import io.reactivex.disposables.CompositeDisposable;
import ir.fanap.samplesdk.R;
import ir.fanap.samplesdk.Toasty;
import ir.fanap.samplesdk.ui.buycredit.IssueCreditActivity;
import ir.fanap.samplesdk.ui.invoice.IssueInvoiceActivity;
import ir.fanap.samplesdk.ui.login.MyPreference;
import ir.fanap.samplesdk.ui.main.AccountBillAdapter;
import ir.fanap.sdk.FanapSDK;
import ir.fanap.sdk.settelment.SettlementCallback;
import ir.fanap.sdk.accountbill.AccountBillCallback;
import ir.fanap.sdk.accountbill.AccountBillData;
import ir.fanap.sdk.settelment.SettlementData;
import ir.fanap.sdk.credit.GetCreditCallback;
import ir.fanap.sdk.credit.GetCreditData;
import ir.fanap.sdk.handshake.HandShakeCallback;
import ir.fanap.sdk.handshake.HandshakeData;
import ir.fanap.sdk.profile.ProfileCallback;
import ir.fanap.sdk.profile.ProfileInfoData;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    public static final String EXPIRE_TIME = "expire_time";
    private String userId = "";
    private ProgressDialog progressDialog;
    private HandshakeData handShake;
    private MutableLiveData<Long> cdTime = new MutableLiveData<Long>();
    private CountDownTimer timer;
    private long expireTime = 0;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Button btnGetCredit, directBuyCredit, btnAccountBill, payInvoice;
    private TextView lblCredit;
    private RecyclerView rcvAccountBill;
    private AccountBillAdapter adapter;
    private TextView textAccountBill;
    private TextView countDownTimerMain;
    private FanapSDK sdk = FanapSDK.getInstance(FanapSDK.Server.PRODUCTION);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setOnclick();
        setOnclick();
        setupTimer();
    }


    private void setOnclick() {

        rcvAccountBill = findViewById(R.id.rcvAccountBill);
        countDownTimerMain = findViewById(R.id.countDownTimerMain);
        textAccountBill = findViewById(R.id.textAccountBill);
        lblCredit = findViewById(R.id.lblCredit);

        btnGetCredit = findViewById(R.id.btnGetCredit);
        btnGetCredit.setOnClickListener(this);

        btnAccountBill = findViewById(R.id.btnAccountBill);
        btnAccountBill.setOnClickListener(this);

        directBuyCredit = findViewById(R.id.directBuyCredit);
        directBuyCredit.setOnClickListener(this);

        payInvoice = findViewById(R.id.payInvoice);
        payInvoice.setOnClickListener(this);

        findViewById(R.id.btnGetKey).setOnClickListener(this);
        findViewById(R.id.refreshToken).setOnClickListener(this);
        findViewById(R.id.exit).setOnClickListener(this);
    }

    private void setupTimer() {
        expireTime = getIntent().getLongExtra(EXPIRE_TIME, 0);

        cdTime.postValue(expireTime);

        timer(expireTime);
    }

    private void setAccessToken(String token) {
        MyPreference.getInstance(this).setAccessToken(token);
    }

    // access token should be get from server or Pod OTP SDK
    private String getAccessToken() {
        return "11e1a171c7ea4ebfa1a87e207dca50b7";
    }

    private ProgressDialog progress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
        }
        return progressDialog;
    }

    private void getKey() {
        progress().setMessage(getString(R.string.GET_KEY));
        progress().show();
        compositeDisposable.add(sdk.getPrivateCode(getAccessToken(), FanapSDK.KEY_SIZE, FanapSDK.KEY_ALGORITHM,
                true, new HandShakeCallback() {
                    @Override
                    public void onSuccessHandshake(HandshakeData handshake) {
                        getProfileInfo();
                        handShake = handshake;
                        progress().dismiss();
                    }

                    @Override
                    public void onErrorHandshake(int errorCode, String message, HandshakeData data) {
                        Toasty.error(MainActivity.this, message, Toast.LENGTH_LONG, false).show();
                        progress().dismiss();
                        getProfileInfo();
                    }
                }));
    }

    private void getProfileInfo() {
        progress().show();
        compositeDisposable.add(sdk.getProfileInfo(getAccessToken(), FanapSDK.DEFALUT_TOKEN_ISSUER,
                new ProfileCallback() {
                    @Override
                    public void onSuccessProfile(ProfileInfoData info) {

                        userId = String.valueOf(info.getUserId());
                        progress().dismiss();
//                        btnGetCredit.setEnabled(true);
//                        btnAccountBill.setEnabled(true);
//                        directBuyCredit.setEnabled(true);
//                        payInvoice.setEnabled(true);
                    }

                    @Override
                    public void onErrorProfile(int errorCode, String message, ProfileInfoData data) {
                        Toasty.error(MainActivity.this, message, Toast.LENGTH_LONG, false).show();
                        progress().dismiss();
                    }
                }));
    }


    private void credit() {

        if (checkKey()) return;

        progress().setMessage(getString(R.string.GET_CREDIT));
        progress().show();
        compositeDisposable.add(sdk.getCreditWithSign(getAccessToken(), FanapSDK.DEFALUT_TOKEN_ISSUER,
                handShake.getPrivateKey(), userId, handShake.getKeyId(),
                FanapSDK.POD_WALLET, new GetCreditCallback() {
                    @Override
                    public void onSuccessGetCredit(GetCreditData credit) {
                        lblCredit.setText(String.format( credit.getResult().
                                getAmount(), " " + credit.getResult().getCurrencySrv().getName()));
                        progress().dismiss();
                    }

                    @Override
                    public void onErrorGetCredit(int errorCode, String message, GetCreditData credit) {
                        Toasty.error(MainActivity.this, message, Toast.LENGTH_LONG, false).show();
                        progress().dismiss();
                    }
                }
        ));
    }

    private AccountBillAdapter getAdapter() {
        if (adapter == null)
            adapter = new AccountBillAdapter(MainActivity.this);
        return adapter;
    }

    private void accountBill() {
        if (checkKey()) return;

        progress().setMessage(getString(R.string.GET_ACCOUNT_BILL));
        progress().show();

        compositeDisposable.add(sdk.getAccountBillWithSign(getAccessToken(), FanapSDK.DEFALUT_TOKEN_ISSUER,
                handShake.getPrivateKey(), 0, 10, userId, handShake.getKeyId()
                , new AccountBillCallback() {
                    @Override
                    public void onSuccessAccountBil(AccountBillData data) {
                        getAdapter().addList(data.getResult());
                        rcvAccountBill.setAdapter(getAdapter());
                        rcvAccountBill.setVisibility(View.VISIBLE);
                        progress().dismiss();
                    }


                    @Override
                    public void onErrorAccountBil(int code, String message, AccountBillData data) {
                        textAccountBill.setText(message);
                        progress().dismiss();

                    }
                }));
    }

    private void refreshToken() {
//        progress().show();
//        MyApplication.otp.refreshToken(MyApplication.otp.getRefreshToken(),
//                new RefreshTokenCallBack() {
//                    @Override
//                    public void onRefreshTokenSuccess(TokenData tokenData) {
//                        progress().dismiss();
////                timer(token.expires_in)
//                        setAccessToken(tokenData.getAccessToken());
//
//                    }
//
//                    @Override
//                    public void onRefreshTokenError(int i, String message) {
//                        Toasty.error(Main5Activity.this, message, Toast.LENGTH_LONG, false).show();
//                        progress().dismiss();
//                    }
//                });
    }

    private void timer(Long exp) {

        timer = new CountDownTimer(exp * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                cdTime.postValue(cdTime.getValue() - 1);
            }

            @Override
            public void onFinish() {
                countDownTimerMain.setVisibility(View.INVISIBLE);
            }
        }.start();

        cdTime.observe(
                MainActivity.this,
                new Observer<Long>() {
                    @Override
                    public void onChanged(Long it) {
                        countDownTimerMain.setText(new StringBuilder()
                                .append(it / 60).append(":").append(it % 60));
                    }
                });
    }

    private void settlementByToolWithSign() {
        progress().show();
        compositeDisposable
            .add(sdk.settlementByToolWithSign(getAccessToken(), FanapSDK.DEFALUT_TOKEN_ISSUER,
                handShake.getPrivateKey(),
                userId, FanapSDK.POD_WALLET,
                handShake.getKeyId(), "", "", "SETTLEMENT_TOOL_CARD",
                "6104337636904185",
                "10000",
                "", "IRR", "", new SettlementCallback() {
                    @Override
                    public void onSuccessSettlement(SettlementData data) {
                        data.isHasError();
                        progress().cancel();
                    }

                    @Override
                    public void onErrorSettlement(int error, String message) {
                        message.length();
                        progress().cancel();
                    }
                }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }


    private void byCredit() {
        if (checkKey()) return;
        Intent intent = new Intent(this, IssueCreditActivity.class);
        intent.putExtra(IssueCreditActivity.USER_ID, userId);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnGetKey:
                getKey();
                break;
            case R.id.btnGetCredit:
                credit();
                break;
            case R.id.btnAccountBill:
                accountBill();
                break;
            case R.id.refreshToken:
                refreshToken();
                break;
            case R.id.directBuyCredit:
                byCredit();
                break;
            case R.id.payInvoice:
                payInvoice();
                break;
            case R.id.exit:
                exit();
                break;

        }
    }


    private void exit() {
        MyPreference.getInstance(this).setAccessToken("");
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void payInvoice() {
        if (checkKey()) return;
        Intent intent = new Intent(this, IssueInvoiceActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    private boolean checkKey() {
        if (userId.length() == 0) {
            Toasty.error(this, "لطفا ابتدا روی دریافت  کلید کنید").show();
            return true;
        }
        return false;
    }
}
