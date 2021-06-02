package com.mx.vise.acarreos.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.mx.vise.acarreos.R;
import com.mx.vise.acarreos.dao.DAODistances;
import com.mx.vise.acarreos.dao.DAOKeys;
import com.mx.vise.acarreos.dao.DAOMaterials;
import com.mx.vise.acarreos.dao.DAOPoints;
import com.mx.vise.acarreos.dao.DAOReprints;
import com.mx.vise.acarreos.dao.DAOTicket;
import com.mx.vise.acarreos.pojos.CancelTicketPOJO;
import com.mx.vise.acarreos.pojos.DistancePOJO;
import com.mx.vise.nfc.pojos.KeyPOJO;
import com.mx.vise.acarreos.pojos.MaterialsByPointPOJO;
import com.mx.vise.acarreos.pojos.MaterialsPOJO;
import com.mx.vise.acarreos.pojos.PointPOJO;
import com.mx.vise.acarreos.pojos.ReprintPOJO;
import com.mx.vise.acarreos.pojos.SyncPOJO;
import com.mx.vise.acarreos.pojos.SyncRequestPOJO;
import com.mx.vise.acarreos.pojos.SyncResponsePOJO;
import com.mx.vise.acarreos.pojos.TagPOJO;
import com.mx.vise.acarreos.pojos.TicketPOJO;
import com.mx.vise.acarreos.singleton.Singleton;
import com.mx.vise.acarreos.util.gps.GPSTracker;
import com.mx.vise.acarreos.util.gps.LatLng;
import com.mx.vise.acarreos.singleton.SingletonGlobal;
import com.mx.vise.acarreos.webservice.WebServiceConstants;
import com.mx.vise.androidwscon.webservice.ConWs;
import com.mx.vise.login.pojos.EmployeePojo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;

import java.util.ArrayList;
import java.util.List;


/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por aloza el lunes 24 de septiembre del 2018
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreosandroid
 */

public class SyncData extends AsyncTask<EmployeePojo, Progress, SyncStatus> implements OnProgressUpdate {


    private static final String DOWN_STATUS = "B";
    private Context context;
    private Preference mPreference;
    public ProgressDialog mProgressDialog;
    private String mImei;
    public static final String TAG = "VISE";
    private OnSyncListener mSyncListener;
    private List<PointPOJO> pointsToSend;
    private List<TicketPOJO> ticketsToSend;
    private List<ReprintPOJO> reprintsToSend;


    public SyncData(Preference preference, Context context, String imei) {
        this.mPreference = preference;
        this.context = context;
        this.mImei = imei;
    }

    public SyncData(Context context, String imei) {
        this.context = context;
        this.mImei = imei;
    }


    @Override
    protected void onPreExecute() {
        // instantiate it within the onCreate method
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(context.getString(R.string.syncing_data));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        super.onPreExecute();
    }

    public void setOnSyncListener(OnSyncListener onSyncListener) {
        this.mSyncListener = onSyncListener;
    }

    @Override
    protected SyncStatus doInBackground(EmployeePojo... employeePojos) {
        /*
         * Busca si hay cubicaciones por enviar
         *
         * */
        SyncStatus syncStatus = new SyncStatus();
        sync(syncStatus);
        return syncStatus;
    }

    /**
     * @param syncStatus el status de sincronizacion
     */
    public void sync(SyncStatus syncStatus) {
        sendPoints(syncStatus);
        sendTickets(false);
        sendTickets(true);
        sendReprints();
        syncMaterialsPointsAndDistances();
    }

