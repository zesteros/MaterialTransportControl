package com.mx.vise.nfc.interfaces;

public interface OnVirginTagDetectedListener extends OnTagReadListenerBase {
    void onVirginTagDetected(String middleData);
}
