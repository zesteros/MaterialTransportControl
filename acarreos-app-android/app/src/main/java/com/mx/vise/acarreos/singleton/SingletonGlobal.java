package com.mx.vise.acarreos.singleton;

import com.google.android.material.navigation.NavigationView;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.TextView;

import com.mx.vise.acarreos.adapters.CustomArrayAdapter;
import com.mx.vise.acarreos.adapters.GenericAdapter;
import com.mx.vise.acarreos.pojos.PointPOJO;
import com.mx.vise.acarreos.util.ModuleType;
import com.mx.vise.acarreos.tasks.LocationThread;
import com.mx.vise.acarreos.util.gps.GPSTracker;
import com.mx.vise.login.pojos.EmployeePojo;

import java.util.Date;
import java.util.HashMap;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * com.mx.vise.acarreos.util.gps
 * Creado por Angelo el viernes 25 de enero del 2019 a las 03:34 PM
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreos_app-android
 */
public class SingletonGlobal {

    private static SingletonGlobal sInstance;
    private GPSTracker mGpsTracker;
    private LocationThread mCarriesThread;
    private HashMap<ModuleType, MenuItem> menus;
    private EmployeePojo session;
    private NavigationView navigationView;
    private TextView console;
    private PointPOJO actualPoint;
    private CustomArrayAdapter<GenericAdapter> availableMaterials;
    private Spinner materialAvailableSpinner;
    private boolean stopUseDatabase;
    private Spinner distancesSpinner;
    private CustomArrayAdapter<GenericAdapter> availableDistances;
    private boolean ticketWasNotPrinted = true;
    private TextView actualLocationTextView;
    private TextView actualCoordinatesTextView;
    private boolean isViewTagDataFragment;
    private Date gpsTime;
    private Date NTPTime;
    private int actualPointType;
    private Integer destinySelected;

    public static SingletonGlobal getInstance(){
        return sInstance == null ? sInstance = new SingletonGlobal() : sInstance;
    }

    public GPSTracker getGpsTracker(){
        return mGpsTracker;
    }

    public void setGpsTracker(GPSTracker mGpsTracker) {
        this.mGpsTracker = mGpsTracker;
    }

    public LocationThread getCarriesThread(){
        return mCarriesThread;
    }

    public void setCarriesThread(LocationThread mCarriesThread) {
        this.mCarriesThread = mCarriesThread;
    }


    public void setMenus(HashMap<ModuleType, MenuItem> menus) {
        this.menus = menus;
    }

    public HashMap<ModuleType, MenuItem> getMenus() {
        return menus;
    }

    public void setSession(EmployeePojo mEmployeePojo) {
        this.session = mEmployeePojo;
    }

    public EmployeePojo getSession() {
        return session;
    }

    public void setNavigationView(NavigationView navView) {
        this.navigationView = navView;
    }

    public NavigationView getNavigationView() {
        return navigationView;
    }

    public void setActualPoint(PointPOJO actualPoint) {
        this.actualPoint = actualPoint;
    }

    public PointPOJO getActualPoint() {
        return actualPoint;
    }

    public void setAvailableMaterials(CustomArrayAdapter<GenericAdapter> materialsPOJOS) {
        this.availableMaterials = materialsPOJOS;
    }

    public CustomArrayAdapter<GenericAdapter> getAvailableMaterials() {
        return availableMaterials;
    }

    public void setMaterialAvailableSpinner(Spinner materialsSpinner) {
        this.materialAvailableSpinner = materialsSpinner;
    }

    public Spinner getMaterialAvailableSpinner() {
        return materialAvailableSpinner;
    }


    public void setDistancesSpinner(Spinner distancesSpinner) {
        this.distancesSpinner = distancesSpinner;
    }

    public Spinner getDistancesSpinner() {
        return distancesSpinner;
    }

    public void setAvailableDistances(CustomArrayAdapter<GenericAdapter> distancesAdapter) {
        this.availableDistances = distancesAdapter;
    }

    public CustomArrayAdapter<GenericAdapter> getAvailableDistances() {
        return availableDistances;
    }

    public boolean isTicketWasPrinted() {
        return ticketWasNotPrinted;
    }

    public void setTicketWasPrinted(boolean ticketWasNotPrinted) {
        this.ticketWasNotPrinted = ticketWasNotPrinted;
    }

    public void setActualLocationTextView(TextView actualLocationTextView) {
        this.actualLocationTextView = actualLocationTextView;
    }

    public TextView getActualLocationTextView() {
        return actualLocationTextView;
    }

    public void setActualCoordinatesTextView(TextView actualCoordinatesTextView) {
        this.actualCoordinatesTextView = actualCoordinatesTextView;
    }

    public TextView getActualCoordinatesTextView() {
        return actualCoordinatesTextView;
    }

    public void isViewTagDataFragment(boolean isViewTagDataFragment) {
        this.isViewTagDataFragment = isViewTagDataFragment;
    }

    public boolean isViewTagDataFragment() {
        return isViewTagDataFragment;
    }


    public Date getGpsTime() {
        return gpsTime;
    }

    public void setGpsTime(Date gpsTime) {
        this.gpsTime = gpsTime;
    }

    public void setNTPTime(Date NTPtime) {
        this.NTPTime = NTPtime;
    }

    public Date getNTPTime() {
        return NTPTime;
    }

    public int getActualPointType() {
        return actualPointType;
    }

    public void setActualPointType(int actualPointType) {
        this.actualPointType = actualPointType;
    }

    public void setPointSelected(Integer value) {
        this.destinySelected = value;
    }

    public Integer getPointSelected() {
        return destinySelected;
    }
}
