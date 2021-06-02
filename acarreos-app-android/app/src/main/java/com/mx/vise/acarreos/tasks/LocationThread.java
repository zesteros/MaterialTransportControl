package com.mx.vise.acarreos.tasks;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Spinner;

import com.mx.vise.acarreos.R;
import com.mx.vise.acarreos.activities.MainActivity;
import com.mx.vise.acarreos.adapters.CustomArrayAdapter;
import com.mx.vise.acarreos.adapters.GenericAdapter;
import com.mx.vise.acarreos.dao.DAODistances;
import com.mx.vise.acarreos.dao.DAOMaterials;
import com.mx.vise.acarreos.dao.DAOPoints;
import com.mx.vise.acarreos.pojos.DistancePOJO;
import com.mx.vise.acarreos.pojos.MaterialsPOJO;
import com.mx.vise.acarreos.pojos.PointPOJO;
import com.mx.vise.acarreos.singleton.SingletonGlobal;
import com.mx.vise.acarreos.util.ModuleType;
import com.mx.vise.acarreos.util.gps.MapCalculator;
import com.mx.vise.acarreos.util.gps.OnNearbyPointFoundListener;
import com.mx.vise.androiduihelper.searchablespinner.SearchableSpinner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mx.vise.acarreos.fragments.Constants.CARRIES_FRAGMENT_TAG;
import static com.mx.vise.acarreos.fragments.Constants.FREE_CARRY_FRAGMENT_TAG;
import static com.mx.vise.acarreos.fragments.Constants.INTERN_CARRY_FRAGMENT_TAG;
import static com.mx.vise.acarreos.fragments.Constants.MATERIAL_REQUEST_FRAGMENT_TAG;
import static com.mx.vise.acarreos.fragments.Constants.ROAD_IMPROVEMENT_FRAGMENT_TAG;
import static com.mx.vise.acarreos.fragments.Constants.ROYALTIES_FRAGMENT_TAG;
import static com.mx.vise.acarreos.fragments.Constants.SUPPLIES_FRAGMENT_TAG;
import static com.mx.vise.acarreos.fragments.TicketFragment.REPLACE_CHAR;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * com.mx.vise.acarreos.util.gps
 * Creado por Angelo el viernes 25 de enero del 2019 a las 03:29 PM
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreos_app-android
 */
public class LocationThread extends Thread {

    private static final String TAG = "vise";
    private final Context mContext;

    private boolean mKeepRunning;

    public final static long REFRESH_EVERY = 2000;

    public static final int BANK_TYPE = 1;
    public static final int THROW_TYPE = 2;
    public static final int WASTE_TYPE = 3;
    public static final int ALL_POINT_TYPE = 4;
    public static final int BANK_AND_WASTE_TYPE = 5;


    private String THROW_FRAGMENTS[] = {
            CARRIES_FRAGMENT_TAG,
            SUPPLIES_FRAGMENT_TAG,
            FREE_CARRY_FRAGMENT_TAG,
            INTERN_CARRY_FRAGMENT_TAG,
            MATERIAL_REQUEST_FRAGMENT_TAG
    };

    public LocationThread(Context context) {
        this.mContext = context;
    }

