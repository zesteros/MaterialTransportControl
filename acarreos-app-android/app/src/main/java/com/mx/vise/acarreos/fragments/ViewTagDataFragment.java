package com.mx.vise.acarreos.fragments;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.nfc.NdefMessage;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.mx.vise.acarreos.R;
import com.mx.vise.acarreos.activities.MainActivity;
import com.mx.vise.acarreos.adapters.RecyclerViewModel;
import com.mx.vise.acarreos.adapters.RecyclerViewAdapter;
import com.mx.vise.acarreos.dao.DAOKeys;
import com.mx.vise.acarreos.dao.DAOMaterials;
import com.mx.vise.acarreos.dao.DAOPoints;
import com.mx.vise.acarreos.pojos.MaterialsPOJO;
import com.mx.vise.acarreos.pojos.PointPOJO;
import com.mx.vise.androiduihelper.UIHelper;
import com.mx.vise.nfc.NFCIdListener;
import com.mx.vise.nfc.MifareClassicCompatibilityStatus;
import com.mx.vise.nfc.interfaces.OnTagReadListenerBase;
import com.mx.vise.nfc.interfaces.OnVirginTagDetectedListener;
import com.mx.vise.nfc.mifareclassic.MifareClassicReadWrite;
import com.mx.vise.nfc.mifareclassic.Sector;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.VIBRATOR_SERVICE;
import static com.mx.vise.acarreos.activities.MainActivity.KEYS_VERSION;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewTagDataFragment extends Fragment implements View.OnClickListener, NFCIdListener {


    private LinearLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    public String TAG = "VISE";
    private Button mReadButton;
    private ArrayList<RecyclerViewModel> mData;
    private ProgressDialog mReadingTagProgressDialog;
    private boolean mReadTagCompatible;

    public ViewTagDataFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_tag_data, container, false);
        ((MainActivity) getActivity()).setNfcIdListener(ViewTagDataFragment.this);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.data_recycler_view);
        mReadButton = rootView.findViewById(R.id.readTagViewTagButton);

        mReadButton.setOnClickListener(this);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mData = new ArrayList<RecyclerViewModel>();
        mAdapter = new RecyclerViewAdapter(mData);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.readTagViewTagButton:
                mReadingTagProgressDialog = ProgressDialog
                        .show(
                                getActivity(),
                                getString(R.string.reading_tag),
                                getString(R.string.wait_please),
                                true,
                                true);

                mReadingTagProgressDialog.setOnCancelListener(dialog -> mReadingTagProgressDialog.dismiss());
                if (mReadTagCompatible)
                    readMifareClassicTag();


