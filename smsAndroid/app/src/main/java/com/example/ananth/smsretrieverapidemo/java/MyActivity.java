package com.example.ananth.smsretrieverapidemo.java;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ananth.smsretrieverapidemo.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class MyActivity extends FragmentActivity implements OTPReceiveListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private int RESOLVE_HINT = 2;
    GoogleApiClient mCredentialsApiClient  = null;
    MySMSBroadcastReceiver smsBroadcast = new MySMSBroadcastReceiver();

    Button getNumber;
    TextView textView;
    String mobNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getNumber = (Button) findViewById(R.id.btn_get);
        textView = (TextView) findViewById(R.id.phone);

        getNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPhoneNumber();
            }
        });


        startSMSListener();

        smsBroadcast.initOTPListener(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);


        this.registerReceiver(smsBroadcast, intentFilter);
    }


        protected void requestPhoneNumber(){
            GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Auth.CREDENTIALS_API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            googleApiClient.connect();
            HintRequest hintRequest = new HintRequest.Builder()
                    .setPhoneNumberIdentifierSupported(true)
                    .build();
            PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(googleApiClient, hintRequest);
            try {
                startIntentSenderForResult(intent.getIntentSender(), RESOLVE_HINT, null, 0, 0, 0, null);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }




    private void startSMSListener() {
    }

    // Construct a request for phone numbers and show the picker
    private void requestHint() throws IntentSender.SendIntentException {
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();

        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(
                mCredentialsApiClient, hintRequest);
        startIntentSenderForResult(intent.getIntentSender(),
                RESOLVE_HINT, null, 0, 0, 0);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                if (credential != null) {
                    mobNumber = credential.getId();
                    textView.setText(mobNumber);

                } else {
                    textView.setText("No phone number available");
                }
            }else {
                textView.setText("No phone number available");
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public String onOTPReceived(String otp) {
        if (smsBroadcast != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(smsBroadcast);
        }
        Toast.makeText(this, otp, Toast.LENGTH_SHORT).show();
        //otpTxtView.text = "Your OTP is: $otp"
        Log.e("OTP Received", otp);
        return otp;
    }

    @Override
    public void onOTPTimeOut() {
        Toast.makeText(this, " SMS retriever API Timeout", Toast.LENGTH_SHORT).show();
    }

}
