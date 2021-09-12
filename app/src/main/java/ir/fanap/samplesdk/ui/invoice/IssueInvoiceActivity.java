package ir.fanap.samplesdk.ui.invoice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import ir.fanap.samplesdk.Toasty;
import ir.fanap.wallet_dependencies.Dependency;
import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;
import ir.fanap.samplesdk.R;
import ir.fanap.sdk.FanapSDK;
import ir.fanap.sdk.invoice.IssueInvoiceCallback;
import ir.fanap.sdk.invoice.IssueInvoiceData;
import ir.fanap.sdk.ott.OttCallback;
import ir.fanap.sdk.ott.OttData;

public class IssueInvoiceActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String USER_ID = "userId";
    private Boolean callVerify = false;
    private String billNumber = "";
    String bToken = "6b37595c6a7a44b0a90fcd68b40814e7"; //TODO(Warning)//Your ‌business token should be on your server , this is example
    String redirectUrl = "http://192.168.1.101:8000/callback?";


    private String userId = "";
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    Dependency add=new Dependency();
    private ProgressDialog progressDialog;
    private EditText price;
    private EditText desc;
    private EditText count;
    private FanapSDK sdk=FanapSDK.getInstance(FanapSDK.Server.PRODUCTION);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getData() != null && intent.getData().toString().startsWith("wallet")) {
                getPaymentData();
            } else {
                userId =intent.getStringExtra(USER_ID) == null ? "" : getIntent().getStringExtra(USER_ID);
            }

            initialize();
        }

    }

    private void initialize() {
        findViewById(R.id.indirectBuyCredit).setOnClickListener(this);
        price = findViewById(R.id.price);
        desc = findViewById(R.id.desc);
        count = findViewById(R.id.count);
    }

    private ProgressDialog progress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
        }
        return progressDialog;
    }

    private void getPaymentData() {
        String splitData = getIntent().getData().toString().split("'?'")[1];
        String[] data = splitData.split("&");
        String paymentBillNumber = data[0].split("=")[1];
        String invoiceId = data[1].split("=")[1];
        String paid = data[2].split("=")[1];
//        val lastFourDigitOfCardNumber = data[3].split("=")[1]
//        val maskedCardNumber = data[4].split("=")[1]

        if (paid.equals("true")) {
            Toasty.success(this, "پرداخت با موفقیت انجام شد").show();
        } else {
            Toasty.error(this, "پرداخت با مشکل مواجه شد").show();

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.indirectBuyCredit:
                payInvoice();
                break;
        }
    }


    private void payInvoice() {
        progress().show();
        compositeDisposable.add(sdk.getOtt(bToken, FanapSDK.DEFALUT_TOKEN_ISSUER, new OttCallback() {
            @Override
            public void onSuccessOtt(OttData data) {
                Toasty.success(IssueInvoiceActivity.this, "get OTT success").show();
                progress().dismiss();
                issueInvoice(bToken, data.getOtt());
            }

            @Override
            public void onErrorOtt(int errorCode, String message,OttData data) {
                Toasty.error(IssueInvoiceActivity.this, message).show();
                progress().dismiss();
            }
        }));
    }

    private void issueInvoice(String bToken, String ott) {
        progress().show();
        ArrayList<String> productIds = new ArrayList<String>();
        productIds.add("0");
        ArrayList<String> prices = new ArrayList<String>();
        prices.add(price.getText().toString());
        ArrayList<String> descs = new ArrayList<String>();
        descs.add(desc.getText().toString());
        ArrayList<String> counts = new ArrayList<String>();
        counts.add(count.getText().toString());

        compositeDisposable.add(sdk.issueInvoice(bToken, FanapSDK.DEFALUT_TOKEN_ISSUER, ott, userId,
                productIds,
                prices,
                descs,
                counts,
                "API_GUILD", null,
                new IssueInvoiceCallback() {
                    @Override
                    public void onSuccessIssueInvoice(IssueInvoiceData data) {
                        Toasty.success(IssueInvoiceActivity.this, "create invoice success").show();
                        String link = sdk.payInvoiceByUniqueNumber(data.getResult().getUniqueNumber(), redirectUrl,
                                "", FanapSDK.GATE_WAY_PEP);
                        openBrowser(link);
                        progress().dismiss();
                    }

                    @Override
                    public void onErrorIssueInvoice(int error, String message,IssueInvoiceData data) {
                        Toasty.error(IssueInvoiceActivity.this, message).show();
                        progress().dismiss();
                    }
                }));

    }

    private void openBrowser(String link) {
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
        customTabsIntent.launchUrl(this, Uri.parse(link));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

}
