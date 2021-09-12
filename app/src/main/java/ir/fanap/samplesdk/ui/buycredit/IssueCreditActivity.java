package ir.fanap.samplesdk.ui.buycredit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import io.reactivex.disposables.CompositeDisposable;
import ir.fanap.samplesdk.R;
import ir.fanap.samplesdk.Toasty;
import ir.fanap.sdk.FanapSDK;
import ir.fanap.sdk.buy.BuyCreditCallback;
import ir.fanap.sdk.buy.BuyCreditData;
import ir.fanap.sdk.buy.VerifyCreditCallback;
import ir.fanap.sdk.buy.VerifyCreditInvoiceData;

public class IssueCreditActivity extends AppCompatActivity implements View.OnClickListener {

  public static String USER_ID = "userId";
  public static String seprator = "?";

  private Boolean callVerify = false;
  private String billNumber = "";

  //TODO(Warning)//Your ‌business token should be on your server , this is example
  String bToken = "6b37595c6a7a44b0a90fcddsf68b40814e7";
  String redirectUrl = "http://192.168.6.58:8000/callback?";


  private CompositeDisposable compositeDisposable = new CompositeDisposable();

  private String userId = "";
  private ProgressDialog progressDialog;
  private EditText price;
  private FanapSDK sdk = FanapSDK.getInstance(FanapSDK.Server.PRODUCTION);


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_but_credit);

    Intent intent = getIntent();

    if (intent != null) {
      if (intent.getData() != null && getIntent().getData().toString()
          .startsWith("wallet://buyCredit")) {

        getPaymentData();

      } else {
        userId = intent.getStringExtra(USER_ID) == null ? ""
            : getIntent().getStringExtra(USER_ID);
      }
    }

    setOnclick();
    initialize();
  }

  private void initialize() {
    price = findViewById(R.id.price);
  }


  private void setOnclick() {
    findViewById(R.id.directBuyCredit).setOnClickListener(this);
    findViewById(R.id.indirectBuyCredit).setOnClickListener(this);
  }

  private void getPaymentData() {
    String splitData = getIntent().getData().toString().split(seprator)[1];
    String[] data = splitData.split("&");
    String tref = data[0].split("=")[1];
    String payed = data[1].split("=")[1];
    String waiting = data[2].split("=")[1];
    String billNumber = data[3].split("=")[1];
  }


  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.directBuyCredit:
        buyCredit();
        break;

      case R.id.indirectBuyCredit:
        buyCreditWithPanel();
        break;

    }
  }


  private void openBrowser(String link) {
    CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
    customTabsIntent.launchUrl(this, Uri.parse(link));
  }

  private ProgressDialog progress() {
    if (progressDialog == null) {
      progressDialog = new ProgressDialog(this);
      progressDialog.setCancelable(false);
    }
    return progressDialog;
  }


  private void buyCredit() {
    progress().show();

    compositeDisposable.add(sdk
            .issueCreditInvoice(bToken, FanapSDK.DEFALUT_TOKEN_ISSUER, price.getText().toString(),
                    userId, redirectUrl, "",
                    FanapSDK.WALLET,
                    FanapSDK.GATE_WAY_PEP, new BuyCreditCallback() {
                      @Override
                      public void onSuccessCredit(BuyCreditData data, String billNumber,
                                                  String creditUrl) {
                        billNumber = billNumber;
                        openBrowser(creditUrl);
                        callVerify = true;
                        progress().dismiss();

                      }

                      @Override
                      public void onErrorCredit(int code, String message, String billId, BuyCreditData data) {
                        Toasty.error(IssueCreditActivity.this, message).show();
                        progress().dismiss();
                      }
            }));
  }

  private void buyCreditWithPanel() {
    String link = sdk.issueCreditUrl(redirectUrl, price.getText().toString(), "");
    openBrowser(link);
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (callVerify) {
      callVerifyCredit();
      callVerify = false;
    }
  }


  private void callVerifyCredit() {
    progress().show();
    compositeDisposable
            .add(sdk.verifyCreditInvoice(bToken, FanapSDK.DEFALUT_TOKEN_ISSUER, billNumber,

                    new VerifyCreditCallback() {
                      @Override
                      public void onSuccessVerify(VerifyCreditInvoiceData data) {
                        Toasty.success(IssueCreditActivity.this, "کیف پول با موفقیت شارژ شد")
                                .show();
                        progress().dismiss();
                      }

                      @Override
                      public void onErrorVerify(int errorCode, String message, VerifyCreditInvoiceData data) {
                        Toasty.error(IssueCreditActivity.this, message).show();
                        progress().dismiss();
                      }
            }));
  }


  @Override
  protected void onDestroy() {
    super.onDestroy();
    compositeDisposable.clear();
  }
}