    /**
     * hilo
     */
    @Override
    public void run() {
        /*
         * Se para el hilo cuando la app se pone en pausa
         * */
        while (keepRunning()) {
            /*
             * Obtiene la sesión actual
             * */
            if (SingletonGlobal.getInstance().getSession() != null) {

                if (SingletonGlobal.getInstance().getSession().getAssignedBuilding() != null) {
                    /*
                     * Determina los puntos acorde el punto donde se esta parado
                     * */
                    String actualBuilding = SingletonGlobal.getInstance().getSession().getAssignedBuilding().getBuildingId();
                    if (actualBuilding != null) {
                        isInPoint(actualBuilding);
                    }
                }
            }
            /*
             * Refresca el hilo cada 2 segundos
             * */
            try {
                Thread.sleep(REFRESH_EVERY);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }


    private void isInPoint(String actualBuilding) {
        /*
         * Obtiene los bancos
         * */

        /*
         * Obtiene todos los puntos
         * */
        ArrayList<PointPOJO> points = DAOPoints.getAllPoints(mContext, actualBuilding, ALL_POINT_TYPE);

        BigDecimal latitude = SingletonGlobal.getInstance().getGpsTracker().latitude;
        BigDecimal longitude = SingletonGlobal.getInstance().getGpsTracker().longitude;
        String coordinatesValue =
                latitude != null && longitude != null ?
                        MapCalculator.round(latitude.floatValue(), 6) + "," +
                                MapCalculator.round(longitude.floatValue(), 6) :
                        "N/A";


        final String coordinates = coordinatesValue;


        /*
         *
         * Si esta localizado en algun punto ya sea banco o tiro
         *
         * */
        if (MapCalculator.isLocatedInThisPoints(mContext, points, new OnNearbyPointFoundListener() {
            @Override
            public void onNearbyLocationDetected(PointPOJO actualNearbyPoint, double distance) {

                /*
                 * Si esta autorizado
                 * */
                if (actualNearbyPoint.getAutorizado() == 1) {
                    /*
                     * Establece el punto actual global
                     * */
                    SingletonGlobal.getInstance().setActualPoint(actualNearbyPoint);
                    /*
                     * Si el tipo es 1 (banco) o tipo 3 (banco de desperdicio) entonces..
                     * */
                    if (actualNearbyPoint.getTipoPunto() == BANK_TYPE || actualNearbyPoint.getTipoPunto() == WASTE_TYPE) {

                        /*
                         * Si es banco y tiro habilita los modulos de banco.
                         * */
                        if (actualNearbyPoint.getEsBancoYTiro() != 1)
                            disableThrowModules();
                        else if (actualNearbyPoint.getEsBancoYTiro() == 1) {
                            enableThrowModules();
                        }

                        if (SingletonGlobal.getInstance().getActualLocationTextView() != null) {
                            SingletonGlobal.getInstance().getActualLocationTextView()
                                    .setText(
                                            mContext.getString(R.string.last_location).replace("%",
                                                    actualNearbyPoint
                                                            .getNombreBanco() +
                                                            "\nDistancia al punto: " +
                                                            String.format("%.2f", distance) +
                                                            " mts"
                                            ));
                        }
                        if (SingletonGlobal.getInstance().getActualCoordinatesTextView() != null) {
                            SingletonGlobal.getInstance().getActualCoordinatesTextView().setText(coordinates);
                        }
                        /*
                         * Habilita los modulos que requieran banco
                         * */
                        enableBankModules();
                        setAvailableMaterialsAndDistances(mContext, actualNearbyPoint);
                    }
                    /*
                     * Si el tipo es 2 (tiro) entonces..
                     * */
                    else if (actualNearbyPoint.getTipoPunto() == THROW_TYPE) {
                        if (actualNearbyPoint.getEsBancoYTiro() != 1)
                            disableBankModules();
                        else if (actualNearbyPoint.getEsBancoYTiro() == 1) {
                            enableBankModules();
                        }
                        /*Habilita los modulos de tiro*/

                        if (SingletonGlobal.getInstance().getActualLocationTextView() != null) {


                            String pointName =
                                    actualNearbyPoint.getTipoPunto() == BANK_TYPE ||
                                            actualNearbyPoint.getTipoPunto() == WASTE_TYPE ?
                                            actualNearbyPoint.getNombreBanco() :
                                            actualNearbyPoint.getCadenamiento();

                            SingletonGlobal.getInstance().getActualLocationTextView()
                                    .setText(
                                            mContext.getString(R.string.last_location).replace(REPLACE_CHAR,
                                                    pointName +
                                                            "\nDistancia al punto: " +
                                                            String.format("%.2f", distance) + " mts")
                                    );

                            if (SingletonGlobal.getInstance().getNavigationView() != null)
                                SingletonGlobal.getInstance().getNavigationView().invalidate();
                        }

                        if (SingletonGlobal.getInstance().getActualCoordinatesTextView() != null) {
                            SingletonGlobal.getInstance().getActualCoordinatesTextView().setText(coordinates);
                        }

                        enableThrowModules();

                        setAvailableMaterialsAndDistances(mContext, actualNearbyPoint);
                    }
                } else {
                    disableAllUI(coordinates);
                }

            }

            /**
             * Si no se detecto un punto cercano deshabilita todos los modulos
             */
            @Override
            public void onNearbyLocationNotDetected() {
                disableAllUI(coordinates);
            }
        })) {

        } else {
            if (mContext instanceof AppCompatActivity)
                ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SingletonGlobal.getInstance().setActualPoint(null);
                        disableBankModules();
                        disableThrowModules();
                    }
                });
        }
        ;


    }

    private void disableAllUI(String coordinates) {
        ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (SingletonGlobal.getInstance().getActualLocationTextView() != null) {
                    SingletonGlobal.getInstance().getActualLocationTextView()
                            .setText(mContext.getString(R.string.last_location)
                                    .replace("%", "N/A"));
                }
                if (SingletonGlobal.getInstance().getActualCoordinatesTextView() != null) {
                    SingletonGlobal.getInstance().getActualCoordinatesTextView().setText(coordinates);
                }
                if (SingletonGlobal.getInstance().getNavigationView() != null)
                    SingletonGlobal.getInstance().getNavigationView().invalidate();
            }
        });
        SingletonGlobal.getInstance().setActualPoint(null);
        disableBankModules();
        disableThrowModules();
    }

    private void disableThrowModules() {
        handleModules(THROW_FRAGMENTS, THROW_TYPE, false);
    }

    private void enableThrowModules() {
        handleModules(THROW_FRAGMENTS, THROW_TYPE, true);
    }

    /**
     * @param context           el contexto
     * @param actualNearbyPoint el punto
     */
    private void setAvailableMaterialsAndDistances(final Context context, final PointPOJO actualNearbyPoint) {
        new Thread(() -> {
            Integer idDistancePoint = SingletonGlobal.getInstance().getPointSelected();

            final ArrayList<MaterialsPOJO> availableMaterials = DAOMaterials.getAvailableMaterials(context, idDistancePoint);

            final ArrayList<DistancePOJO> availableDistances = DAODistances.getAvailableDistances(context, idDistancePoint);


            changeDataSpinner(context,
                    SingletonGlobal.getInstance().getMaterialAvailableSpinner(),
                    SingletonGlobal.getInstance().getAvailableMaterials(),
                    parseMaterialListToAdapter(availableMaterials));

            changeDataSpinner(context,
                    SingletonGlobal.getInstance().getDistancesSpinner(),
                    SingletonGlobal.getInstance().getAvailableDistances(),
                    parseDistanceListToAdapter(availableDistances));


        }).start();


    }

    /**
     * @param availableDistances las distancias disponibles
     * @return la lista para mostrar al usuario
     */
    private List<GenericAdapter> parseDistanceListToAdapter(ArrayList<DistancePOJO> availableDistances) {
        List<GenericAdapter> distances = new ArrayList<>();
        for (DistancePOJO distance : availableDistances) {

            PointPOJO pointPOJO = DAOPoints.getPointById(mContext, distance.getIdPoint());

            GenericAdapter genericAdapter = new GenericAdapter();
            String text = distance.getDistance() + " KM - " + pointPOJO.getNombreBanco();
            String value = String.valueOf(distance.getDistance());

            genericAdapter.setText(text);
            genericAdapter.setValue(value);

            distances.add(genericAdapter);
        }
        Collections.sort(distances, (object1, object2) -> object1.getText().compareTo(object2.getText()));
        return distances;
    }

    public void changeDataSpinner(Context context, Spinner spinner,
                                  final CustomArrayAdapter<GenericAdapter> adapter,
                                  final List<GenericAdapter> newData) {
        if (spinner != null) {
            if (adapter != null) {
                if (!newData.isEmpty()) {
                    ((AppCompatActivity) context).runOnUiThread(() -> {
                        adapter.clear();
                        adapter.addAll(newData);
                        adapter.notifyDataSetChanged();
                        if (spinner instanceof SearchableSpinner) {
                            ((SearchableSpinner) spinner).setTitle(context.getString(R.string.select_an_option));
                            ((SearchableSpinner) spinner).setPositiveButton(context.getString(R.string.accept));
                            ((SearchableSpinner) spinner)
                                    .setOnSearchableItemCreatedListener(o -> ((GenericAdapter) o).getText());

                        }
                    });
                }
            }
        }
    }

    public List<GenericAdapter> parseMaterialListToAdapter(List<MaterialsPOJO> materialsPOJO) {

        List<GenericAdapter> list = new ArrayList<>();

        for (MaterialsPOJO materials : materialsPOJO) {
            GenericAdapter genericAdapter = new GenericAdapter();


            String description = materials.getDescription() != null ? materials.getDescription() : "";
            genericAdapter.setText(materials.getIdMaterialNavision() + " - " + description);
            genericAdapter.setValue(String.valueOf(materials.getIdMaterialServer()));

            list.add(genericAdapter);
        }

        Collections.sort(list, (object1, object2) -> object1.getText().compareTo(object2.getText()));

        return list;
    }


    private void enableBankModules() {
        handleModules(new String[]{ROAD_IMPROVEMENT_FRAGMENT_TAG, ROYALTIES_FRAGMENT_TAG}, BANK_TYPE, true);
    }

    private void disableBankModules() {
        handleModules(new String[]{ROAD_IMPROVEMENT_FRAGMENT_TAG, ROYALTIES_FRAGMENT_TAG}, BANK_TYPE, false);
    }

    private void handleModules(final String[] fragmentTags, final int moduleType, final boolean visible) {

        ((AppCompatActivity) mContext).runOnUiThread(() -> {
            HashMap<ModuleType, MenuItem> menus = SingletonGlobal.getInstance().getMenus();

            if (menus != null) {
                /*
                 * Oculta menu de regalias y mejoramiento de caminos
                 * */
                if (!menus.isEmpty()) {
                    for (Map.Entry<ModuleType, MenuItem> entry : menus.entrySet()) {
                        ModuleType key = entry.getKey();
                        MenuItem value = entry.getValue();
                        if (key.getType() == moduleType) {
                            value.setVisible(visible);
                        }
                    }

                    /*
                     * Recarga barra de navegacion lateral
                     * */
                    SingletonGlobal.getInstance().getNavigationView().invalidate();
                    /*
                     * Oculta los fragments si estan visibles
                     * */
                    if (!visible)
                        if (mContext instanceof MainActivity) {
                            if (SingletonGlobal.getInstance().isTicketWasPrinted())
                                hideFragments(fragmentTags);
                        }
                }
            }
        });

    }


    private void hideFragments(String[] tags) {
        FragmentManager fragmentManager = ((MainActivity) mContext).getSupportFragmentManager();
        for (int i = 0; i < tags.length; i++) {
            Fragment fragment = fragmentManager.findFragmentByTag(tags[i]);
            if (fragment != null) {
                if (fragment.isVisible()) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.hide(fragment).remove(fragment).commitAllowingStateLoss();
                    ((MainActivity) mContext).setTitle(R.string.app_name);
                }
            }
        }

    }

    private boolean keepRunning() {
        return mKeepRunning;
    }

    public void keepRunning(boolean mKeepRunning) {
        this.mKeepRunning = mKeepRunning;
    }


}
