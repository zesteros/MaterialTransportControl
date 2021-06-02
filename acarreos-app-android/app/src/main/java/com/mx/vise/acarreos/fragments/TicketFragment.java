package com.mx.vise.acarreos.fragments;


import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.nfc.NdefMessage;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mx.vise.acarreos.R;
import com.mx.vise.acarreos.activities.MainActivity;
import com.mx.vise.acarreos.adapters.CustomArrayAdapter;
import com.mx.vise.acarreos.adapters.GenericAdapter;
import com.mx.vise.acarreos.dao.DAODistances;
import com.mx.vise.acarreos.dao.DAOKeys;
import com.mx.vise.acarreos.dao.DAOMaterials;
import com.mx.vise.acarreos.dao.DAOPoints;
import com.mx.vise.acarreos.dao.DAOReprints;
import com.mx.vise.acarreos.dao.DAOTicket;
import com.mx.vise.acarreos.pojos.DistancePOJO;
import com.mx.vise.acarreos.pojos.PointPOJO;
import com.mx.vise.acarreos.pojos.ReprintPOJO;
import com.mx.vise.acarreos.pojos.TicketPOJO;
import com.mx.vise.acarreos.pojos.MaterialsPOJO;
import com.mx.vise.acarreos.tasks.OnOperationRunning;
import com.mx.vise.acarreos.tasks.WaitTask;
import com.mx.vise.acarreos.util.CancelTicketRequest;
import com.mx.vise.acarreos.util.TimeHelper;
import com.mx.vise.acarreos.util.UtilPrinter;
import com.mx.vise.acarreos.util.gps.GPSTracker;
import com.mx.vise.acarreos.util.gps.LatLng;
import com.mx.vise.acarreos.singleton.SingletonGlobal;
import com.mx.vise.acarreos.util.gps.MapCalculator;
import com.mx.vise.androiduihelper.UIHelper;
import com.mx.vise.androiduihelper.searchablespinner.SearchableSpinner;
import com.mx.vise.login.pojos.EmployeePojo;
import com.mx.vise.nfc.NFCIdListener;
import com.mx.vise.nfc.MifareClassicCompatibilityStatus;
import com.mx.vise.nfc.interfaces.OnTagReadListenerBase;
import com.mx.vise.nfc.interfaces.OnVirginTagDetectedListener;
import com.mx.vise.nfc.mifareclassic.MifareClassicReadWrite;
import com.mx.vise.nfc.mifareclassic.Sector;
import com.mx.vise.zebraprinterandroid.entities.PrintObject;
import com.mx.vise.zebraprinterandroid.enums.PrintStatus;
import com.mx.vise.zebraprinterandroid.interfaces.OnPrintListener;
import com.mx.vise.zebraprinterandroid.tasks.PrintTask;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.content.Context.VIBRATOR_SERVICE;
import static com.mx.vise.acarreos.activities.MainActivity.KEYS_VERSION;
import static com.mx.vise.acarreos.tasks.LocationThread.ALL_POINT_TYPE;
import static com.mx.vise.acarreos.tasks.LocationThread.BANK_AND_WASTE_TYPE;
import static com.mx.vise.acarreos.tasks.LocationThread.BANK_TYPE;
import static com.mx.vise.acarreos.tasks.LocationThread.THROW_TYPE;
import static com.mx.vise.acarreos.tasks.LocationThread.WASTE_TYPE;
import static com.mx.vise.androiduihelper.UIHelper.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class TicketFragment extends Fragment implements View.OnClickListener, OnPrintListener, NFCIdListener {


    private static final String TAG = "VISE";
    public static final String REPLACE_CHAR = "%";
    private static final String WITHOUT_EXIT_SHEETNUMBER_FLAG = "1";
    private SearchableSpinner mMaterialsSpinner;
    private Button mReadTagButton;
    private Button mPrintButton;
    private EditText mDiscountEditText;
    private MaterialsPOJO mSelectedMaterial;
    private TicketPOJO mTicketPOJO;
    private TextView mDiscountTextView;
    private TicketType mTicketType;
    private TextView mMainLabel;
    private TextView mDistanceTextView;
    private EditText mDistanceEditText;
    private TextView mDestinyTextView;
    private SearchableSpinner mDestinySpinner;
    private TextView mMaterialTextView;
    private SearchableSpinner mDistancesSpinner;
    private TextView mOriginTextView;
    private SearchableSpinner mOriginSpinner;

    private int mAmountReprints;

    private static final String TICKET_ALREADY_CARRIED_FLAG = "1";
    private ProgressDialog mReadingTagProgressDialog;
    private boolean mReadTagCompatible;


    public TicketFragment() {
        Log.i(TAG, "TicketFragment: instance");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tickets, container, false);

        /*
         * Se instancian los componentes iniciales (se instancian todos y se desactivan segun las reglas
         * */

        mMaterialTextView = view.findViewById(R.id.materialTextView);
        mMaterialsSpinner = view.findViewById(R.id.materialSpinner);
        mDiscountTextView = view.findViewById(R.id.discountRoyaltiesTextView);
        mDiscountEditText = view.findViewById(R.id.discountEditText);
        mReadTagButton = view.findViewById(R.id.readTagButton);
        mPrintButton = view.findViewById(R.id.printTicketButton);
        mMainLabel = view.findViewById(R.id.mainLabel);

        /*
         * Road improvement ui items
         * */
        mDistanceTextView = view.findViewById(R.id.distanceTextView);
        mDistancesSpinner = view.findViewById(R.id.distanceSpinner);
        mDistanceEditText = view.findViewById(R.id.distanceEditText);

        mDestinyTextView = view.findViewById(R.id.destinyTextView);
        mDestinySpinner = view.findViewById(R.id.destinySpinner);

        mOriginSpinner = view.findViewById(R.id.originSpinner);
        mOriginTextView = view.findViewById(R.id.originTextView);

        /*
         * Dependiendo del tipo habilita o deshabilita elementos
         * */
        SingletonGlobal.getInstance().setActualPointType(BANK_TYPE);
        SingletonGlobal.getInstance().setPointSelected(null);

        switch (getTicketType()) {
            /*
             *
             * Si es mejora de caminos habilita distancia y destino
             *
             * */
            case ROAD_IMPROVEMENT:
                mMainLabel.setText(getString(R.string.generate_road_improvement_ticket));
                mDistanceTextView.setVisibility(View.VISIBLE);
                mDistanceEditText.setVisibility(View.VISIBLE);
                mDestinyTextView.setVisibility(View.VISIBLE);
                mDestinySpinner.setVisibility(View.VISIBLE);
                mOriginSpinner.setVisibility(View.VISIBLE);
                mOriginTextView.setVisibility(View.VISIBLE);
                setPointsAdapter(mDestinySpinner, THROW_TYPE);
                setPointsAdapter(mOriginSpinner, BANK_TYPE);
                //checkAvailableDataSpinner(SpinnerType.MATERIAL);
                break;
            case ROYALTIES:
                /*
                 * Si es regalia deshabilita la distancia y el destino
                 * */
                mMainLabel.setText(getString(R.string.generate_royalty_ticket));
                mDistanceTextView.setVisibility(View.GONE);
                mDistanceEditText.setVisibility(View.GONE);

                mDestinyTextView.setVisibility(View.GONE);
                mDestinySpinner.setVisibility(View.GONE);

                mOriginSpinner.setVisibility(View.VISIBLE);
                mOriginTextView.setVisibility(View.VISIBLE);
                //checkAvailableDataSpinner(SpinnerType.MATERIAL);
                setPointsAdapter(mOriginSpinner, BANK_TYPE);
                //setMaterialsAdapter(mMaterialsSpinner, BANK_TYPE);

                setPointItemSelectedListener(mOriginSpinner);

                break;
            case CARRIES:
                //
                //
                //* Si es acarreo deshabilita todos
                //*
                //
                mMainLabel.setText(getString(R.string.generate_carry_ticket));
                hideAllForThrows();
                mDistanceTextView.setVisibility(View.GONE);
                mDistancesSpinner.setVisibility(View.GONE);

                mOriginTextView.setVisibility(View.GONE);
                mOriginSpinner.setVisibility(View.GONE);

                //checkAvailableDataSpinner(SpinnerType.DISTANCE);
                break;
            case SUPPLIES:
                /*
                 * Si es suministro deshabilita
                 * */
                mMainLabel.setText(getString(R.string.generate_supply_ticket));
                hideAllForThrows();

                mMaterialTextView.setVisibility(View.VISIBLE);
                mMaterialsSpinner.setVisibility(View.VISIBLE);


                mOriginTextView.setVisibility(View.VISIBLE);
                mOriginSpinner.setVisibility(View.VISIBLE);

                mDistanceTextView.setVisibility(View.VISIBLE);
                mDistancesSpinner.setVisibility(View.VISIBLE);

                setPointsAdapter(mOriginSpinner, BANK_TYPE);

                //checkAvailableDataSpinner(SpinnerType.BOTH);
                break;
            case FREE_CARRY:
                mMainLabel.setText(getString(R.string.generate_free_carry_ticket));


                hideAllForThrows();

                mMaterialTextView.setVisibility(View.VISIBLE);
                mMaterialsSpinner.setVisibility(View.VISIBLE);

                mDistanceTextView.setVisibility(View.VISIBLE);
                mDistancesSpinner.setVisibility(View.VISIBLE);

                mOriginSpinner.setVisibility(View.GONE);
                mOriginTextView.setVisibility(View.GONE);

                mDestinySpinner.setVisibility(View.VISIBLE);
                mDestinyTextView.setVisibility(View.VISIBLE);

                setPointsAdapter(mDestinySpinner, WASTE_TYPE);

                //checkAvailableDataSpinner(SpinnerType.BOTH);

                SingletonGlobal.getInstance().setActualPointType(WASTE_TYPE);

                setPointItemSelectedListener(mDestinySpinner);

                break;

            case INTERN_CARRY:
                SingletonGlobal.getInstance().setActualPointType(BANK_AND_WASTE_TYPE);

                mMainLabel.setText(getString(R.string.generate_intern_carry_ticket));

                hideAllForThrows();

                mMaterialTextView.setVisibility(View.VISIBLE);
                mMaterialsSpinner.setVisibility(View.VISIBLE);

                mDistanceTextView.setVisibility(View.VISIBLE);
                mDistanceEditText.setVisibility(View.VISIBLE);

                mOriginSpinner.setVisibility(View.GONE);
                mOriginTextView.setVisibility(View.GONE);

                mDestinySpinner.setVisibility(View.VISIBLE);
                mDestinyTextView.setVisibility(View.VISIBLE);

                setPointsAdapter(mDestinySpinner, THROW_TYPE);

                //checkAvailableDataSpinner(SpinnerType.BOTH);


                break;
            case MATERIAL_REQUEST:
                mMainLabel.setText(getString(R.string.generate_request_ticket));
                mDestinySpinner.setVisibility(View.GONE);
                mDestinyTextView.setVisibility(View.GONE);

                mDistanceEditText.setVisibility(View.GONE);
                mDistanceTextView.setVisibility(View.GONE);

                setPointsAdapter(mOriginSpinner, BANK_TYPE);

                //checkAvailableDataSpinner(SpinnerType.MATERIAL);
                break;

        }

        mReadTagButton.setOnClickListener(this);
        mPrintButton.setOnClickListener(this);

        List<GenericAdapter> genericAdapters = new ArrayList<>();

        CustomArrayAdapter<GenericAdapter> arrayAdapter = new CustomArrayAdapter<>(Objects.requireNonNull(getActivity()), genericAdapters);

        List<GenericAdapter> distanceAdapters = new ArrayList<>();

        CustomArrayAdapter<GenericAdapter> distancesAdapter = new CustomArrayAdapter<>(getActivity(), distanceAdapters);


        mMaterialsSpinner.setAdapter(arrayAdapter);

        mDistancesSpinner.setAdapter(distancesAdapter);

        SingletonGlobal.getInstance().setAvailableMaterials(arrayAdapter);

        SingletonGlobal.getInstance().setAvailableDistances(distancesAdapter);

        SingletonGlobal.getInstance().setMaterialAvailableSpinner(mMaterialsSpinner);

        SingletonGlobal.getInstance().setDistancesSpinner(mDistancesSpinner);

        mMaterialsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (SingletonGlobal.getInstance().getAvailableMaterials() != null) {
                    final String materialSelectedId = SingletonGlobal.getInstance().getAvailableMaterials().getItem(position).getValue();
                    mSelectedMaterial = DAOMaterials.getMaterialByID(getActivity(), materialSelectedId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ((MainActivity) getActivity()).setNfcIdListener(this);

        // Inflate the layout for this fragment
        return view;
    }

    public void setPointItemSelectedListener(SearchableSpinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                GenericAdapter genericAdapter = (GenericAdapter) spinner.getAdapter().getItem(i);
                SingletonGlobal.getInstance().setPointSelected(Integer.parseInt(genericAdapter.getValue()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                SingletonGlobal.getInstance().setPointSelected(null);
            }
        });
    }


    public void checkAvailableDataSpinner(final SpinnerType spinnerType) {

        boolean isAvailableData = false;

        if (spinnerType == SpinnerType.DISTANCE)
            isAvailableData = !DAODistances.getAvailableDistances(getActivity(), null).isEmpty();
        else if (spinnerType == SpinnerType.MATERIAL)
            isAvailableData = !DAOMaterials.getAvailableMaterials(getActivity(), null).isEmpty();
        else if (spinnerType == SpinnerType.BOTH)
            isAvailableData =
                    !DAOMaterials.getAvailableMaterials(getActivity(), null).isEmpty() &&
                            !DAODistances.getAvailableDistances(getActivity(), null).isEmpty();


        String type;
        if (spinnerType == SpinnerType.DISTANCE) type = getString(R.string.distance);
        else type = spinnerType == SpinnerType.MATERIAL ? getString(R.string.materials) :
                getString(R.string.materials_and_distances);

        String title = getString(R.string.no_data_available_title).replace(REPLACE_CHAR, type);
        String message = getString(R.string.no_data_available_message).replace(REPLACE_CHAR, type);

        if (!isAvailableData) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setCancelable(false)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(R.string.accept, (dialog1, which) -> getActivity().onBackPressed())
                    .show();
        }
    }

    /**
     * @param spinner   el spinner
     * @param pointType el tipo de punto
     */
    public void setPointsAdapter(final Spinner spinner, final int pointType) {

        final List<GenericAdapter> listOfItems = new ArrayList<>();

        /*
         * Agrega los puntos disponibles
         * */

        String building = SingletonGlobal.getInstance().getSession().getAssignedBuilding().getBuildingId();
        List<PointPOJO> points = DAOPoints.getAllPoints(getActivity(), building, pointType);


        for (PointPOJO point : points) {
            GenericAdapter genericAdapter = new GenericAdapter();
            String name = pointType == THROW_TYPE ? point.getCadenamiento() + " - " + point.getNombreBanco()
                    : point.getNombreBanco();
            genericAdapter.setText(name);
            genericAdapter.setValue(String.valueOf(point.getIdPuntoServer()));
            listOfItems.add(genericAdapter);
        }

        /*
         * Si es banco entonces
         * */
        if (pointType == 1) {
            PointPOJO actualPoint = SingletonGlobal.getInstance().getActualPoint();
            /*
             * Si el punto actual es banco
             * */
            if (actualPoint != null) {
                if (actualPoint.getTipoPunto() == 1) {
                    /*
                     * Solo muestra el banco actual
                     * */
                    listOfItems.clear();

                    GenericAdapter genericAdapter = new GenericAdapter();
                    genericAdapter.setText(actualPoint.getCadenamiento() + " - " + actualPoint.getNombreBanco());
                    genericAdapter.setValue(String.valueOf(actualPoint.getIdPuntoServer()));

                    listOfItems.add(genericAdapter);
                }
            }


        }


        final CustomArrayAdapter<GenericAdapter> pointAdapter = new CustomArrayAdapter<>(getActivity(), listOfItems);

        if (spinner instanceof SearchableSpinner) {
            ((SearchableSpinner) spinner).setPositiveButton(getString(R.string.accept));
            if (pointType == BANK_TYPE) {
                ((SearchableSpinner) spinner)
                        .setTitle(getString(R.string.select_a)
                                .replace(REPLACE_CHAR, getString(R.string.bank)));
            } else if (pointType == THROW_TYPE) {
                ((SearchableSpinner) spinner)
                        .setTitle(getString(R.string.select_a).replace(REPLACE_CHAR, getString(R.string.drop)));
            } else if (pointType == WASTE_TYPE) {
                ((SearchableSpinner) spinner)
                        .setTitle(getString(R.string.select_a).replace(REPLACE_CHAR, getString(R.string.waste)));
            }
            ((SearchableSpinner) spinner)
                    .setOnSearchableItemCreatedListener(
                            o -> ((GenericAdapter) o).getText()
                    );
        }
        spinner.setAdapter(pointAdapter);


    }

    public void hideAllForThrows() {
        mDistanceTextView.setVisibility(View.GONE);
        mDistanceEditText.setVisibility(View.GONE);

        mDestinyTextView.setVisibility(View.GONE);
        mDestinySpinner.setVisibility(View.GONE);

        mMaterialTextView.setVisibility(View.GONE);
        mMaterialsSpinner.setVisibility(View.GONE);
    }


    /**
     * @param v la vista q se hizo click
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {

        final EmployeePojo employeePojo = ((MainActivity) getActivity()).getEmployeePojo();


        switch (v.getId()) {

            case R.id.readTagButton:

                if (!validateFields()) return;

                if (mReadingTagProgressDialog == null) {


                    mReadingTagProgressDialog = ProgressDialog
                            .show(
                                    getActivity(),
                                    getString(R.string.reading_tag),
                                    getString(R.string.wait_please),
                                    true,
                                    true);
                    mReadingTagProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (dialogInterface, i) -> {
                        mReadingTagProgressDialog.dismiss();
                        mReadingTagProgressDialog = null;
                    });

                    mReadingTagProgressDialog.setOnCancelListener(dialog -> {
                        mReadingTagProgressDialog.dismiss();
                        mReadingTagProgressDialog = null;
                    });
                    if (mReadTagCompatible)
                        readMifareClassicTag();
                }

                break;
            case R.id.printTicketButton:


                if (mTicketPOJO != null) {

                    new PrintTask(getActivity(), false)
                            .setOnPrintListener(TicketFragment.this)
                            .execute(new UtilPrinter().getPrintObjects(employeePojo, mTicketPOJO, getTicketType(), false, 0));


                } else {
                    showSnackbarWithAction(getActivity(), getString(R.string.did_not_captured_a_tag));
                }
                break;
            default:
                break;

        }
    }


    /**
     * @param data        los datos a depurar
     * @param sectors
     * @param flagsSector
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void debugTagData(String data, ArrayList<Sector> sectors, Sector flagsSector) {
        /*Separa los datos con un pipe*/
        final String[] dataFromCubage = data.split("\\|");

        mTicketPOJO = null;

        /*Se espera una longitud de 4 forzada para los folios de regalias o mejora de caminos*/
        if ((dataFromCubage.length >= 4) &&
                (getTicketType() == TicketType.ROYALTIES ||
                        getTicketType() == TicketType.ROAD_IMPROVEMENT ||
                        getTicketType() == TicketType.SUPPLIES ||
                        getTicketType() == TicketType.FREE_CARRY ||
                        getTicketType() == TicketType.INTERN_CARRY ||
                        getTicketType() == TicketType.MATERIAL_REQUEST)) {

            /*La placa es la posicion 1*/
            String licencePlate = dataFromCubage[1];

            try {
                float volume = Float.parseFloat(dataFromCubage[2]);
                float increase = Float.parseFloat(dataFromCubage[3]);

                mTicketPOJO = new TicketPOJO();

                mTicketPOJO.setRearLicensePlate(licencePlate);
                mTicketPOJO.setCapacity(volume);
                mTicketPOJO.setIncrease(increase);


                if (getTicketType() == TicketType.SUPPLIES ||
                        getTicketType() == TicketType.FREE_CARRY ||
                        getTicketType() == TicketType.INTERN_CARRY ||
                        getTicketType() == TicketType.MATERIAL_REQUEST) {

                    showSnackbarWithAction(getActivity(),
                            getString(R.string.read_tag_sucess));

                    proceedWithPrint(getString(R.string.captured_tag), false);

                    SingletonGlobal.getInstance().setTicketWasPrinted(false);

                } else if (getTicketType() == TicketType.ROYALTIES ||
                        getTicketType() == TicketType.ROAD_IMPROVEMENT) {
                    writeInTag(sectors);
                }
                switch (getTicketType()) {
                    case SUPPLIES:
                    case FREE_CARRY:
                    case INTERN_CARRY:
                    case MATERIAL_REQUEST:
                        completeProcess();
                        saveTicket(((MainActivity) getActivity()).getEmployeePojo());
                        break;

                }


            } catch (NumberFormatException e) {
                mTicketPOJO = null;
                showSnackbarWithAction(getActivity(), getString(R.string.info_in_tag_is_invalid));
                e.printStackTrace();
            }
            /*
             * Se espera una longitud de 11 para los de acarreos
             *
             * Aqui se determina que el tag leido es de acarreos
             *
             * */
        } else if ((dataFromCubage.length >= 12) &&
                getTicketType() == TicketType.CARRIES) {

            CancelTicketRequest cancelTicketRequest = DAOTicket.ticketExist(getActivity(), dataFromCubage[4]);

            if (cancelTicketRequest.getCancelStatus() == CancelTicketRequest.CancelStatus.ALREADY_CANCELED) {
                showSnackbarWithAction(getActivity(), getString(R.string.ticket_already_canceled));
                return;
            }


            /*
             * Si la obra del tag no corresponde a la obra donde se esta recibiendo el acarreo
             * muestra el mensaje
             * */
            String buildingInTag = dataFromCubage[0];
            String assignedBuilding = SingletonGlobal.getInstance().getSession().getAssignedBuilding().getBuildingId();

            if (buildingInTag != null) {
                if (!buildingInTag.equals(assignedBuilding)) {
                    showDialog(
                            getActivity(),
                            getString(R.string.wrong_building),
                            getString(R.string.tag_does_not_correspond).replace(REPLACE_CHAR, buildingInTag),
                            false,
                            true,
                            null);
                    return;
                }
            }



            /*
             * Obtiene la bandera de acarreo pendiente
             * */
            String flags = flagsSector.getBlocks().get(0).getData().substring(0, 1);

            if (flags.equals(TICKET_ALREADY_CARRIED_FLAG)) {
                showSnackbarWithAction(getActivity(), getString(R.string.tag_already_carried));
                return;
            }


            /*
             *
             * Si es acarreo, jala automaticamente la distancia desde el tag
             * */
            int idPointOrigin = Integer.parseInt(dataFromCubage[6]);

            ArrayList<DistancePOJO> availableDistances = DAODistances.getAvailableDistances(getActivity(), idPointOrigin);

            PointPOJO pointPOJO = DAOPoints.getPointById(getActivity(), Long.valueOf(idPointOrigin));

            if (availableDistances.isEmpty()) {
                showDialog(getActivity(),
                        getString(R.string.origin_without_distances),
                        getString(R.string.origin_without_distances_message).replace(REPLACE_CHAR, pointPOJO.getNombreBanco()),
                        false,
                        true,
                        null);
                return;
            }

            /*
             *
             * Escribe la bandera de folio
             *
             * */
            MifareClassicReadWrite.writeFlag(
                    getActivity(),
                    DAOKeys.getFlagSectorKey(getActivity(),
                            KEYS_VERSION),
                    WITHOUT_EXIT_SHEETNUMBER_FLAG,
                    0);


            mTicketPOJO = new TicketPOJO();

            /*
             * Si solo hay una distancia procede
             * */
            if (availableDistances.size() == 1) {
                mTicketPOJO.setDistance(availableDistances.get(0).getDistance());
                continueCarriesProcess(dataFromCubage, null);
            }
            /*
             * Si no selecciona una distancia del banco
             * */
            else {
                AlertDialog.Builder distanceDialog = new AlertDialog.Builder(getActivity());
                distanceDialog.setTitle(getString(R.string.select_distance));
                distanceDialog.setCancelable(false);
                String[] distancesInDialog = new String[availableDistances.size()];
                for (DistancePOJO distancePOJO : availableDistances)
                    distancesInDialog[availableDistances.indexOf(distancePOJO)] = distancePOJO.getDistance() + " KM";

                distanceDialog.setItems(distancesInDialog, (dialog, which) -> {

                    mTicketPOJO.setDistance(availableDistances.get(which).getDistance());
                    continueCarriesProcess(dataFromCubage, dialog);
                });

                distanceDialog.show();
            }


        } else {

            /*
             * Si no tiene datos correctos o si tiene ya datos no se puede sobreescribir
             * */
            showSnackbarWithAction(getActivity(), getString(R.string.invalid_tag_with_info));
        }
    }

    /**
     * @param dataFromCubage los datos de la cubicacion
     * @param dialog         el dialog
     */
    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void continueCarriesProcess(String[] dataFromCubage, DialogInterface dialog) {
        if (getTicketType() != TicketType.SUPPLIES) {
            MaterialsPOJO materialsPOJO = DAOMaterials.getMaterialByID(getActivity(), dataFromCubage[5]);
            mTicketPOJO.setMaterial(materialsPOJO);

            PointPOJO pointPOJO = DAOPoints.getPointById(getActivity(), (long) Integer.parseInt(dataFromCubage[6]));
            mTicketPOJO.setOrigin(pointPOJO);
        }


        String licencePlate = dataFromCubage[1];

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss", Locale.CANADA);

            float volume = Float.parseFloat(dataFromCubage[2]);
            float increase = Float.parseFloat(dataFromCubage[3]);

            mTicketPOJO.setBuilding(dataFromCubage[0]);
            mTicketPOJO.setRearLicensePlate(licencePlate);
            mTicketPOJO.setCapacity(volume);
            mTicketPOJO.setIncrease(increase);
            mTicketPOJO.setSheetNumber(dataFromCubage[4]);
            mTicketPOJO.setExitDate(simpleDateFormat.parse(dataFromCubage[7]));
            mTicketPOJO.setUserIdBank(Integer.parseInt(dataFromCubage[8]));
            mTicketPOJO.setUsernameBank(dataFromCubage[9]);
            mTicketPOJO.setExitCoordinates(new LatLng()
                    .setLatitude(
                            BigDecimal.valueOf(Double.parseDouble(dataFromCubage[10].split(",")[0]))
                    )
                    .setLongitude(
                            BigDecimal.valueOf(Double.parseDouble(dataFromCubage[10].split(",")[1]))
                    ));
            mTicketPOJO.setUnitOfMeasure(Integer.parseInt(dataFromCubage[11]));

            showSnackbarWithAction(getActivity(),
                    getString(R.string.read_tag_sucess));

            proceedWithPrint(getString(R.string.captured_tag), false);

            SingletonGlobal.getInstance().setTicketWasPrinted(false);

            completeProcess();

            saveTicket(((MainActivity) getActivity()).getEmployeePojo());

        } catch (NumberFormatException e) {
            mTicketPOJO = null;
            e.printStackTrace();
            showSnackbarWithAction(getActivity(), getString(R.string.info_in_tag_is_invalid));
            if (dialog != null) dialog.dismiss();
        } catch (ParseException e) {
            e.printStackTrace();
            if (dialog != null) dialog.dismiss();
        }
        if (dialog != null) dialog.dismiss();
    }


    /**
     * Metodo para comenzar a escribir en el tag, dependiendo si fue exitoso o no se llama a {@link #()}
     * o {@link #(boolean)}
     */
    public void writeInTag(ArrayList<Sector> sectors) {
        completeProcess();

        final EmployeePojo employeePojo = ((MainActivity) getActivity()).getEmployeePojo();

        MifareClassicReadWrite.writeData(
                getActivity(),
                parseTicketToTagInfo(employeePojo),
                sectors,
                new com.mx.vise.nfc.mifareclassic.OnTagWriteListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onTagWriteSuccess() {
                        /*Si fue exitosa entra aquí y muestra al usuario*/
                        showSnackbarWithAction(getActivity(), getString(R.string.tag_successfully_writed));

                        MifareClassicReadWrite.writeFlag(getActivity(), DAOKeys.getFlagSectorKey(getActivity(), KEYS_VERSION), "0", 0);

                        proceedWithPrint(getString(R.string.generated_tag), true);

                        SingletonGlobal.getInstance().setTicketWasPrinted(false);

                        saveTicket(employeePojo);

                        switch (getTicketType()) {
                            case ROAD_IMPROVEMENT:
                                disableUIForRoyaltiesAndRoadImprovement();
                                mDistanceTextView.setVisibility(View.GONE);
                                mDistanceEditText.setVisibility(View.GONE);
                                mDiscountEditText.setVisibility(View.GONE);
                                mDestinySpinner.setEnabled(false);
                                break;
                            case ROYALTIES:
                                disableUIForRoyaltiesAndRoadImprovement();
                                break;

                        }
                    }

                    @Override
                    public void onTagWriteFailed(OnTagReadListenerBase.MiFareClassicWriteStatus status) {
                        showSnackbarWithAction(getActivity(), getString(R.string.something_went_wrong_writing));
                    }
                });
    }


    /**
     * Si se ha impreso correctamente
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onPrintSuccess() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setPositiveButton(R.string.yes, (dialog, which) -> {

                    setAmountReprints(0);
                    SingletonGlobal.getInstance().setTicketWasPrinted(true);
                    /*
                     * Cambia de color
                     * */
                    mPrintButton.setBackgroundTintList(getResources().getColorStateList(R.color.green));
                    /*
                     * Cambia el texto del botón
                     * */
                    mPrintButton.setText(getString(R.string.ticket_printed));
                    /*Icono*/
                    mPrintButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);

                    mPrintButton.setEnabled(false);

                    cleanFields();

                })
                .setMessage(R.string.information_is_correct)
                .setNegativeButton(R.string.print_again, (dialogInterface, i) -> {
                    final EmployeePojo employeePojo = ((MainActivity) getActivity()).getEmployeePojo();

                    setAmountReprints(getAmountReprints() + 1);

                    LatLng latLng = new LatLng();
                    if (SingletonGlobal.getInstance().getGpsTracker() != null) {
                        GPSTracker gpsTracker = SingletonGlobal.getInstance().getGpsTracker();
                        latLng.longitude = gpsTracker.longitude;
                        latLng.latitude = gpsTracker.latitude;
                    }
                    String coordinates = latLng.latitude.floatValue() + "," + latLng.longitude.floatValue();

                    final ReprintPOJO reprintPOJO = new ReprintPOJO();

                    reprintPOJO.setCoordinates(coordinates);
                    reprintPOJO.setAddUser(employeePojo.getEmployeeId());
                    reprintPOJO.setSheetNumber(mTicketPOJO.getSheetNumber());

                    DAOReprints.addReprint(getActivity(), reprintPOJO);

                    new PrintTask(getActivity(), true)
                            .setOnPrintListener(TicketFragment.this)
                            .execute(new UtilPrinter()
                                    .getPrintObjects(
                                            employeePojo,
                                            mTicketPOJO,
                                            getTicketType(),
                                            true,
                                            getAmountReprints()));
                })
                .setCancelable(false)
                .show();


    }

    private void saveTicket(EmployeePojo employeePojo) {

        switch (getTicketType()) {
            case ROAD_IMPROVEMENT:
                /*
                 * Cambia el estatus de todos los boletos que contengan esa placa
                 * */
                DAOTicket.saveRoadImprovement(getActivity(), employeePojo, mTicketPOJO);
                break;
            case ROYALTIES:
                /*
                 * Cambia el estatus de todos los boletos que contengan esa placa
                 * */
                DAOTicket.saveRoyalty(getActivity(), employeePojo, mTicketPOJO);
                break;
            case CARRIES:
            case SUPPLIES:
                DAOTicket.saveCarry(getActivity(), employeePojo, mTicketPOJO);
                break;
            case FREE_CARRY:
            case INTERN_CARRY:
                DAOTicket.saveFreeInternCarry(getActivity(), employeePojo, mTicketPOJO);
                break;
            case MATERIAL_REQUEST:
                DAOTicket.saveMaterialRequest(getActivity(), employeePojo, mTicketPOJO);
                break;
        }

    }

    /**
     * Limpia los campos y objetos
     */
    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void cleanFields() {

        mDiscountTextView.setVisibility(View.VISIBLE);
        mDiscountEditText.setVisibility(View.VISIBLE);
        mDiscountEditText.setText("0");

        mReadTagButton.setText(R.string.read_tag);
        mReadTagButton.setBackgroundTintList(getResources().getColorStateList(R.color.primary));
        mReadTagButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_read_tag, 0, 0, 0);
        mPrintButton.setText(R.string.print_ticket);
        mPrintButton.setBackgroundTintList(getResources().getColorStateList(R.color.primary));
        mPrintButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_print, 0, 0, 0);
        mPrintButton.setVisibility(View.GONE);
        mPrintButton.setEnabled(true);

        showSnackbarWithAction(getActivity(), getString(R.string.ticket_successfully_saved));


        mTicketPOJO = null;
        switch (getTicketType()) {
            case ROAD_IMPROVEMENT:
                cleanFieldsRoyaltyAndRoadImprovement();
                cleanFieldsRoadImprovement();
                break;
            case CARRIES:

                mDistanceTextView.setVisibility(View.VISIBLE);
                mDistancesSpinner.setEnabled(true);
                break;
            case SUPPLIES:
                mDistanceTextView.setVisibility(View.VISIBLE);
                mDistancesSpinner.setEnabled(true);
                mMaterialsSpinner.setEnabled(true);
                mOriginSpinner.setEnabled(true);
                break;
            case ROYALTIES:
                cleanFieldsRoyaltyAndRoadImprovement();
                break;
            case INTERN_CARRY:
                mDestinySpinner.setEnabled(true);
                mDestinySpinner.setSelection(0);
                mMaterialsSpinner.setEnabled(true);
                mMaterialsSpinner.setSelection(0);
                mDiscountEditText.setVisibility(View.VISIBLE);
                mDiscountTextView.setVisibility(View.VISIBLE);
                mDistanceEditText.setEnabled(true);
                mDistanceEditText.setText("0");
                break;
            case FREE_CARRY:
                mDestinySpinner.setEnabled(true);
                mDestinySpinner.setSelection(0);
                mMaterialsSpinner.setEnabled(true);
                mMaterialsSpinner.setSelection(0);
                mDiscountEditText.setVisibility(View.VISIBLE);
                mDiscountTextView.setVisibility(View.VISIBLE);
                mDistancesSpinner.setEnabled(true);
                mDistancesSpinner.setSelection(0);
                break;

        }
    }

    public void cleanFieldsRoyaltyAndRoadImprovement() {
        mMaterialsSpinner.setEnabled(true);
        mOriginSpinner.setEnabled(true);

    }

    public void cleanFieldsRoadImprovement() {
        mDestinySpinner.setEnabled(true);
        mDestinyTextView.setVisibility(View.VISIBLE);
        mDistanceEditText.setText("");
        mDistanceEditText.setEnabled(true);
        mDistanceEditText.setVisibility(View.VISIBLE);
        mDistanceTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Completa la instancia del objeto del proceso (boleto de regalia)
     */
    public void completeProcess() {

        EmployeePojo employeePojo = ((MainActivity) getActivity()).getEmployeePojo();


        mTicketPOJO.setDiscount(Float.valueOf(mDiscountEditText.getText().toString()));
        mTicketPOJO.setAddUser(employeePojo.getEmployeeId());


        switch (getTicketType()) {
            case ROAD_IMPROVEMENT:
                completeProcessForRoyaltyAndRoadImprovement(employeePojo);
                mTicketPOJO.setDistance(Float.parseFloat(mDistanceEditText.getText().toString()));
                PointPOJO selectedDestiny = DAOPoints.getPointById(getActivity(),
                        (long) Integer.parseInt(((GenericAdapter) mDestinySpinner.getSelectedItem()).getValue()));
                mTicketPOJO.setDestiny(selectedDestiny);
                mTicketPOJO.setTicketType(2);
                break;

            case ROYALTIES:
                completeProcessForRoyaltyAndRoadImprovement(employeePojo);
                mTicketPOJO.setTicketType(1);
                break;
            case CARRIES:
                setDataForThrow(employeePojo, 3);
                break;
            case SUPPLIES:
                setDataForThrow(employeePojo, 4);
                setDataForSupplies(employeePojo);
                break;
            case FREE_CARRY:
                setDataForFreeAndInternCarry(employeePojo, 5);
                break;
            case INTERN_CARRY:
                setDataForFreeAndInternCarry(employeePojo, 6);
                break;
            case MATERIAL_REQUEST:
                setDataForMaterialRequest(employeePojo, 7);
                break;


        }

    }

    private void setDataForMaterialRequest(EmployeePojo employeePojo, int ticketType) {
        mTicketPOJO.setTicketType(ticketType);
        mTicketPOJO.setSheetNumber(String.valueOf(TimeHelper.obtainValidDate().getDate().getTime()));
        mTicketPOJO.setBuilding(employeePojo.getAssignedBuilding().getBuildingId());
        mTicketPOJO.setMaterial(mSelectedMaterial);
        LatLng latLng = new LatLng();
        if (SingletonGlobal.getInstance().getGpsTracker() != null) {
            GPSTracker gpsTracker = SingletonGlobal.getInstance().getGpsTracker();
            latLng.longitude = gpsTracker.longitude;
            latLng.latitude = gpsTracker.latitude;
        }

        mTicketPOJO.setExitCoordinates(latLng);


        mTicketPOJO.setExitDate(TimeHelper.obtainValidDate().getDate());


        mTicketPOJO.setUsernameBank(employeePojo.getEmployeeName());
        mTicketPOJO.setUserIdBank(employeePojo.getEmployeeId());
        PointPOJO selectedOrigin = DAOPoints.getPointById(getActivity(),
                (long) Integer.parseInt(((GenericAdapter) mOriginSpinner.getSelectedItem()).getValue()));
        mTicketPOJO.setOrigin(selectedOrigin);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyy");
        try {
            Date date = simpleDateFormat.parse(simpleDateFormat.format(TimeHelper.obtainValidDate().getDate()));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 99);
            calendar.add(Calendar.DATE, 3);
            mTicketPOJO.setExpirationDate(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mTicketPOJO.setUnitOfMeasure(0);
    }

    private void setDataForFreeAndInternCarry(EmployeePojo employeePojo, int ticketType) {
        mTicketPOJO.setTicketType(ticketType);
        mTicketPOJO.setBuilding(employeePojo.getAssignedBuilding().getBuildingId());
        mTicketPOJO.setSheetNumber(String.valueOf(TimeHelper.obtainValidDate().getDate().getTime()));
        mTicketPOJO.setMaterial(mSelectedMaterial);
        mTicketPOJO.setOrigin(SingletonGlobal.getInstance().getActualPoint());

        LatLng latLng = new LatLng();
        if (SingletonGlobal.getInstance().getGpsTracker() != null) {
            GPSTracker gpsTracker = SingletonGlobal.getInstance().getGpsTracker();
            latLng.longitude = gpsTracker.longitude;
            latLng.latitude = gpsTracker.latitude;
        }

        mTicketPOJO.setExitCoordinates(latLng);

        mTicketPOJO.setExitDate(TimeHelper.obtainValidDate().getDate());

        mTicketPOJO.setUsernameBank(employeePojo.getEmployeeName());
        mTicketPOJO.setUserIdBank(employeePojo.getEmployeeId());
        PointPOJO selectedDestiny = DAOPoints.getPointById(getActivity(),
                (long) Integer.parseInt(((GenericAdapter) mDestinySpinner.getSelectedItem()).getValue()));
        mTicketPOJO.setDestiny(selectedDestiny);

        float distance = 0;
        try {
            if (ticketType == 5) {
                if (mDistancesSpinner.getSelectedItem() != null)
                    distance = Float.parseFloat(((GenericAdapter) mDistancesSpinner.getSelectedItem()).getValue());
            } else {
                distance = Float.parseFloat(mDistanceEditText.getText().toString());
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        mTicketPOJO.setDistance(distance);

        mTicketPOJO.setUnitOfMeasure(0);
    }

    private void setDataForSupplies(EmployeePojo employeePojo) {

        mTicketPOJO.setMaterial(mSelectedMaterial);


        PointPOJO selectedOrigin = DAOPoints.getPointById(getActivity(),
                (long) Integer.parseInt(((GenericAdapter) mOriginSpinner.getSelectedItem()).getValue()));
        mTicketPOJO.setOrigin(selectedOrigin);

        LatLng latLng = new LatLng();
        if (SingletonGlobal.getInstance().getGpsTracker() != null) {
            GPSTracker gpsTracker = SingletonGlobal.getInstance().getGpsTracker();
            latLng.longitude = gpsTracker.longitude;
            latLng.latitude = gpsTracker.latitude;
        }
        mTicketPOJO.setExitCoordinates(latLng);
        mTicketPOJO.setExitDate(mTicketPOJO.getArrivalDate());

        mTicketPOJO.setBuilding(employeePojo.getAssignedBuilding().getBuildingId());
        mTicketPOJO.setSheetNumber(String.valueOf(TimeHelper.obtainValidDate().getDate().getTime()));
        mTicketPOJO.setUnitOfMeasure(0);

    }

    public void setDataForThrow(EmployeePojo employeePojo, int throwType) {
        LatLng latLng = new LatLng();
        if (SingletonGlobal.getInstance().getGpsTracker() != null) {
            GPSTracker gpsTracker = SingletonGlobal.getInstance().getGpsTracker();
            latLng.longitude = gpsTracker.longitude;
            latLng.latitude = gpsTracker.latitude;
        }

        mTicketPOJO.setArrivalCoordinates(latLng);
        mTicketPOJO.setArrivalDate(TimeHelper.obtainValidDate().getDate());
        mTicketPOJO.setTicketType(throwType);
        mTicketPOJO.setDiscount(Float.valueOf(mDiscountEditText.getText().toString()));
        PointPOJO destiny = SingletonGlobal.getInstance().getActualPoint();

        float distance = 0;

        if (throwType == 4) {

            try {
                if (mDistancesSpinner.getSelectedItem() != null)
                    distance = Float.parseFloat(((GenericAdapter) mDistancesSpinner.getSelectedItem()).getValue());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            mTicketPOJO.setDistance(distance);
        }
        mTicketPOJO.setDestiny(destiny);
        mTicketPOJO.setUserIdThrow(employeePojo.getEmployeeId());
        mTicketPOJO.setUserNameThrow(employeePojo.getEmployeeName());
    }

    public void completeProcessForRoyaltyAndRoadImprovement(EmployeePojo employeePojo) {

        /*Tipo de boleto regalia*/
        mTicketPOJO.setBuilding(employeePojo.getAssignedBuilding().getBuildingId());

        mTicketPOJO.setExitDate(TimeHelper.obtainValidDate().getDate());
        LatLng latLng = new LatLng();
        if (SingletonGlobal.getInstance().getGpsTracker() != null) {
            GPSTracker gpsTracker = SingletonGlobal.getInstance().getGpsTracker();
            latLng.longitude = gpsTracker.longitude;
            latLng.latitude = gpsTracker.latitude;
        }
        mTicketPOJO.setExitCoordinates(latLng);
        mTicketPOJO.setMaterial(mSelectedMaterial);
        PointPOJO selectedOrigin = DAOPoints.getPointById(getActivity(),
                (long) Integer.parseInt(((GenericAdapter) mOriginSpinner.getSelectedItem()).getValue()));
        mTicketPOJO.setOrigin(selectedOrigin);
        //String imei = Singleton.getInstance().getImei();

        mTicketPOJO.setSheetNumber(String.valueOf(TimeHelper.obtainValidDate().getDate().getTime()));

        mTicketPOJO.setUserIdBank(employeePojo.getEmployeeId());
        mTicketPOJO.setUsernameBank(employeePojo.getEmployeeName());

        mTicketPOJO.setUnitOfMeasure(0);

    }

    /**
     * @return transforma la regalia a información en el tag
     */
    public String parseTicketToTagInfo(EmployeePojo employeePojo) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");

        String data =
                /*Obra - 0*/
                employeePojo.getAssignedBuilding().getBuildingId() +
                        /*Placa - 1*/
                        "|" + mTicketPOJO.getRearLicensePlate().replace("-", "") +
                        /*Capacidad - 2*/
                        "|" + MapCalculator.round(mTicketPOJO.getCapacity(), 2) +
                        /*Monten - 3*/
                        "|" + MapCalculator.round(mTicketPOJO.getIncrease(), 2) +
                        /*Folio - 4*/
                        "|" + mTicketPOJO.getSheetNumber() +
                        /*Material - 5*/
                        "|" + mTicketPOJO.getMaterial().getIdMaterialServer() +
                        /*Origen - 6*/
                        "|" + mTicketPOJO.getOrigin().getIdPuntoServer() +
                        /*Fecha de salida - 7*/
                        "|" + simpleDateFormat.format(mTicketPOJO.getExitDate()) +
                        /*Usuario id - 8*/
                        "|" + mTicketPOJO.getUserIdBank() +
                        /*Usuario nombre - 9*/
                        "|" + mTicketPOJO.getUsernameBank().split(" ")[0] +
                        /*Coordenadas de salida - 10*/
                        "|" + MapCalculator.round(mTicketPOJO.getExitCoordinates().latitude.floatValue(), 6) + "," + MapCalculator.round(mTicketPOJO.getExitCoordinates().longitude.floatValue(), 6) +
                        /*Unidad de medida - 11*/
                        "|" + mTicketPOJO.getUnitOfMeasure() +
                        /*Si ya tiene boleto de acarreo - 12*/
                        "|0|";
        return data;
    }

    /**
     * @return si los campos requeridos son validos
     */
    public boolean validateFields() {
        if (!validateField(getActivity(), mDiscountEditText, getString(R.string.set_discount)))
            return false;

        switch (getTicketType()) {
            case INTERN_CARRY:
            case ROAD_IMPROVEMENT:

                float distance = 0;
                try {
                    distance = Float.parseFloat(mDistanceEditText.getText().toString());
                    if (distance <= 0) {
                        UIHelper.showSnackbarWithAction(getActivity(), getString(R.string.select_distance));
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    UIHelper.showSnackbarWithAction(getActivity(), getString(R.string.select_distance));
                    return false;
                }
                break;
            case ROYALTIES:
                break;
            case CARRIES:
            case SUPPLIES:
                break;
        }
        return true;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onPrintFailed(PrintStatus printStatus) {
        switch (printStatus) {
            case MAC_DOES_NOT_DECLARED:
                showSnackbarWithAction(getActivity(), getString(R.string.print_does_not_configured));
                break;
            case CAN_NOT_CONNECT_TO_ESTABLISHED_PRINTER:
                showSnackbarWithAction(getActivity(), getString(R.string.cannot_communicate_with_printer));
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void proceedWithPrint(String message, boolean hideDiscount) {

        switch (getTicketType()) {
            case INTERN_CARRY:
            case FREE_CARRY:
                mDistanceEditText.setEnabled(false);
                mDestinySpinner.setEnabled(false);
                mMaterialsSpinner.setEnabled(false);
                mDiscountEditText.setVisibility(View.GONE);
                mDiscountTextView.setVisibility(View.GONE);
                mDistancesSpinner.setEnabled(false);
                break;
            case MATERIAL_REQUEST:
                //mOriginSpinner.setEnabled(false);
//                mMaterialsSpinner.setEnabled(false);
//                mDiscountEditText.setVisibility(View.GONE);
//                mDiscountTextView.setVisibility(View.GONE);
                break;

        }

        /*
         * Cambia de color
         * */
        mReadTagButton.setBackgroundTintList(getResources().getColorStateList(R.color.green));
        /*
         * Cambia el texto del botón
         * */
        mReadTagButton.setText(message);
        /*Icono*/
        mReadTagButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);


        mPrintButton.setVisibility(View.VISIBLE);
        if (hideDiscount) {
            mDiscountTextView.setVisibility(View.GONE);
            mDiscountEditText.setVisibility(View.GONE);
        }
    }


    public void disableUIForRoyaltiesAndRoadImprovement() {
        mMaterialsSpinner.setEnabled(false);
        mOriginSpinner.setEnabled(false);

    }

    public void setTicketType(TicketType ticketType) {
        this.mTicketType = ticketType;
    }

    public TicketType getTicketType() {
        return mTicketType;
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
                    showSnackbarWithAction(getActivity(), "El tag leído no es compatible.");
                    return;
                case NO_COMPATIBLE_HARDWARE:
                    showSnackbarWithAction(getActivity(), "Tu dispositivo no es compatible con el tag.");
                    return;
                default:
                    showSnackbarWithAction(getActivity(), "El tag leído no es compatible.");
                    return;
            }


        }

    }

    public void readMifareClassicTag() {

        Log.i(TAG, "onNFCIdRead: tag compatible");
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
                mReadingTagProgressDialog.dismiss();
                mReadingTagProgressDialog = null;
                showSnackbarWithAction(getActivity(), getString(R.string.virgin_tag_detected));
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onTagReadSuccess(String data, ArrayList<Sector> sectors, Sector flagsSector) {
                setAmountReprints(0);
                debugTagData(data, sectors, flagsSector);
                mReadingTagProgressDialog.dismiss();
                mReadingTagProgressDialog = null;
            }

            @Override
            public void onTagReadFailed(MiFareClassicReadStatus status, ArrayList<Sector> sectors) {
                switch (status) {
                    case UKNOWN_KEYS:
                        showSnackbarWithAction(getActivity(), getString(R.string.uknown_keys_message));
                        break;
                    case USER_DATA_IS_EMPTY:
                        showSnackbarWithAction(getActivity(), getString(R.string.empty_tag_message));
                        break;
                    case TAG_REMOVED_WHILE_READING:
                        showSnackbarWithAction(getActivity(), getString(R.string.tag_removed_while_reading_message));
                        break;
                    case TAG_REMOVED_OR_ANY_KEY_INVALID:
                        showSnackbarWithAction(getActivity(), getString(R.string.tag_removed_or_wrong_key));
                        break;
                    case USER_DATA_CORRUPTED:
                        if (mSelectedMaterial == null)
                            showSnackbarWithAction(getActivity(), getString(R.string.no_material_selected));
                        else
                            showSnackbarWithAction(getActivity(), getString(R.string.tag_data_corrupted));
                        break;
                    case NONE_KEY_VALID_FOR_READING:
                        showSnackbarWithAction(getActivity(), getString(R.string.there_is_no_valid_key_message));
                        break;
                }
                mReadingTagProgressDialog.dismiss();
                mReadingTagProgressDialog = null;
            }
        }, DAOKeys.getKeysByVersion(getActivity(), KEYS_VERSION), false);


    }

    public int getAmountReprints() {
        return mAmountReprints;
    }

    public void setAmountReprints(int amountReprints) {
        this.mAmountReprints = amountReprints;
    }
}
