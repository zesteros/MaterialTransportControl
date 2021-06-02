package com.mx.vise.nfc;

import android.nfc.Tag;

public class TagRead {
    private Tag tag;
    private MifareClassicCompatibilityStatus compatibilityStatus;

    public TagRead(Tag tag, MifareClassicCompatibilityStatus compatibilityStatus) {
        this.tag = tag;
        this.compatibilityStatus = compatibilityStatus;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public MifareClassicCompatibilityStatus getCompatibilityStatus() {
        return compatibilityStatus;
    }

    public void setCompatibilityStatus(MifareClassicCompatibilityStatus compatibilityStatus) {
        this.compatibilityStatus = compatibilityStatus;
    }
}
