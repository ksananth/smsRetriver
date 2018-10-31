package com.example.ananth.smsretrieverapidemo.java;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

public class MySMSBroadcastReceiver extends BroadcastReceiver {
    private OTPReceiveListener otpReceiver;

    void initOTPListener(OTPReceiveListener receiver) {
        this.otpReceiver = receiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

            switch(status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    String otp = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    if (otpReceiver != null) {
                        //otp = otp.replace("<#> Your ExampleApp code is: ", "").split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                        otpReceiver.onOTPReceived(otp);
                    }
                    break;
                case CommonStatusCodes.TIMEOUT:
                    otpReceiver.onOTPTimeOut();
                    break;
            }
        }
    }
}

interface OTPReceiveListener {

    String onOTPReceived(String otp);

    void onOTPTimeOut();
}
