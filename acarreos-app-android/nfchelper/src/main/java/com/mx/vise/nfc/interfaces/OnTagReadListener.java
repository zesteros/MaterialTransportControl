package com.mx.vise.nfc.interfaces;

import com.mx.vise.nfc.mifareclassic.Sector;

import java.util.ArrayList;

public interface OnTagReadListener extends OnTagReadListenerBase {
    void onTagReadSuccess(ArrayList<Sector> sectors);
}