//                uhfHelper.readTag(new OnTagReadListener() {
//                    @RequiresApi(api = Build.VERSION_CODES.M)
//                    @Override
//                    public void onTagRead(Tag tag) {
//                        if (tag.getUserData() != null) {
//                            debugTagData(data, tag, sectors);
//                        } else {
//                            mData.clear();
//                            mAdapter.notifyDataSetChanged();
//                            switch (tag.getTagReadStatus()) {
//                                case CANNOT_DECRYPT:
//                                    UIHelper.showSnackbarWithAction(getActivity(), getString(R.string.cannot_obtain_tag_data), true);
//                                    break;
//                                case USER_DATA_IS_EMPTY:
//                                    UIHelper.showSnackbarWithAction(getActivity(), getString(R.string.tag_contains_info), true);
//                                    break;
//                                case COMMUNICATION_ERROR:
//                                    UIHelper.showSnackbarWithAction(getActivity(), getString(R.string.communication_error), true);
//                                    break;
//                            }
//                        }
//                        progressDialog.dismiss();
//
//                    }
//
//                    @Override
//                    public void onTagReadTimeout() {
//
//                    }
//
//                    @Override
//                    public void onTagReadFailed() {
//                        mData.clear();
//                        progressDialog.dismiss();
//                        mAdapter.notifyDataSetChanged();
//                    }
//                }, 20000);
                break;
        }
    }

    /**
     * @param data
     * @param sectors
     */
    private void debugTagData(String data, ArrayList<Sector> sectors) {
        final String[] dataInTag = data.split("\\|");

        if (dataInTag.length == 4) {
            mData.clear();
            addBasicData(dataInTag, getString(R.string.with_cubage_data));
            mAdapter.notifyDataSetChanged();

        } else if (dataInTag.length >= 12) {
            mData.clear();

            addBasicData(dataInTag, getString(R.string.with_royalty_data));
            addCompleteData(dataInTag);

            mAdapter.notifyDataSetChanged();

        }
    }

    /**
     * @param dataInTag the split data
     * @param tagType   the text to show in tag type
     */
    public void addBasicData(String[] dataInTag, String tagType) {

        mData.add(new RecyclerViewModel(
                getString(R.string.type),
                getString(R.string.tag), tagType
        ));
        mData.add(new RecyclerViewModel(
                getString(R.string.license_plate),
                getString(R.string.rear_license_plate), dataInTag[1]
        ));
        mData.add(new RecyclerViewModel(
                getString(R.string.capacity),
                getString(R.string.volume), dataInTag[2] + " M3"
        ));

        mData.add(new RecyclerViewModel(
                getString(R.string.increase_title),
                getString(R.string.increase), dataInTag[3] + " M"
        ));
    }

    /**
     * @param dataInTag the data in tag
     */
    public void addCompleteData(String[] dataInTag) {

        mData.add(new RecyclerViewModel(
                getString(R.string.building),
                getString(R.string.number), dataInTag[0]
        ));

        mData.add(new RecyclerViewModel(
                getString(R.string.sheet_number),
                getString(R.string.number), dataInTag[4]
        ));

        MaterialsPOJO materialsPOJO = DAOMaterials.getMaterialByID(getActivity(), dataInTag[5]);
        PointPOJO pointPOJO = DAOPoints.getPointById(getActivity(), (long) Integer.parseInt(dataInTag[6]));

        mData.add(new RecyclerViewModel(
                getString(R.string.materials),
                getString(R.string.id_navision), materialsPOJO.getIdMaterialNavision(),
                getString(R.string.description), materialsPOJO.getDescription()
        ));

        mData.add(new RecyclerViewModel(
                getString(R.string.origin),
                getString(R.string.name), pointPOJO.getNombreBanco(),
                getString(R.string.chainage), pointPOJO.getCadenamiento()
        ));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
        try {
            Date exitDate = simpleDateFormat.parse(dataInTag[7]);
            mData.add(new RecyclerViewModel(
                    getString(R.string.exit_date),
                    getString(R.string.date), new SimpleDateFormat("dd/MM/yyyy").format(exitDate),
                    getString(R.string.time), new SimpleDateFormat("HH:mm:SS").format(exitDate)
            ));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mData.add(new RecyclerViewModel(
                getString(R.string.bank_user),
                getString(R.string.id_eflow), dataInTag[8],
                getString(R.string.name), dataInTag[9]
        ));
        mData.add(new RecyclerViewModel(
                getString(R.string.exit_coordinates),
                getString(R.string.latitude), dataInTag[10].split(",")[0],
                getString(R.string.longitude), dataInTag[10].split(",")[1]
        ));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onNFCIdRead(NdefMessage[] msgs, String hexId, long decId, MifareClassicCompatibilityStatus status) {
        if (status == MifareClassicCompatibilityStatus.MIFARE_CLASSIC_FULL_COMPATIBLE) {
            mReadTagCompatible = true;
            if (mReadingTagProgressDialog != null) {
                if (mReadingTagProgressDialog.isShowing()) {
                    readMifareClassicTag();
                }
            }

        } else {
            mReadTagCompatible = false;
            Log.i(TAG, "onNFCIdRead: " + status);

            switch (status) {
                case NO_COMPATIBLE_TAG:
                    UIHelper.showSnackbarWithAction(getActivity(), "El tag leído no es compatible.");
                    return;
                case NO_COMPATIBLE_HARDWARE:
                    UIHelper.showSnackbarWithAction(getActivity(), "Tu dispositivo no es compatible con el tag.");
                    return;
                default:
                    UIHelper.showSnackbarWithAction(getActivity(), "El tag leído no es compatible.");
                    return;
            }


        }
    }

    public void readMifareClassicTag() {
        Vibrator v = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(100);
        }


        MifareClassicReadWrite.readTag(getActivity(), new OnVirginTagDetectedListener() {

            @Override
            public void onVirginTagDetected(String middleData) {
                mReadingTagProgressDialog.setMessage("Tag sin contraseña detectado, leyendo datos...");
                MifareClassicReadWrite.readTag(getActivity(), new OnTagReadListenerBase() {
                    @Override
                    public void onTagReadSuccess(String data, ArrayList<Sector> sectors, Sector flagsSector) {
                        mData.clear();


                        mData.add(new RecyclerViewModel(
                                getString(R.string.data_in_tag),
                                getString(R.string.data),
                                !MifareClassicReadWrite.tagHasData(data) ? "Sin datos" : MifareClassicReadWrite.hexToString(data)
                        ));
                        if (mReadingTagProgressDialog != null)
                            mReadingTagProgressDialog.dismiss();
                        mReadingTagProgressDialog = null;
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onTagReadFailed(MiFareClassicReadStatus status, ArrayList<Sector> sectors) {
                        handleReadResult(status);
                    }
                }, null, true);
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onTagReadSuccess(String data, ArrayList<Sector> sectors, Sector flagsSector) {
                debugTagData(data, sectors);
                mReadingTagProgressDialog.dismiss();
                mReadingTagProgressDialog = null;
            }

            @Override
            public void onTagReadFailed(MiFareClassicReadStatus status, ArrayList<Sector> sectors) {
                handleReadResult(status);
            }
        }, DAOKeys.getKeysByVersion(getActivity(), KEYS_VERSION), false);


    }

    public void handleReadResult(OnTagReadListenerBase.MiFareClassicReadStatus status) {
        switch (status) {
            case UKNOWN_KEYS:
                UIHelper.showSnackbarWithAction(getActivity(), getString(R.string.uknown_keys_message));
                break;
            case USER_DATA_IS_EMPTY:
                UIHelper.showSnackbarWithAction(getActivity(), getString(R.string.empty_tag_message));
                break;
            case TAG_REMOVED_WHILE_READING:
                UIHelper.showSnackbarWithAction(getActivity(), getString(R.string.tag_removed_while_reading_message));
                break;
            case TAG_REMOVED_OR_ANY_KEY_INVALID:
                UIHelper.showSnackbarWithAction(getActivity(), getString(R.string.tag_removed_or_wrong_key));
                break;
            case USER_DATA_CORRUPTED:
                UIHelper.showSnackbarWithAction(getActivity(), getString(R.string.tag_data_corrupted));
                break;
            case NONE_KEY_VALID_FOR_READING:
                UIHelper.showSnackbarWithAction(getActivity(), getString(R.string.there_is_no_valid_key_message));
                break;
        }
        if (mReadingTagProgressDialog != null)
            mReadingTagProgressDialog.dismiss();
        mReadingTagProgressDialog = null;
    }
}
