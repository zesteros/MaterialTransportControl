package com.mx.vise.acarreos.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.Result;
import com.mx.vise.acarreos.tasks.BarcodeDetected;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.mx.vise.acarreos.fragments.CancelTicketFragment.BARCODE_LISTENER_EXTRA;

public class SimpleScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private static final String TAG = "VISE";
    public static final String BARCODE_EXTRA = "barcode_extra";
    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    /**
     * @param rawResult llama para aca cuando se ha leido un codigo de barras
     */
    @Override
    public void handleResult(Result rawResult) {
        mScannerView.resumeCameraPreview(this);

        Intent returnIntent = new Intent();
        /*
        * Regresa al mainactivity con el resultado
        * */
        returnIntent.putExtra(BARCODE_EXTRA, rawResult.getText());

        setResult(Activity.RESULT_OK, returnIntent);

        finish();
    }
}