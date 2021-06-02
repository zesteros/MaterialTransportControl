package com.mx.vise.nfc.interfaces;

import com.mx.vise.nfc.mifareclassic.Sector;

import java.util.ArrayList;

public interface OnTagReadListenerBase {
    void onTagReadSuccess(String data, ArrayList<Sector> sectors, Sector flagsSector);

    enum MiFareClassicReadStatus {
        NONE_KEY_VALID_FOR_READING,
        TAG_REMOVED_WHILE_READING,
        USER_DATA_IS_EMPTY, USER_DATA_CORRUPTED,
        TAG_WITH_DEFAULT_KEYS, UKNOWN_KEYS, TAG_REMOVED_OR_ANY_KEY_INVALID
    }
    void onTagReadFailed(MiFareClassicReadStatus status, ArrayList<Sector> sectors);

    public enum MiFareClassicWriteStatus {ERROR_WRITING_SECTOR, NOT_BLOCK_IN_SECTOR}
}
