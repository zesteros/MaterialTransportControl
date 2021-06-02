package com.mx.vise.nfc;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class NFCAppCompatActivity extends AppCompatActivity implements NFCIdListener {

    protected NFCHelper mNfcHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        mNfcHelper = new NFCHelper(this, this);
        mNfcHelper.resolveIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNfcHelper.doOnResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNfcHelper.doOnPause();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        mNfcHelper.resolveIntent(intent);
    }

    @Override
    public void onNFCIdRead(NdefMessage[] msgs, String hexId, long decId, MifareClassicCompatibilityStatus nfcStatus) {

    }
}
