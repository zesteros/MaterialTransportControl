package com.mx.vise.nfc;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Parcelable;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.mx.vise.nfc.mifareclassic.Common;


/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por aloza el miércoles 11 de abril del 2018
 * <p>
 * Clase para manejar el NFC y obtener el id.
 * <p>
 * Consideraciones:
 * <p>
 * 1.- Añadir los permisos y filtros en intent correspondientes en Manifest (hacer unica instancia)
 * 2.- Reescribir el newIntent de la actividad y poner el método resolveIntent dentro.
 * 3.- Poner los métodos correspondientes en onPause y onResume (doOnPause y doOnResume).
 * 4.- Poner el método resolveIntent en el método onCreate.
 * 5.- Agregar el xml correspondiente a las tecnologías NFC (res/xml/technologies.xml)
 *
 * @author Angelo de Jesus Loza Martinez
 * @version CombustibleVISEandroidv1.0
 */

public class NFCHelper {

    private static final String TAG = "VISE";
    private final PendingIntent mPendingIntent;
    private Context mContext;
    private NFCIdListener mIdListener;
    private NfcAdapter mAdapter;

    /**
     * @param context    el contexto de la app
     * @param idListener el listener para cuando haya detectado un id
     */
    public NFCHelper(Context context, NFCIdListener idListener) {
        this.mContext = context;
        this.mIdListener = idListener;
        mAdapter = NfcAdapter.getDefaultAdapter(context);
        mPendingIntent = PendingIntent.getActivity(context, 0,
                new Intent(context,
                        context.getClass()
                )
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                0);
    }

    public NFCHelper(Context context) {
        this.mContext = context;
        mAdapter = NfcAdapter.getDefaultAdapter(context);
        mPendingIntent = PendingIntent.getActivity(context, 0,
                new Intent(context,
                        context.getClass()
                )
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                0);
    }


    /**
     * @param idListener the listener when id is detected
     */
    public void setIdListener(NFCIdListener idListener) {
        this.mIdListener = idListener;
    }

    /**
     * @param intent the intent of app
     */
    public void resolveIntent(Intent intent) {
        String action = intent.getAction();
        if ("android.nfc.action.TAG_DISCOVERED".equals(action) || "android.nfc.action.TECH_DISCOVERED".equals(action) || "android.nfc.action.NDEF_DISCOVERED".equals(action)) {

            TagRead tagRead = Common.treatAsNewTag(intent, mContext);

            NdefMessage[] msgs = null;
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra("android.nfc.extra.NDEF_MESSAGES");
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++)
                    msgs[i] = (NdefMessage) rawMsgs[i];
            }

            if (mIdListener != null)
                mIdListener.onNFCIdRead(msgs, getIdHex(intent), getIdDec(intent), tagRead.getCompatibilityStatus());

        }
    }


    /**
     * do when app is in resume state
     */
    public void doOnResume() {
        if (this.mAdapter == null) {
            if (!this.mAdapter.isEnabled()) {
                Log.d("VISE", "El nfc está desactivado.");
            }
        } else if (!this.mAdapter.isEnabled()) {
            Log.d("VISE", "El nfc está desactivado.");
        } else if (this.mAdapter != null) {
            this.mAdapter.enableForegroundDispatch((AppCompatActivity) mContext, this.mPendingIntent, null, null);
        }
    }


    /**
     * do when is paused
     */
    public void doOnPause() {
        if (this.mAdapter != null) {
            this.mAdapter.disableForegroundDispatch((AppCompatActivity) mContext);
            this.mAdapter.disableForegroundNdefPush((AppCompatActivity) mContext);
        }
    }


    /**
     * @param intent the intent of app
     * @return the hex of id
     */
    public String getIdHex(Intent intent) {
        Tag tag = intent.getParcelableExtra("android.nfc.extra.TAG");
        return tag != null ? getReverseHex(getHex(tag.getId())) : null;
    }

    /**
     * @param intent the intent of app
     * @return the decimal id
     */
    public long getIdDec(Intent intent) {
        Tag tag = intent.getParcelableExtra("android.nfc.extra.TAG");
        return tag != null ? getDec(tag.getId()) : 0;
    }


    /**
     * @param bytes
     * @return
     */
    public String getHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; i--) {
            int b = bytes[i] & 0xff;
            if (b < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }

        return sb.toString();
    }

    /**
     * @param hex the hex original
     * @return the hex inverted
     */
    public String getReverseHex(String hex) {
        String[] newhex = hex.split(" ");
        for (int i = 0; i < newhex.length / 2; i++) {
            String temp = newhex[i];
            newhex[i] = newhex[newhex.length - i - 1];
            newhex[newhex.length - i - 1] = temp;
        }
        String reverseHex = "";

        for (int i = 0; i < newhex.length; i++) {
            reverseHex += newhex[i];
            if (i < newhex.length)
                reverseHex += " ";
        }
        return reverseHex;
    }

    /**
     * @param bytes
     * @return
     */
    public long getDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (byte b : bytes) {
            result += (((long) b) & 255) * factor;
            factor *= 256;
        }
        return result;
    }


    /**
     * @param bytes
     * @return
     */
    private long getReversed(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length - 1; i >= 0; --i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

}
