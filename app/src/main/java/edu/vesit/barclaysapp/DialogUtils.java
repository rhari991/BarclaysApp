package edu.vesit.barclaysapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import com.paytm.pgsdk.PaytmMerchant;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

public class DialogUtils {

    public static void showEnterParameterDialog(final Context context, final String parameter) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AppTheme));
        if(parameter.equals("account_no"))
            builder.setTitle("Enter the receiver's account number : ");
        else if(parameter.contains("Paytm"))
            builder.setTitle("Enter the amount to be transferred : ");
        else if(parameter.equals("amount"))
            builder.setTitle("Enter the transaction amount : ");
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_accountno, null);
        builder.setView(dialogView);
        if(parameter.contains("Paytm"))
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String enteredParameter = ((EditText) dialogView.findViewById(R.id.etAccountNo)).getText().toString();
                    Log.e("Sending ", enteredParameter);
                    DialogUtils.startPaytmTransaction(context, enteredParameter);
                }
            });
        else
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String enteredParameter = ((EditText) dialogView.findViewById(R.id.etAccountNo)).getText().toString();
                    Log.e("Sending ", enteredParameter);
                    PythonServerUtils.sendParameterToServer(context, parameter, enteredParameter);
                }
            });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    public static void openTwitterLoginDialog(final Context context, String oAuthUrl) {
        final Dialog mDialog = new Dialog(context);
        mDialog.setContentView(R.layout.dialog_twitter_login);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        lp.copyFrom(mDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        mDialog.getWindow().setAttributes(lp);
        WebView webView = (WebView) mDialog.findViewById(R.id.wvBrowser);
        WebViewClient wvClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("auth url ", url + "");
                TweetEvent tweetEvent = new TweetEvent();
                Uri uri = Uri.parse(url);
                tweetEvent.setOauthVerifier(uri.getQueryParameter("oauth_verifier"));
                EventBus.getDefault().post(tweetEvent);
                mDialog.dismiss();
                return true;
            }
        };
        webView.setWebViewClient(wvClient);
        webView.loadUrl(oAuthUrl);
        mDialog.show();
    }

    public static void startPaytmTransaction(final Context context, String amount) {
        Log.e("starting paytm ", "yes");
        PaytmPGService Service = PaytmPGService.getStagingService();
        Map<String, String> paramMap = new HashMap<String, String>();

        // these are mandatory parameters

        paramMap.put("ORDER_ID", "12345");
        paramMap.put("MID", "WorldP64425807474247");
        paramMap.put("CUST_ID", "CUST23657");
        paramMap.put("CHANNEL_ID", "WAP");
        paramMap.put("INDUSTRY_TYPE_ID", "Retail");
        paramMap.put("WEBSITE", "worldpressplg");
        paramMap.put("TXN_AMOUNT", amount);
        paramMap.put("THEME", "merchant");
        paramMap.put("EMAIL", "abc@gmail.com");
        paramMap.put("MOBILE_NO", "123");
        PaytmOrder Order = new PaytmOrder(paramMap);

        PaytmMerchant Merchant = new PaytmMerchant(
                "https://pguat.paytm.com/paytmchecksum/paytmCheckSumGenerator.jsp",
                "https://pguat.paytm.com/paytmchecksum/paytmCheckSumVerify.jsp");

        Service.initialize(Order, Merchant, null);

        Service.startPaymentTransaction(context, true, true,
                new PaytmPaymentTransactionCallback() {
                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        // Some UI Error Occurred in Payment Gateway Activity.
                        // // This may be due to initialization of views in
                        // Payment Gateway Activity or may be due to //
                        // initialization of webview. // Error Message details
                        // the error occurred.
                    }

                    @Override
                    public void onTransactionSuccess(Bundle inResponse) {
                        // After successful transaction this method gets called.
                        // // Response bundle contains the merchant response
                        // parameters.
                        Log.e("LOG", "Payment Transaction is successful " + inResponse);
                        Toast.makeText(context, "Payment Transaction is successful ", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onTransactionFailure(String inErrorMessage,
                                                     Bundle inResponse) {
                        // This method gets called if transaction failed. //
                        // Here in this case transaction is completed, but with
                        // a failure. // Error Message describes the reason for
                        // failure. // Response bundle contains the merchant
                        // response parameters.
                        Log.e("LOG", "Payment Transaction Failed " + inErrorMessage);
                        Toast.makeText(context, "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void networkNotAvailable() { // If network is not
                        // available, then this
                        // method gets called.
                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                        // This method gets called if client authentication
                        // failed. // Failure may be due to following reasons //
                        // 1. Server error or downtime. // 2. Server unable to
                        // generate checksum or checksum response is not in
                        // proper format. // 3. Server failed to authenticate
                        // that client. That is value of payt_STATUS is 2. //
                        // Error Message describes the reason for failure.
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode,
                                                      String inErrorMessage, String inFailingUrl) {

                    }

                    // had to be added: NOTE
                    @Override
                    public void onBackPressedCancelTransaction() {
                        // TODO Auto-generated method stub
                    }

                });
    }
}
