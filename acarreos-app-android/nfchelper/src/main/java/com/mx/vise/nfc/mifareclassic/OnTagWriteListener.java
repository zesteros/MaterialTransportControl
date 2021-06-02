package com.mx.vise.nfc.mifareclassic;

import com.mx.vise.nfc.interfaces.OnTagReadListenerBase;

public interface OnTagWriteListener {
    void onTagWriteSuccess();
    void onTagWriteFailed(OnTagReadListenerBase.MiFareClassicWriteStatus notBlockInSector);
}
