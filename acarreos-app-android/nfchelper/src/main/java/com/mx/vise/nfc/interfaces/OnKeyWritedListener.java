package com.mx.vise.nfc.interfaces;

public interface OnKeyWritedListener {
    void onKeyWriteSuccess();
    void onKeyWriteFailed(WriteKeyStatus status);
}
