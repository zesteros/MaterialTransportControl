package com.mx.vise.acarreos.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.mx.vise.acarreos.R;
import com.mx.vise.acarreos.activities.MainActivity;
import com.mx.vise.acarreos.dao.DAOPoints;
import com.mx.vise.acarreos.pojos.PointPOJO;
import com.mx.vise.acarreos.tasks.WaitTask;
import com.mx.vise.acarreos.tasks.OnOperationRunning;
import com.mx.vise.acarreos.util.gps.GPSTracker;
import com.mx.vise.androiduihelper.UIHelper;
import com.mx.vise.login.pojos.EmployeePojo;

import java.util.ArrayList;
import java.util.Date;

import static com.mx.vise.acarreos.activities.MainActivity.SESSION_EXTRA;

public class PointsRegisterFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private Spinner mPointTypeSpinner;
    private EditText mChainageEditText, mPointNameEditText;
    private CheckBox mIsBankTooCheckbox;
    private Spinner mRadioSpinner;
    private TextView mChainageTextView, mRadioTextView, mPointTextView;
    private Button mSaveButton;
    private EmployeePojo mSession;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_points_register, container, false);

        Bundle extras = getActivity().getIntent().getExtras();

        mSession = extras != null ? (EmployeePojo) extras.get(SESSION_EXTRA) : null;

        mPointTypeSpinner = rootView.findViewById(R.id.pointTypeSpinner);
        mChainageEditText = rootView.findViewById(R.id.chainageEditText);
        mIsBankTooCheckbox = rootView.findViewById(R.id.isBankTooCheckBox);
        mRadioSpinner = rootView.findViewById(R.id.radioSpinner);
        mRadioTextView = rootView.findViewById(R.id.radioTextView);
        mChainageTextView = rootView.findViewById(R.id.chainageTextView);
        mSaveButton = rootView.findViewById(R.id.savePointButton);
        mPointNameEditText = rootView.findViewById(R.id.bankNameEditText);
        mPointTextView = rootView.findViewById(R.id.pointNameTextView);

        ArrayList<String> bankTypes = new ArrayList<>();
        bankTypes.add("Selecciona un tipo de punto");
        bankTypes.add("Banco");
        bankTypes.add("Tiro");
        bankTypes.add("Banco de desperdicio");

        ArrayList<String> radios = new ArrayList<>();
        radios.add("Selecciona un radio de influencia");
        radios.add("1m");
        radios.add("10m");
        radios.add("20m");
        radios.add("50m");
        radios.add("70m");
        radios.add("100m");
        radios.add("200m");

        ArrayAdapter bankTypesAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, bankTypes);
        ArrayAdapter radiosAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, radios);

        mRadioSpinner.setAdapter(radiosAdapter);
        mPointTypeSpinner.setAdapter(bankTypesAdapter);

        mPointTypeSpinner.setOnItemSelectedListener(this);

        mSaveButton.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            mPointTextView.setVisibility(View.GONE);
            mPointNameEditText.setVisibility(View.GONE);
            mChainageEditText.setVisibility(View.GONE);
            mIsBankTooCheckbox.setVisibility(View.GONE);
            mChainageTextView.setVisibility(View.GONE);
            mRadioTextView.setVisibility(View.GONE);
            mRadioSpinner.setVisibility(View.GONE);
            mSaveButton.setVisibility(View.GONE);
        } else if (position == 1) {
            mPointTextView.setVisibility(View.VISIBLE);
            mPointNameEditText.setVisibility(View.VISIBLE);
            mChainageEditText.setVisibility(View.GONE);
            mChainageTextView.setVisibility(View.GONE);
            mIsBankTooCheckbox.setVisibility(View.GONE);
            mRadioTextView.setVisibility(View.VISIBLE);
            mRadioSpinner.setVisibility(View.VISIBLE);
            mSaveButton.setVisibility(View.VISIBLE);
        } else if (position == 2) {
            mPointTextView.setVisibility(View.VISIBLE);
            mPointNameEditText.setVisibility(View.VISIBLE);
            mChainageEditText.setVisibility(View.VISIBLE);
            mIsBankTooCheckbox.setVisibility(View.VISIBLE);
            mChainageTextView.setVisibility(View.VISIBLE);
            mRadioTextView.setVisibility(View.VISIBLE);
            mRadioSpinner.setVisibility(View.VISIBLE);
            mSaveButton.setVisibility(View.VISIBLE);
        }
        else if (position == 3) {
            mPointTextView.setVisibility(View.VISIBLE);
            mPointNameEditText.setVisibility(View.VISIBLE);
            mChainageEditText.setVisibility(View.GONE);
            mChainageTextView.setVisibility(View.GONE);
            mIsBankTooCheckbox.setVisibility(View.VISIBLE);
            mRadioTextView.setVisibility(View.VISIBLE);
            mRadioSpinner.setVisibility(View.VISIBLE);
            mSaveButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {

        final GPSTracker gpsTracker = ((MainActivity) getActivity()).getGpsTracker();
        if (gpsTracker != null) {
            if (mSession != null) {
                if (mPointTypeSpinner.getSelectedItemPosition() == 1 &&
                        !UIHelper.validateField(getActivity(), mPointNameEditText, getActivity().getString(R.string.write_point_name)))
                    return;

                if (mPointTypeSpinner.getSelectedItemPosition() == 2 &&
                        !UIHelper.validateField(getActivity(), mChainageEditText, getActivity().getString(R.string.write_chainage)))
                    return;
                if (mPointTypeSpinner.getSelectedItemPosition() == 2 && !mChainageEditText.getText().toString().contains("+")) {
                    UIHelper.showSnackbarWithAction(getActivity(), "El cadenamiento debe de tener KM+MMM");
                    return;
                }

                if (mRadioSpinner.getSelectedItemPosition() == 0) {
                    mRadioSpinner.performClick();
                    UIHelper.showSnackbarWithAction(getActivity(), getActivity().getString(R.string.select_influency_radio));
                    return;
                }

                final boolean[] pointAdded = new boolean[2];
                new WaitTask(getActivity(), new OnOperationRunning() {
                    @Override
                    public void onOperationStart() {

                    }

                    @Override
                    public void onOperationRun() {
                        if (gpsTracker.longitude != null & gpsTracker.latitude != null) {
                            PointPOJO point = new PointPOJO(
                                    mSession.getEmployeeId(),
                                    mChainageEditText.getText().toString(),
                                    mIsBankTooCheckbox.isChecked() ? 1 : 0,
                                    "A",
                                    gpsTracker.latitude.floatValue(),
                                    gpsTracker.longitude.floatValue(),
                                    mPointNameEditText.getText().toString(),
                                    Float.parseFloat(
                                            mRadioSpinner
                                                    .getSelectedItem()
                                                    .toString()
                                                    .replace("m", "")),
                                    new Date(),
                                    mPointTypeSpinner.getSelectedItemPosition());
                            point.setObra(mSession.getAssignedBuilding().getBuildingId());
                            point.setAutorizado(0);
                            //if (!DAOPoints.bankAlreadyExists(getActivity(), point)) {
                            pointAdded[0] = DAOPoints.addPoint(getActivity(), point);


                            //} else pointAdded[1] = true;
                        } else pointAdded[0] = pointAdded[1] = false;

                    }

                    @Override
                    public void onOperationFinish() {
                        if (pointAdded[0]) {
                            mPointNameEditText.setText("");
                            mChainageEditText.setText("");
                            mRadioSpinner.setSelection(0);
                            mIsBankTooCheckbox.setChecked(false);
                            UIHelper
                                    .showSnackbarWithAction(getActivity(), getString(R.string.save_point_successfull));
                            mPointTypeSpinner
                                    .setSelection(0);
                        } else {
                            if (!pointAdded[1])
                                UIHelper
                                        .showSnackbarWithAction(getActivity(),
                                                getString(R.string.something_went_wrong_saving_point)
                                        );
                            else
                                UIHelper
                                        .showSnackbarWithAction(getActivity(),
                                                getString(R.string.bank_already_exists)
                                                        .replace("%", mPointNameEditText.getText().toString()));
                        }
                    }
                }).execute();


                //Toast.makeText(getActivity(), "guardar lat=" + gpsTracker.latitude + ", lon=" + gpsTracker.longitude, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
