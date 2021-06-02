package com.mx.vise.acarreos.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.mx.vise.acarreos.R;
import com.mx.vise.acarreos.activities.MainActivity;
import com.mx.vise.acarreos.activities.SimpleScannerActivity;
import com.mx.vise.acarreos.dao.DAOTicket;
import com.mx.vise.acarreos.dao.entities.Tickets;
import com.mx.vise.acarreos.tasks.OnBarcodeDetectedListener;
import com.mx.vise.acarreos.util.CancelTicketRequest;
import com.mx.vise.androiduihelper.UIHelper;

import java.util.List;

import static com.mx.vise.acarreos.fragments.TicketFragment.REPLACE_CHAR;

public class CancelTicketFragment extends Fragment {

    public static final String BARCODE_LISTENER_EXTRA = "barcode_detected";
    private static final String TAG = "VISE";
    public static int BARCODE_INTENT_CODE = 721;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cancel_ticket, container, false);


        Intent intent = new Intent(getActivity(), SimpleScannerActivity.class);

        Button scanButton = rootView.findViewById(R.id.scanTicket);

        ((MainActivity) getActivity()).setBarcodeListener((OnBarcodeDetectedListener) barcode -> {
            Log.i(TAG, "onCreateView: " + barcode);
            cancelTicket(barcode);
        });

        scanButton.setOnClickListener(view -> {

            getActivity().startActivityForResult(intent, BARCODE_INTENT_CODE);

        });


        return rootView;
    }

    /**
     * Cancela el boleto cambia su estatus a cancelado y posteriormente se enviara la solicitud
     * de cancelacion  al servidor
     *
     * @param barcode el codigo de barras o numero de folio
     */
    private void cancelTicket(String barcode) {

        /*
         * Busca si existe o si ya esta cancelado
         * */
        CancelTicketRequest request = DAOTicket.ticketExist(getActivity(), barcode);

        List<Tickets> tickets = request.getTickets();


        /*
         * Si existe cancelalo
         * */
        if (request.getCancelStatus() == CancelTicketRequest.CancelStatus.SUCCESS) {

            UIHelper.showDialog(
                    getActivity(),
                    getString(R.string.cancel_ticket),
                    getString(R.string.cancel_ticket_message).replace(REPLACE_CHAR, "\"" + barcode + "\""),
                    true,
                    true,
                    (dialogInterface, i) -> {
                        DAOTicket.cancelTickets(getActivity(), tickets);
                        UIHelper.showSnackbarWithAction(getActivity(), getString(R.string.ticket_canceled_successfully));
                    });
        }
        // Si no existe muestra al usuario
        else {

            if (request.getCancelStatus() == CancelTicketRequest.CancelStatus.ALREADY_CANCELED) {
                UIHelper.showSnackbarWithAction(getActivity(), getString(R.string.ticket_already_canceled));

            } else if (request.getCancelStatus() == CancelTicketRequest.CancelStatus.DOES_NOT_EXIST_IN_DEVICE) {
                UIHelper.showSnackbarWithAction(getActivity(), getString(R.string.ticket_does_not_exist));

//                UIHelper.showDialog(
//                        getActivity(),
//                        "Boleto no existente",
//                        "El boleto con número de folio " + barcode + " no existe o no fue capturado por la terminal, ¿Deseas solicitar la cancelación de este boleto en caso de que exista?",
//                        true,
//                        true,
//                        (dialogInterface, i) -> {
//                            requestTicketCancelation(barcode);
//                        });
            }
        }
    }

    private void requestTicketCancelation(String sheetNumber) {

    }


}