    /**
     * @param syncStatus el estado de sincronización
     */
    private void sendPoints(SyncStatus syncStatus) {
        /*
         * Lista de ids de las cubicaciones
         * */
        try {
            /*
             * Obtiene las cubicaciones a enviar
             * */
            pointsToSend = DAOPoints.getPointsToSend(context, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
         * Muestra el progreso de las N cubicaciones que se vayan a subir, inicializalo segun
         * la cantidad de cubicaciones
         * */
        if (pointsToSend == null)
            return;
        int totalElements = pointsToSend.size();
        publishProgress(new Progress(0, 0, totalElements, context.getString(R.string.uploading_points)));
        /*
         *  Envíalas (si las hay)
         * */
        syncStatus.setUploadSuccessful(true);

        int i = 0;//contador de puntos enviados
        for (PointPOJO point : pointsToSend) {

            ConWs<Integer, PointPOJO> con = new ConWs(WebServiceConstants.URL_PUT_POINT, this.mImei);

            ResponseEntity<Integer> responseEntity = con.getObject(point, Integer.class);
            Log.i(TAG, "sendPoints: " + con);

            if (responseEntity != null) {
                Log.i(TAG, "sendPoints: " + responseEntity);
                if (responseEntity.getStatusCode().value() == 200) {
                    if (responseEntity.getBody() != null) {


                        /*
                         * Agrega los puntos enviados
                         * */
                        DAOPoints.changePointsStatus(context, "B", point.getIdPuntoLocal(), responseEntity.getBody().longValue());
                    }
                } else if (responseEntity.getStatusCode().value() == 204) {
                }
            }
            i++;
            publishProgress(i * 100 / totalElements, i, totalElements);

        }
    }

    private void syncMaterialsPointsAndDistances() {
        /*
         * Lista de ids de las cubicaciones
         * */
        try {

            String assignedBuilding = SingletonGlobal
                    .getInstance()
                    .getSession()
                    .getAssignedBuilding()
                    .getBuildingId();
            /*
             * Obtiene los puntos a enviar
             * */
            List<PointPOJO> actualPoints = DAOPoints
                    .getActualPoints(context, this);
            /*
             * Obtiene los materiales a enviar
             * */
            List<MaterialsPOJO> actualMaterials = DAOMaterials
                    .getActualMaterials(context, this);
            /*
             * Obtiene los materiales por punto a enviar
             * */
            List<MaterialsByPointPOJO> actualMaterialsByPoint = DAOMaterials
                    .getActualMaterialsByPoint(context, this);

            List<KeyPOJO> actualKeys = DAOKeys.getActualKeys(context, this);

            List<DistancePOJO> actualDistances = DAODistances
                    .getActualDistances(context, this);

            SyncRequestPOJO syncRequestPOJO = new SyncRequestPOJO();
            syncRequestPOJO.setPointsAlreadyRegistered(actualPoints);
            syncRequestPOJO.setMaterialsAlreadyRegistered(actualMaterials);
            syncRequestPOJO.setMaterialsByPointRegistered(actualMaterialsByPoint);
            syncRequestPOJO.setDistancesRegistered(actualDistances);
            syncRequestPOJO.setKeysRegistered(actualKeys);
            syncRequestPOJO.setBuilding(assignedBuilding);
            syncRequestPOJO.setSyncData(getSyncData());
            /*recibe envia*/
            ConWs<SyncResponsePOJO, SyncRequestPOJO> con = new ConWs(WebServiceConstants.URL_SYNC_POINTS, this.mImei);
            ResponseEntity<SyncResponsePOJO> responseEntity = null;
            try {
                responseEntity = con.getObject(syncRequestPOJO, SyncResponsePOJO.class);
            } catch (ResourceAccessException e) {

            } catch (Exception ex) {

            }
            if (responseEntity != null) {
                if (responseEntity.getStatusCode().value() == 200) {
                    if (responseEntity.getBody() != null) {

                        SyncResponsePOJO pointSyncPOJO = responseEntity.getBody();
                        addNewData(pointSyncPOJO);

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @return los datos de sincronizacion
     */
    private SyncPOJO getSyncData() {
        SyncPOJO syncPOJO = new SyncPOJO();
        String imei = Singleton.getInstance().getImei() != null ? Singleton.getInstance().getImei() : "0";
        syncPOJO.setImei(imei);
        LatLng latLng = new LatLng();
        if (SingletonGlobal.getInstance().getGpsTracker() != null) {
            GPSTracker gpsTracker = SingletonGlobal.getInstance().getGpsTracker();
            latLng.longitude = gpsTracker.longitude;
            latLng.latitude = gpsTracker.latitude;
        }

        String coordinates = latLng.latitude != null ? latLng.latitude.floatValue() + "," + latLng.longitude.floatValue() : "0,0";
        syncPOJO.setLocation(coordinates);

        int userid = SingletonGlobal.getInstance().getSession() != null ? SingletonGlobal.getInstance().getSession().getEmployeeId() : 0;

        syncPOJO.setUserId(userid);

        return syncPOJO;
    }

    /**
     * envia las reimpresiones
     */
    private void sendReprints() {
        /*
         * Lista de ids de las cubicaciones
         * */
        try {
            /*
             * Obtiene las cubicaciones a enviar
             * */
            reprintsToSend = DAOReprints.getReprintsToSend(context, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (reprintsToSend == null) return;
        /*
         * Muestra el progreso de las N cubicaciones que se vayan a subir, inicializalo segun
         * la cantidad de cubicaciones
         * */
        int totalElements = reprintsToSend.isEmpty() ? 0 : reprintsToSend.size();

        //Log.i(TAG, "doInBackground: Procesando reimpresiones");
        publishProgress(new Progress(0, 0, totalElements, "Subiendo reimpresiones, por favor espere..."));
        /*
         *  Envíalas (si las hay)
         * */
        int i = 0;//contador de puntos enviados
        for (ReprintPOJO reprint : reprintsToSend) {

            ConWs<Integer, ReprintPOJO> con = new ConWs(WebServiceConstants.URL_PUT_REPRINT, this.mImei);
            ResponseEntity<Integer> responseEntity = null;
            try {
                responseEntity = con.getObject(reprint, Integer.class);
            } catch (Exception e) {

            }

            if (responseEntity != null) {
                if (responseEntity.getStatusCode().value() == 200) {
                    if (responseEntity.getBody() != null) {


                        /*
                         * Agrega los puntos enviados
                         * */
                        DAOReprints.changeReprintStatus(context, "B", reprint.getIdReprintLocal(), responseEntity.getBody());


                    }
                } else if (responseEntity.getStatusCode().value() == 204) {

                }
            }
            i++;
            publishProgress(i * 100 / totalElements, i, totalElements);

        }
    }

    /**
     * Envia los boletos
     */
    private void sendTickets(boolean sendCanceled) {
        /*
         * Lista de ids de las cubicaciones
         * */
        try {
            /*
             * Obtiene las cubicaciones a enviar
             * */
            ticketsToSend =
                    !sendCanceled ? DAOTicket.getTicketsToSend(context, this) :
                            DAOTicket.getTicketsCanceledToSend(context, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
         * Muestra el progreso de las N cubicaciones que se vayan a subir, inicializalo segun
         * la cantidad de cubicaciones
         * */
        if (ticketsToSend == null) return;
        int totalElements = ticketsToSend.isEmpty() ? 0 : ticketsToSend.size();

        //Log.i(TAG, "doInBackground: Procesando boletos");

        publishProgress(new Progress(0, 0,
                totalElements, context.getString(R.string.uploading_tickets)));
        /*
         *  Envíalas (si las hay)
         * */
        int i = 0;//contador de puntos enviados

        for (TicketPOJO ticket : ticketsToSend) {

            String url = sendCanceled ?
                    WebServiceConstants.URL_PUT_CANCELED_TICKET :
                    WebServiceConstants.URL_PUT_TICKET;

            ResponseEntity<Integer> responseEntity = null;

            if (!sendCanceled) {

                ConWs<Integer, TicketPOJO> con = new ConWs(url, this.mImei);

                responseEntity = con.getObject(ticket, Integer.class);

            } else {
                ConWs<Integer, CancelTicketPOJO> con = new ConWs(url, this.mImei);

                CancelTicketPOJO cancelTicketPOJO = new CancelTicketPOJO();
                cancelTicketPOJO.setTicketPOJO(ticket);
                cancelTicketPOJO.setIdEmpleado(SingletonGlobal.getInstance().getSession().getEmployeeId());

                responseEntity = con.getObject(cancelTicketPOJO, Integer.class);


            }

            if (responseEntity != null) {
                if (responseEntity.getStatusCode().value() == 200) {
                    if (responseEntity.getBody() != null) {
                        /*
                         * Cambia el status del boleto ya entregado al servidor
                         * */
                        DAOTicket.changeTicketStatus(context, DOWN_STATUS, ticket.getIdTicket(), Long.valueOf(responseEntity.getBody()), sendCanceled);
                    }
                } else if (responseEntity.getStatusCode().value() == 204) {
                    Log.i(TAG, "sendTickets: error al enviar el boleto");
                }
            }
            i++;
            publishProgress(i * 100 / totalElements, i, totalElements);


        }
    }

    private void addNewData(SyncResponsePOJO pointSyncPOJO) {

        ArrayList<PointPOJO> pendingPoints = pointSyncPOJO.getNewPoints();

        ArrayList<PointPOJO> updatedPoints = pointSyncPOJO.getPointsChanged();

        ArrayList<MaterialsPOJO> newMaterials = pointSyncPOJO.getNewMaterials();

        ArrayList<MaterialsPOJO> updatedMaterials = pointSyncPOJO.getMaterialsChanged();

        ArrayList<MaterialsByPointPOJO> newMaterialsByPoint = pointSyncPOJO.getNewMaterialsByPoint();

        ArrayList<MaterialsByPointPOJO> updatedMaterialsByPoint = pointSyncPOJO.getMaterialsByPointChanged();

        ArrayList<DistancePOJO> newDistances = pointSyncPOJO.getNewDistances();

        ArrayList<DistancePOJO> updatedDistances = pointSyncPOJO.getDistancesChanged();

        ArrayList<KeyPOJO> newKeys = pointSyncPOJO.getNewKeys();

        ArrayList<KeyPOJO> updatedKeys = pointSyncPOJO.getKeysChanged();

        DAOPoints.addPendingPoints(pendingPoints, context, this);

        DAOPoints.updatePoints(updatedPoints, context, this);

        DAOMaterials.addPendingMaterials(newMaterials, context, this);

        DAOMaterials.updateMaterials(updatedMaterials, context, this);

        DAOMaterials.addPendingMaterialsByPoint(newMaterialsByPoint, context, this);

        DAOMaterials.updateMaterialsByPoint(updatedMaterialsByPoint, context, this);

        DAODistances.addPendingDistances(newDistances, context, this);

        DAODistances.updateDistances(updatedDistances, context, this);

        DAOKeys.addPendingKeys(newKeys, context, this);

        DAOKeys.updateKeys(updatedKeys, context, this);

        //DAOTags.addTags(context, tags, this);

    }


    @Override
    protected void onPostExecute(SyncStatus syncSuccessful) {
        mProgressDialog.dismiss();
        if (mPreference != null)
            mPreference.setSummary("Actualizado hace 1 minuto");
        if (syncSuccessful.isDownloadSuccessful() && syncSuccessful.isUploadSuccessful()) {
            if (mSyncListener != null)
                mSyncListener.onSyncSuccessful();
        } else {
            if (mSyncListener != null)
                mSyncListener.onSyncFailed(syncSuccessful);
        }
    }

    @Override
    protected void onProgressUpdate(final Progress... values) {
        if (mProgressDialog != null) {
            mProgressDialog.setIndeterminate(false);
            if (values[0].getText() != null) {
                if (context instanceof PreferenceActivity) {
                    ((PreferenceActivity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog.setMessage(values[0].getText());
                        }
                    });
                } else if (context instanceof AppCompatActivity) {
                    ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog.setMessage(values[0].getText());
                        }
                    });
                }
            }
            mProgressDialog.setProgress(values[0].getProgress());
            mProgressDialog.setSecondaryProgress(values[0].getSecondaryProgress());
            mProgressDialog.setMax(values[0].getTotalElements());
        }
    }

    @Override
    public void publishProgress(int progress, int secondaryProgress, int totalElements) {
        onProgressUpdate(new Progress[]{new Progress(progress, secondaryProgress, totalElements)});
    }

    @Override
    public void publishProgress(Progress progress) {
        onProgressUpdate(new Progress[]{progress});
    }
}
