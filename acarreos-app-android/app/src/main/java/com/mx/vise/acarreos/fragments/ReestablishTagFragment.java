package com.mx.vise.acarreos.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.nfc.NdefMessage;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.mx.vise.acarreos.R;
import com.mx.vise.acarreos.activities.MainActivity;

import com.mx.vise.acarreos.dao.DAOKeys;
import com.mx.vise.androiduihelper.UIHelper;
import com.mx.vise.nfc.NFCIdListener;
import com.mx.vise.nfc.MifareClassicCompatibilityStatus;
import com.mx.vise.nfc.interfaces.OnTagReadListenerBase;
import com.mx.vise.nfc.mifareclassic.MifareClassicReadWrite;
import com.mx.vise.nfc.mifareclassic.Sector;

import java.util.ArrayList;

import static com.mx.vise.acarreos.activities.MainActivity.KEYS_VERSION;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por Angelo el jueves 04 de abril del 2019 a las 18:04
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreos-app-android
 */
public class ReestablishTagFragment extends Fragment implements View.OnClickListener, NFCIdListener {

    private static final String TAG = "VISE";
    private Button mReestablishTagButton;
    private ProgressDialog mReadingTagProgressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reestablish_tag, container, false);

        mReestablishTagButton = rootView.findViewById(R.id.reestablishTagButton);

        mReestablishTagButton.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reestablishTagButton:
                showConfirmDialog();
                break;
        }
    }

    /**
     * Muestra dialogo de confirmación
     */
    private void showConfirmDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog
                .setMessage(R.string.sure_to_erase_data_in_tag_message)
                .setTitle(R.string.confirm_reestablish_tag)
                .setPositiveButton(R.string.accept,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                eraseDataInTag();
                            }
                        })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    /**
     * Elimina los datos del tag
     */

    private void eraseDataInTag() {

        mReadingTagProgressDialog = ProgressDialog
                .show(
                        getActivity(),
                        getString(R.string.reading_tag),
                        getString(R.string.wait_please),
                        true,
                        true);

        mReadingTagProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onCancel(DialogInterface dialog) {
                mReadingTagProgressDialog.dismiss();

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onNFCIdRead(NdefMessage[] msgs, String hexId, long decId, MifareClassicCompatibilityStatus status) {
        if (mReadingTagProgressDialog != null) {
            if (mReadingTagProgressDialog.isShowing()) {
                if (status == MifareClassicCompatibilityStatus.MIFARE_CLASSIC_FULL_COMPATIBLE) {

                    MifareClassicReadWrite.readTag(getActivity(), new OnTagReadListenerBase() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onTagReadSuccess(String data, ArrayList<Sector> sectors, Sector flagsSector) {
                            /*
                             * DELETE HERE
                             *
                             * */
                        }

                        @Override
                        public void onTagReadFailed(MiFareClassicReadStatus status, ArrayList<Sector> sectors) {
                            Toast.makeText(getActivity(), "Error al leer el tag." + String.valueOf(status), Toast.LENGTH_SHORT).show();

                            Log.i(TAG, "onTagReadFailed: " + String.valueOf(status));
                            mReadingTagProgressDialog.dismiss();
                        }
                    },
                            DAOKeys.getKeysByVersion(getActivity(), KEYS_VERSION),
                            false);

                } else {

                    switch (status) {
                        case NO_COMPATIBLE_TAG:
                            UIHelper.showSnackbarWithAction(getActivity(), "El tag leído no es compatible.");
                            break;
                        case NO_COMPATIBLE_HARDWARE:
                            UIHelper.showSnackbarWithAction(getActivity(), "Tu dispositivo no es compatible con el tag.");
                            break;
                    }
                    mReadingTagProgressDialog.dismiss();

                }

            }
        }
    }
}
