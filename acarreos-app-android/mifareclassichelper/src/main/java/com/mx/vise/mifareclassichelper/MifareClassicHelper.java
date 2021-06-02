package com.mx.vise.mifareclassichelper;

import android.content.Intent;
import android.util.SparseArray;

public class MifareClassicHelper {

//    /**
//     * Triggered by {@link #onActivityResult(int, int, Intent)}
//     * this method starts a worker thread that first reads the tag and then
//     * calls {@link #createTagDump(SparseArray)}.
//     */
//    private void readTag() {
//        final MCReader reader = Common.checkForTagAndCreateReader(this);
//        if (reader == null) {
//            return;
//        }
//        new Thread(() -> {
//            // Get key map from glob. variable.
//            mRawDump = reader.readAsMuchAsPossible(
//                    Common.getKeyMap());
//
//            reader.close();
//
//            mHandler.post(() -> createTagDump(mRawDump));
//        }).start();
//    }

}
