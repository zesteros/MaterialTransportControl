package com.mx.vise.acarreos.tasks;

import java.io.Serializable;

/**
 * Clase para transmitir la deteccion del codigo de barras
 */
public class BarcodeDetected implements Serializable {
    private OnBarcodeDetectedListener onBarcodeDetectedListener;
    private String barcode;

    public OnBarcodeDetectedListener getOnBarcodeDetectedListener() {
        return onBarcodeDetectedListener;
    }

    public void setOnBarcodeDetectedListener(OnBarcodeDetectedListener onBarcodeDetectedListener) {
        this.onBarcodeDetectedListener = onBarcodeDetectedListener;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getBarcode(){
        return barcode;
    }
}
