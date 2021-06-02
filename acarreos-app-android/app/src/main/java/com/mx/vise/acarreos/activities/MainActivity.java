package com.mx.vise.acarreos.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;

import android.text.InputType;
import android.util.Log;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mx.vise.acarreos.R;
import com.mx.vise.acarreos.fragments.CancelTicketFragment;
import com.mx.vise.acarreos.fragments.TicketFragment;
import com.mx.vise.acarreos.fragments.PointsRegisterFragment;
import com.mx.vise.acarreos.fragments.TagDataPagerFragment;
import com.mx.vise.acarreos.pojos.RequestDeviceRegisterPOJO;
import com.mx.vise.acarreos.singleton.Singleton;
import com.mx.vise.acarreos.tasks.OnBarcodeDetectedListener;
import com.mx.vise.acarreos.tasks.OnOperationRunning;
import com.mx.vise.acarreos.tasks.SyncThread;
import com.mx.vise.acarreos.tasks.WaitTask;
import com.mx.vise.acarreos.util.ImeiHelper;
import com.mx.vise.acarreos.util.ModuleType;
import com.mx.vise.acarreos.util.gps.GPSTracker;
import com.mx.vise.acarreos.singleton.SingletonGlobal;
import com.mx.vise.acarreos.tasks.LocationThread;
import com.mx.vise.acarreos.webservice.WebServiceConstants;
import com.mx.vise.androiduihelper.UIHelper;
import com.mx.vise.androidwscon.enums.ObtainStatus;
import com.mx.vise.androidwscon.interfaces.BaseListener;
import com.mx.vise.androidwscon.tasks.BaseTask;
import com.mx.vise.login.asynctasks.ValidateUserTask;
import com.mx.vise.login.interfaces.OnLoginListener;
import com.mx.vise.login.pojos.EmployeePojo;
import com.mx.vise.login.pojos.LoginEmpleado;
import com.mx.vise.login.pojos.ModulePOJO;
import com.mx.vise.login.pojos.ProjectPOJO;
import com.mx.vise.login.pojos.ProjectsByArea;
import com.mx.vise.nfc.NFCHelper;
import com.mx.vise.nfc.NFCIdListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.mx.vise.acarreos.activities.SimpleScannerActivity.BARCODE_EXTRA;
import static com.mx.vise.acarreos.fragments.Constants.CANCEL_TICKET_FRAGMENT;
import static com.mx.vise.acarreos.fragments.Constants.CANCEL_TICKET_SHORTCUT;
import static com.mx.vise.acarreos.fragments.Constants.EXIT_SHORTCUT;
import static com.mx.vise.acarreos.fragments.Constants.MODULE_CANCEL_TICKET;
import static com.mx.vise.acarreos.fragments.Constants.CARRIES_FRAGMENT_TAG;
import static com.mx.vise.acarreos.fragments.Constants.CARRIES_SHORTCUT;
import static com.mx.vise.acarreos.fragments.Constants.CONFIGURE_TAG_FRAGMENT_TAG;
import static com.mx.vise.acarreos.fragments.Constants.CONFIGURE_TAG_SHORTCUT;
import static com.mx.vise.acarreos.fragments.Constants.FREE_CARRY_FRAGMENT_TAG;
import static com.mx.vise.acarreos.fragments.Constants.FREE_CARRY_SHORTCUT;
import static com.mx.vise.acarreos.fragments.Constants.INTERN_CARRY_FRAGMENT_TAG;
import static com.mx.vise.acarreos.fragments.Constants.INTERN_CARRY_SHORTCUT;
import static com.mx.vise.acarreos.fragments.Constants.MATERIAL_REQUEST_FRAGMENT_TAG;
import static com.mx.vise.acarreos.fragments.Constants.MATERIAL_REQUEST_SHORTCUT;
import static com.mx.vise.acarreos.fragments.Constants.MODULE_CARRIES;
import static com.mx.vise.acarreos.fragments.Constants.MODULE_CONFIGURE_TAG;
import static com.mx.vise.acarreos.fragments.Constants.MODULE_FREE_CARRY;
import static com.mx.vise.acarreos.fragments.Constants.MODULE_INTERN_CARRY;
import static com.mx.vise.acarreos.fragments.Constants.MODULE_MATERIAL_REQUEST;
import static com.mx.vise.acarreos.fragments.Constants.MODULE_POINTS_REGISTER;
import static com.mx.vise.acarreos.fragments.Constants.MODULE_ROAD_IMPROVEMENT;
import static com.mx.vise.acarreos.fragments.Constants.MODULE_ROYALTIES;
import static com.mx.vise.acarreos.fragments.Constants.MODULE_SUPPLIES;
import static com.mx.vise.acarreos.fragments.Constants.POINTS_REGISTER_SHORTCUT;
import static com.mx.vise.acarreos.fragments.Constants.POINTS_REGISTER_TAG;
import static com.mx.vise.acarreos.fragments.Constants.ROAD_IMPROVEMENT_FRAGMENT_TAG;
import static com.mx.vise.acarreos.fragments.Constants.ROAD_IMPROVEMENT_SHORTCUT;
import static com.mx.vise.acarreos.fragments.Constants.ROYALTIES_FRAGMENT_TAG;
import static com.mx.vise.acarreos.fragments.Constants.ROYALTIES_SHORTCUT;
import static com.mx.vise.acarreos.fragments.Constants.SUPPLIES_FRAGMENT_TAG;
import static com.mx.vise.acarreos.fragments.Constants.SUPPLIES_SHORTCUT;
import static com.mx.vise.acarreos.fragments.TicketFragment.REPLACE_CHAR;
import static com.mx.vise.acarreos.fragments.TicketType.CARRIES;
import static com.mx.vise.acarreos.fragments.TicketType.FREE_CARRY;
import static com.mx.vise.acarreos.fragments.TicketType.INTERN_CARRY;
import static com.mx.vise.acarreos.fragments.TicketType.MATERIAL_REQUEST;
import static com.mx.vise.acarreos.fragments.TicketType.ROAD_IMPROVEMENT;
import static com.mx.vise.acarreos.fragments.TicketType.ROYALTIES;
import static com.mx.vise.acarreos.fragments.TicketType.SUPPLIES;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LocationListener {
    public static final String SESSION_EXTRA = "VISE";
    private static final String TAG = "VISE";
    private static final int NONE = 0;
    private EmployeePojo mEmployeePojo;
    private GPSTracker mGpsTracker;
    private boolean mDoubleBackToExitPressedOnce;
    private LocationThread mCarriesThread;
    private ExecutorService mExecutor;
    private boolean debug = false;
    private ImageView mBackground;
    private SyncThread mSyncThread;
    private NFCHelper mNfcHelper;
    private NFCIdListener mNfcIdListener;
    public static final int KEYS_VERSION = 0;
    private OnBarcodeDetectedListener mBarcodeListener;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        Singleton.getInstance().setNfcActivated(false);


        mNfcHelper = new NFCHelper(this, mNfcIdListener);
        mNfcHelper.resolveIntent(getIntent());

        mBackground = findViewById(R.id.carryLogo);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Bundle extras = getIntent().getExtras();
        mEmployeePojo = (EmployeePojo) extras.get(SESSION_EXTRA);

        setEmployeePojo(mEmployeePojo);

        if (mEmployeePojo != null) {
            if (mEmployeePojo.getAssignedBuilding() == null) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog
                        .setCancelable(false)
                        .setPositiveButton(R.string.accept, (dialog1, which) -> finish())
                        .setTitle(getString(R.string.no_building))
                        .setMessage(getString(R.string.no_building_msg))
                        .show();
            }
            requestPermissions();

            NavigationView navigationView = findViewById(R.id.nav_view);
            View headerView = navigationView.getHeaderView(0);

            TextView actualLocationTextView = headerView.findViewById(R.id.actualLocationTextView);
            SingletonGlobal.getInstance().setActualLocationTextView(actualLocationTextView);
            TextView actualCoordinatesTextView = headerView.findViewById(R.id.actualCoordinatesTextView);
            SingletonGlobal.getInstance().setActualCoordinatesTextView(actualCoordinatesTextView);
            actualCoordinatesTextView.setOnLongClickListener(v -> {
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    vibrator.vibrate(500);
                }
                String coordinates = ((TextView) v).getText().toString();
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("geo:" + coordinates));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                return true;
            });
            TextView navUsername = headerView.findViewById(R.id.usernameTextView);
            TextView navUserEmail = headerView.findViewById(R.id.emailTextView);
            TextView navBuilding = headerView.findViewById(R.id.buildingTextView);
            TextView appVersion = headerView.findViewById(R.id.appVersion);
            try {
                appVersion.setText(getString(R.string.app_version).replace(REPLACE_CHAR, getPackageManager().getPackageInfo(getPackageName(), 0).versionName));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                /*Si ocurre algun error simplemente haz invisible la versión*/
                appVersion.setVisibility(View.GONE);
            }


            navUsername.setText(mEmployeePojo.getEmployeeName());
            navUserEmail.setText(mEmployeePojo.getEmployeeEmail());
            String assignedBuilding = mEmployeePojo.getAssignedBuilding() != null ? mEmployeePojo.getAssignedBuilding().getBuildingId() : "N/A";
            navBuilding.setText("Obra asignada: " + assignedBuilding);
            navigationView.setNavigationItemSelectedListener(this);


            setGpsTracker(new GPSTracker(this));
            SingletonGlobal.getInstance().setGpsTracker(getGpsTracker());

            SingletonGlobal.getInstance().setSession(mEmployeePojo);

            addMenuItemInNavMenuDrawer();

        } else {
            finish();
        }


    }

    @Override
    protected void onNewIntent(Intent intent) {
        Singleton.getInstance().setNfcActivated(true);
        setIntent(intent);
        mNfcHelper.resolveIntent(intent);
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED

                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED

                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED

                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED

                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED

                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED

                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.CAMERA,
                                Manifest.permission.ACCESS_NETWORK_STATE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        }, 100);

                startImeiHelper();

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            startImeiHelper();
        }
    }

    public void startImeiHelper() {

        ImeiHelper mImeiHelper = new ImeiHelper(this);

        if (mImeiHelper.getImei() != null) {
            Singleton.getInstance().setImei(mImeiHelper.getImei());
            if (mEmployeePojo.getAssignedIMEI() != null) {
                if (!mEmployeePojo.getAssignedIMEI().equals(mImeiHelper.getImei())) {
                    /*AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog
                            .setCancelable(false)
                            .setPositiveButton(R.string.accept, (dialog1, which) -> finish())
                            .setNegativeButton(R.string.request_register, (dialogInterface, i) ->
                                    requestDeviceRegister(mImeiHelper.getImei(), mImeiHelper.getDeviceName()))
                            .setTitle(getString(R.string.no_imei_match))
                            .setMessage(getString(R.string.no_imei_match_msg))
                            .show();*/
                }
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog
                        .setCancelable(false)
                        .setPositiveButton(R.string.accept, (dialog1, which) -> showPlayStore())
                        .setTitle(getString(R.string.no_imei_match_null))
                        .setMessage(getString(R.string.no_imei_match_msg_null))
                        .show();
            }
        }
    }

    private void showPlayStore() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.mx.vise.eflowandroid")));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.mx.vise.eflowandroid")));
        }
        finish();
    }

    /**
     * @param imei       el imei del telefono
     * @param deviceName
     */
    private void requestDeviceRegister(String imei, String deviceName) {

        RequestDeviceRegisterPOJO requestDeviceRegister = new RequestDeviceRegisterPOJO();
        requestDeviceRegister.setIdEmpleado(mEmployeePojo.getEmployeeId());
        requestDeviceRegister.setImei(imei);
        requestDeviceRegister.setDeviceName(deviceName);

        ProgressDialog progressDialog = ProgressDialog.show(this,"Solicitud de registro","Enviando solicitud",true,false);
        new BaseTask<RequestDeviceRegisterPOJO, Boolean>(
                this,
                WebServiceConstants.URL_REQUEST_DEVICE_REGISTER,
                new BaseListener() {
                    @Override
                    public void onDataObtained(Object o) {
                        if ((Boolean) o) {
                            progressDialog.dismiss();
                            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                            dialog
                                    .setCancelable(false)
                                    .setPositiveButton(R.string.accept, (dialog1, which) -> finish())
                                    .setTitle(getString(R.string.requested_success))
                                    .setMessage(getString(R.string.requested_success_msg))
                                    .show();
                            //UIHelper.showSnackbarWithAction(MainActivity.this, "Solicitud enviada correctamente", view -> finish());
                        }
                    }

                    @Override
                    public void onDataObtainFailed(ObtainStatus obtainStatus) {
                        progressDialog.dismiss();
                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                        dialog
                                .setCancelable(false)
                                .setPositiveButton(R.string.accept, (dialog1, which) -> finish())
                                .setTitle(getString(R.string.requested_failed))
                                .setMessage(getString(R.string.request_failed_msg).replace(REPLACE_CHAR, String.valueOf(obtainStatus)))
                                .show();
                    }
                }, Boolean.class).execute(requestDeviceRegister);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private MenuItem getMenuItem(Menu menu, String text, int icon, char shortcut) {
        return menu.add(text).setIcon(getDrawable(icon)).setNumericShortcut(shortcut);
    }

    /**
     * Agrega un elemento en la barra de navegacion
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void addMenuItemInNavMenuDrawer() {
        NavigationView navView = findViewById(R.id.nav_view);

        SingletonGlobal.getInstance().setNavigationView(navView);

        Menu menu = navView.getMenu();

        HashMap<ModuleType, MenuItem> hashMap = new HashMap<>();

        ArrayList<Integer> modules = getCarriesModules(getEmployeePojo());


        if (modules.contains(MODULE_POINTS_REGISTER))
            hashMap.put(new ModuleType(MODULE_POINTS_REGISTER, 0),
                    getMenuItem(menu, getString(R.string.points_register), R.drawable.ic_points_register_2, POINTS_REGISTER_SHORTCUT));
        if (modules.contains(MODULE_ROYALTIES))
            hashMap.put(new ModuleType(MODULE_ROYALTIES, 1),
                    getMenuItem(menu, getString(R.string.royalties), R.drawable.ic_royalties, ROYALTIES_SHORTCUT));
        if (modules.contains(MODULE_ROAD_IMPROVEMENT))
            hashMap.put(new ModuleType(MODULE_ROAD_IMPROVEMENT, 1),
                    getMenuItem(menu, getString(R.string.road_improvement), R.drawable.ic_road_improvement, ROAD_IMPROVEMENT_SHORTCUT));
        if (modules.contains(MODULE_CARRIES))
            hashMap.put(new ModuleType(MODULE_CARRIES, 2),
                    getMenuItem(menu, getString(R.string.carries), R.drawable.ic_carries, CARRIES_SHORTCUT));
        if (modules.contains(MODULE_SUPPLIES))
            hashMap.put(new ModuleType(MODULE_SUPPLIES, 2),
                    getMenuItem(menu, getString(R.string.supplies), R.drawable.ic_supplies, SUPPLIES_SHORTCUT));
        if (modules.contains(MODULE_FREE_CARRY))
            hashMap.put(new ModuleType(MODULE_FREE_CARRY, 2),
                    getMenuItem(menu, getString(R.string.free_carry), R.drawable.ic_free_carry, FREE_CARRY_SHORTCUT));
        if (modules.contains(MODULE_INTERN_CARRY))
            hashMap.put(new ModuleType(MODULE_INTERN_CARRY, 2),
                    getMenuItem(menu, getString(R.string.intern_carry), R.drawable.ic_intern_carry, INTERN_CARRY_SHORTCUT));
        if (modules.contains(MODULE_MATERIAL_REQUEST))
            hashMap.put(new ModuleType(MODULE_MATERIAL_REQUEST, 2),
                    getMenuItem(menu, getString(R.string.material_request), R.drawable.ic_material_request, MATERIAL_REQUEST_SHORTCUT));
        if (modules.contains(MODULE_CONFIGURE_TAG))
            hashMap.put(new ModuleType(MODULE_CONFIGURE_TAG, 0),
                    getMenuItem(menu, getString(R.string.configure_tag), R.drawable.ic_tag_grey_light, CONFIGURE_TAG_SHORTCUT));
        if (modules.contains(MODULE_CANCEL_TICKET))
            hashMap.put(new ModuleType(MODULE_CANCEL_TICKET, 0),
                    getMenuItem(menu, getString(R.string.cancel_ticket), R.drawable.ic_cancel_ticket, CANCEL_TICKET_SHORTCUT));


        hashMap.put(new ModuleType(NONE, 0),
                getMenuItem(menu, getString(R.string.exit), R.drawable.ic_exit, EXIT_SHORTCUT));


        if (hashMap.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            })
                    .setMessage(R.string.did_not_count_with_permissions)
                    .setTitle(getString(R.string.no_permissions))
                    .setCancelable(false)
                    .show();
        }
        SingletonGlobal.getInstance().setMenus(hashMap);

        navView.invalidate();
    }

    private ArrayList<Integer> getCarriesModules(EmployeePojo employeePojo) {
        ArrayList modules = new ArrayList<>();

        for (ProjectsByArea projects : employeePojo.getPermissions()) {

            for (ProjectPOJO projectPOJO : projects.getProjects()) {
                if (projectPOJO.getProjectName().equals(getString(R.string.carries))) {
                    for (ModulePOJO modulePOJO : projectPOJO.getModules()) {
                        modules.add(modulePOJO.getIdModule());
                    }
                }

            }
        }
        return modules;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (SingletonGlobal.getInstance().isTicketWasPrinted()) {

            mBackground.setAlpha(0.1f);

            Fragment actualFragment = getActualFragment();

            manageClick(item, actualFragment, fragmentManager);
        } else {
            UIHelper.showSnackbarWithAction(this, getString(R.string.ticket_was_not_printed));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    public void manageClick(MenuItem item, Fragment actualFragment, FragmentManager fragmentManager) {
        switch (item.getNumericShortcut()) {
            case POINTS_REGISTER_SHORTCUT:
                setTitle(R.string.points_register);
                PointsRegisterFragment pointsRegisterFragment = new PointsRegisterFragment();
                fragmentManager.beginTransaction().replace(R.id.fragment, pointsRegisterFragment, POINTS_REGISTER_TAG).commit();

                break;
            case ROYALTIES_SHORTCUT:
                setTitle(R.string.royalties);
                TicketFragment royaltiesFragment = new TicketFragment();
                royaltiesFragment.setTicketType(ROYALTIES);
                fragmentManager.beginTransaction().replace(R.id.fragment, royaltiesFragment, ROYALTIES_FRAGMENT_TAG).commit();
                break;
            case ROAD_IMPROVEMENT_SHORTCUT:
                setTitle(R.string.road_improvement);
                TicketFragment roadImprovement = new TicketFragment();
                roadImprovement.setTicketType(ROAD_IMPROVEMENT);
                fragmentManager.beginTransaction().replace(R.id.fragment, roadImprovement, ROAD_IMPROVEMENT_FRAGMENT_TAG).commit();
                break;
            case CARRIES_SHORTCUT:
                setTitle(R.string.carries);
                TicketFragment carriesFragment = new TicketFragment();
                carriesFragment.setTicketType(CARRIES);
                fragmentManager.beginTransaction().replace(R.id.fragment, carriesFragment, CARRIES_FRAGMENT_TAG).commit();
                break;
            case SUPPLIES_SHORTCUT:
                setTitle(R.string.supplies);
                TicketFragment suppliesFragment = new TicketFragment();
                suppliesFragment.setTicketType(SUPPLIES);
                fragmentManager.beginTransaction().replace(R.id.fragment, suppliesFragment, SUPPLIES_FRAGMENT_TAG).commit();
                break;
            case FREE_CARRY_SHORTCUT:
                setTitle(R.string.free_carry);
                TicketFragment freeCarryFragment = new TicketFragment();
                freeCarryFragment.setTicketType(FREE_CARRY);
                fragmentManager.beginTransaction().replace(R.id.fragment, freeCarryFragment, FREE_CARRY_FRAGMENT_TAG).commit();
                break;
            case INTERN_CARRY_SHORTCUT:
                setTitle(R.string.intern_carry);
                TicketFragment internCarryFragment = new TicketFragment();
                internCarryFragment.setTicketType(INTERN_CARRY);
                fragmentManager.beginTransaction().replace(R.id.fragment, internCarryFragment, INTERN_CARRY_FRAGMENT_TAG).commit();
                break;
            case MATERIAL_REQUEST_SHORTCUT:
                setTitle(R.string.material_request);
                TicketFragment materialRequestFragment = new TicketFragment();
                materialRequestFragment.setTicketType(MATERIAL_REQUEST);
                fragmentManager.beginTransaction().replace(R.id.fragment, materialRequestFragment, MATERIAL_REQUEST_FRAGMENT_TAG).commit();
                break;
            case CONFIGURE_TAG_SHORTCUT:
                setTitle(R.string.configure_tag);
                TagDataPagerFragment tagPagerFragment = new TagDataPagerFragment();
                if (actualFragment != null) {
                    if (!getActualFragment().getTag().equals(CONFIGURE_TAG_FRAGMENT_TAG)) {
                        fragmentManager.beginTransaction().replace(R.id.fragment, tagPagerFragment, CONFIGURE_TAG_FRAGMENT_TAG).commit();
                    }

                } else {
                    fragmentManager.beginTransaction().replace(R.id.fragment, tagPagerFragment, CONFIGURE_TAG_FRAGMENT_TAG).commit();
                }
                break;
            case CANCEL_TICKET_SHORTCUT:
                setTitle(R.string.cancel_ticket);
                CancelTicketFragment cancelTicketFragment = new CancelTicketFragment();

                if (actualFragment != null) {
                    if (!getActualFragment().getTag().equals(CANCEL_TICKET_FRAGMENT)) {
                        fragmentManager.beginTransaction().replace(R.id.fragment, cancelTicketFragment, CANCEL_TICKET_FRAGMENT).commit();
                    }
                } else {
                    fragmentManager.beginTransaction().replace(R.id.fragment, cancelTicketFragment, CANCEL_TICKET_FRAGMENT).commit();
                }
                break;
            case EXIT_SHORTCUT:
                AlertDialog.Builder exitDialog = new AlertDialog.Builder(this);
                exitDialog.setMessage("¿Estás seguro(a) de que deseas salir?, Perderás los datos no guardados.")
                        .setPositiveButton(R.string.accept, (dialogInterface, i) -> finish())
                        .setNegativeButton(R.string.cancel, null)
                        .setTitle("Salir")
                        .setIcon(R.drawable.ic_exit)
                        .show();
                break;
            default:
                Log.i(TAG, "onNavigationItemSelected: nothing");
                break;
        }
    }

    public void showPasswordDialog(final FragmentManager fragmentManager, final LoginEmpleado loginEmpleado) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle(R.string.configure_tag_access);
        alertDialog.setMessage(R.string.configure_tag_message_dialog);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(30, 0, 30, 0);

        // Add a TextView here for the "Title" label, as noted in the comments
        final EditText user = new EditText(this);
        user.setHint(getString(R.string.set_user));
        user.setSingleLine(true);
        user.selectAll();
        user.setLayoutParams(layoutParams);
        user.setInputType(InputType.TYPE_CLASS_TEXT);
        if (loginEmpleado != null) {
            user.setText(loginEmpleado.getUsername());
        }
        layout.addView(user); // Notice this is an add method

        // Add another TextView here for the "Description" label
        final EditText key = new EditText(this);
        key.setHint(getString(R.string.set_password));
        key.setSingleLine(true);
        key.selectAll();
        key.setLayoutParams(layoutParams);
        key.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);
        if (loginEmpleado != null) {
            key.setText(loginEmpleado.getKey());
        }
        layout.addView(key); // Another add method
        alertDialog.setView(layout);
        alertDialog.setIcon(R.drawable.ic_key);

        alertDialog.setPositiveButton(R.string.accept,
                (dialog, which) -> {
                    String username = user.getText().toString();
                    String password = key.getText().toString();

                    LoginEmpleado employee = new LoginEmpleado();
                    employee.setKey(password);
                    employee.setUsername(username);
                    ArrayList<Integer> projects = new ArrayList<>();
                    projects.add(82);
                    employee.setProjects(projects);

                    if (username == null || username.equals("")) {
                        showPasswordDialog(fragmentManager, employee);
                        UIHelper.showToast(
                                MainActivity.this,
                                getString(R.string.set_user)
                        );
                        return;
                    }
                    if (password == null || password.equals("")) {
                        showPasswordDialog(fragmentManager, employee);
                        UIHelper.showToast(
                                MainActivity.this,
                                getString(R.string.set_password)
                        );
                        return;
                    }
                    final String originalHost = WebServiceConstants.HOST;
                    final String originalProject = WebServiceConstants.PROJECT;


                    String projectTest = "/eflow_login_android_open";


                    final String CONTROLLER_GET_USER = "/getEflowUser";

                    WebServiceConstants.setUrlLogin(
                            WebServiceConstants.HOST +
                                    projectTest +
                                    WebServiceConstants.REQUEST +
                                    CONTROLLER_GET_USER);
                    new ValidateUserTask(MainActivity.this,
                            Singleton.getInstance().getImei(),
                            new OnLoginListener() {
                                @Override
                                public void onCreateLogin() {
                                    Log.i(TAG, "onCreateLogin: ");
                                }

                                @RequiresApi(api = Build.VERSION_CODES.M)
                                @Override
                                public void onLogin(EmployeePojo employeePojo) {
                                    Log.i(TAG, "onLogin: " + WebServiceConstants.URL_LOGIN_USER);
                                    ArrayList<Integer> modules = getCarriesModules(employeePojo);

                                    if (modules.contains(MODULE_CONFIGURE_TAG)) {
                                        TagDataPagerFragment tagPagerFragment = new TagDataPagerFragment();
                                        fragmentManager.beginTransaction().replace(R.id.fragment, tagPagerFragment, CONFIGURE_TAG_FRAGMENT_TAG).commit();
                                    } else {
                                        UIHelper.showSnackbarWithAction(MainActivity.this, getString(R.string.missing_configure_tag_permissions));
                                    }
                                    WebServiceConstants.setUrlLogin(originalHost + originalProject + WebServiceConstants.REQUEST + CONTROLLER_GET_USER);

                                }

                                @Override
                                public void onLoginFailed(int i) {
                                    WebServiceConstants.setUrlLogin(originalHost + originalProject + WebServiceConstants.REQUEST + CONTROLLER_GET_USER);
                                }
                            }).execute(employee);

                });

        alertDialog.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onPause() {
        mNfcHelper.doOnPause();
        getExecutor().shutdown();
        setExecutor(null);

        super.onPause();
        if (mCarriesThread != null) {
            mCarriesThread.keepRunning(false);
            mCarriesThread = null;
            SingletonGlobal.getInstance().setCarriesThread(mCarriesThread);
        }

        if (mSyncThread != null) {
            mSyncThread.setKeepRunning(false);
            mSyncThread = null;
        }

    }

    @Override
    protected void onResume() {
        mNfcHelper.doOnResume();

        new WaitTask(this, new OnOperationRunning() {
            @Override
            public void onOperationStart() {
                //Log.i(TAG, "onOperationStart: ");
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onOperationRun() {

                if (Looper.myLooper() == null) {
                    Looper.prepare();
                }


                while (getGpsTracker().latitude == null &&
                        getGpsTracker().longitude == null) {
                    getGpsTracker().getLocation();
                    if (debug) {
                        getGpsTracker().latitude = BigDecimal.valueOf(21.10334969);
                        getGpsTracker().longitude = BigDecimal.valueOf(-101.62567139);
                    }

                }


            }

            @Override
            public void onOperationFinish() {
                //Log.i(TAG, "onOperationFinish: ");
            }
        }, getString(R.string.tag_gps_reader), getString(R.string.open_tag_gps_reader)).execute();
        setExecutor(Executors.newSingleThreadExecutor());
        mCarriesThread = new LocationThread(this);
        mCarriesThread.keepRunning(true);
        mCarriesThread.start();
        mSyncThread = new SyncThread(this);
        mSyncThread.start();
        SingletonGlobal.getInstance().setCarriesThread(mCarriesThread);
        super.onResume();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBackPressed() {
        if (SingletonGlobal.getInstance().isTicketWasPrinted()) {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);

            } else {
                Fragment actualFragment = getActualFragment();
                if (actualFragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.hide(actualFragment).remove(actualFragment).commit();
                    setTitle(R.string.app_name);
                    mBackground.setAlpha(1f);
                } else exitWhenBackButtonIsPressedTwice();
            }
        } else {
            UIHelper.showSnackbarWithAction(this, getString(R.string.ticket_was_not_printed));
        }
    }

    /**
     * Method to wait 2 seconds until back button is pressed if is pressed get out of the app
     */
    public void exitWhenBackButtonIsPressedTwice() {
        if (mDoubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.mDoubleBackToExitPressedOnce = true;
        UIHelper.showSnackbarWithAction(this, getString(R.string.toast_press_again_to_exit));

        new Handler().postDelayed(() -> mDoubleBackToExitPressedOnce = false, 2000);
    }

    /**
     * @return the actual fragment on UI
     */
    public Fragment getActualFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment[] frags = {
                fragmentManager.findFragmentByTag(POINTS_REGISTER_TAG),
                fragmentManager.findFragmentByTag(ROYALTIES_FRAGMENT_TAG),
                fragmentManager.findFragmentByTag(ROAD_IMPROVEMENT_FRAGMENT_TAG),
                fragmentManager.findFragmentByTag(CARRIES_FRAGMENT_TAG),
                fragmentManager.findFragmentByTag(SUPPLIES_FRAGMENT_TAG),
                fragmentManager.findFragmentByTag(FREE_CARRY_FRAGMENT_TAG),
                fragmentManager.findFragmentByTag(INTERN_CARRY_FRAGMENT_TAG),
                fragmentManager.findFragmentByTag(MATERIAL_REQUEST_FRAGMENT_TAG),
                fragmentManager.findFragmentByTag(CONFIGURE_TAG_FRAGMENT_TAG),
                fragmentManager.findFragmentByTag(CANCEL_TICKET_FRAGMENT)
        };
        for (int i = 0; i < frags.length; i++)
            if (frags[i] != null && frags[i].isVisible()) return frags[i];
        return null;
    }

    public GPSTracker getGpsTracker() {
        return mGpsTracker;
    }

    public void setGpsTracker(GPSTracker mGpsTracker) {
        this.mGpsTracker = mGpsTracker;
    }

    public EmployeePojo getEmployeePojo() {
        return mEmployeePojo;
    }

    public void setEmployeePojo(EmployeePojo employeePojo) {
        this.mEmployeePojo = employeePojo;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged: " + location.getLatitude() + "," + location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(TAG, "onStatusChanged: ");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(TAG, "onProviderEnabled: ");
    }

    /**
     * @param provider
     */
    @Override
    public void onProviderDisabled(String provider) {
        Log.i(TAG, "onProviderDisabled: ");
    }

    public ExecutorService getExecutor() {
        return mExecutor;
    }

    public void setExecutor(ExecutorService executor) {
        this.mExecutor = executor;
    }

    public void setNfcIdListener(NFCIdListener nfcIdListener) {
        this.mNfcIdListener = nfcIdListener;
        mNfcHelper = new NFCHelper(this, nfcIdListener);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (data != null) {
            if (data.getExtras() != null) {
                String barcode = data.getExtras().getString(BARCODE_EXTRA);
                if (barcode != null)
                    if (getBarcodeListener() != null)
                        getBarcodeListener().onBarcodeDetected(barcode);
            }
        }

    }

    public OnBarcodeDetectedListener getBarcodeListener() {
        return mBarcodeListener;
    }

    public void setBarcodeListener(OnBarcodeDetectedListener mBarcodeListener) {
        this.mBarcodeListener = mBarcodeListener;
    }
}
