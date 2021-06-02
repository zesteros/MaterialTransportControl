package com.mx.vise.acarreos.tasks;

import java.io.Serializable;

/**
 * Interfaz para accionar la deteccion del codigo de barras
 */
public interface OnBarcodeDetectedListener extends Serializable {
    void onBarcodeDetected(String barcode);
}
