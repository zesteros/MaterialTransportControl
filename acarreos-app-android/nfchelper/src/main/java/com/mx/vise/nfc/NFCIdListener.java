package com.mx.vise.nfc;

import android.nfc.NdefMessage;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por aloza el miércoles 11 de abril del 2018
 *
 * @author Angelo de Jesus Loza Martinez
 * @version CombustibleVISEandroidv1.0
 */

public interface NFCIdListener {
    /**
     * @param msgs los mensajes del nfc
     * @param hexId el id hexadecimal
     * @param decId el id decimal
     * @param nfcStatus
     */
    void onNFCIdRead(NdefMessage[] msgs, String hexId, long decId, MifareClassicCompatibilityStatus nfcStatus);

}
