package com.mx.vise.acarreos.util;

import android.util.Log;

import com.mx.vise.acarreos.fragments.TicketType;
import com.mx.vise.acarreos.pojos.TicketPOJO;
import com.mx.vise.acarreos.util.gps.MapCalculator;
import com.mx.vise.login.pojos.EmployeePojo;
import com.mx.vise.zebraprinterandroid.PrinterHelper;
import com.mx.vise.zebraprinterandroid.entities.CodebarOrientation;
import com.mx.vise.zebraprinterandroid.entities.PrintObject;
import com.mx.vise.zebraprinterandroid.entities.PrintType;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por Angelo el viernes 15 de febrero del 2019 a las 13:51
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreos_app-android
 */
public class UtilPrinter {

    private static final String TAG = "VISE";
    private static int LIMIT_CHAR_WITH_SIDE_BARCODE = 40;

    /**
     * @return la lista de objetos a imprimir
     */
    public ArrayList<PrintObject> getPrintObjects(EmployeePojo employee, TicketPOJO ticket,
                                                  TicketType ticketType, boolean reprint, int reprintCounter) {
        /*
         * Se debe crear una lista de elementos que contendra el ticket
         * */
        ArrayList<PrintObject> objects = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        PrinterHelper.START_LINE =
                ticketType == TicketType.INTERN_CARRY ? 40 : 12;
        PrinterHelper.SPACE_LINE = 20;


        //if (ticketType != TicketType.INTERN_CARRY) {
        PrintObject codebarSide = new PrintObject();
        codebarSide
                .setCodeBarHeight(110)
                .setCodebarOrientation(CodebarOrientation.VERTICAL)
                .setContent(ticket.getSheetNumber())
                .setPrintType(PrintType.CODE_BAR)
                .setX(320)
                .setY(140);

        objects.add(codebarSide);

        //}

        String unitOfMeasure = ticket.getUnitOfMeasure() == 0 ? "M3" : "TN";

        /*Inserta el título del ticket*/
        PrintObject title = new PrintObject();
        title
                .setPrintType(PrintType.TITLE)
                .setContent(getTicketType(ticketType))
                .setX(160 - ((getTicketType(ticketType).length() / 2) * 15))//posición en x
                .setY(60)//posición en y
                .setTextHeight(35)
                .setTextWidth(35);
        objects.add(title);
        objects.add(getSingleText(""));
        /*Separa el nombre de la obra del numero*/
        String buildingNumber = ticket.getBuilding();



        objects.add(getSingleText("FOLIO:" + ticket.getSheetNumber()));

        objects.add(getSingleText("OBRA:" + buildingNumber));
        objects.add(getSingleText("PLACA TRASERA:" + ticket.getRearLicensePlate()));
        objects.add(getSingleText("MONTEN: " + MapCalculator.round(ticket.getIncrease(),
                2) + " M3"));
        objects.add(getSingleText("CAPACIDAD: " + MapCalculator.round(ticket.getCapacity(),
                2) + " " + unitOfMeasure));
        //objects.add(getSingleText());
        Log.i(TAG, "getPrintObjects: "+ticket.getMaterial());
        String description =  "MATERIAL:" + ticket.getMaterial().getDescription().replace((char) 160, ' ');
        //objects.add(getSingleText(ticket.getMaterial().getIdMaterialNavision()));
        addMultilineObjects(description, objects, LIMIT_CHAR_WITH_SIDE_BARCODE);
        objects.add(getSingleText("DESCUENTO:" + MapCalculator.round(ticket.getDiscount(),
                2) + " " + unitOfMeasure));
        if (ticketType != TicketType.SUPPLIES) {
            objects.add(getSingleText("FECHA DE SALIDA:"));
            objects.add(getSingleText(dateFormat.format(ticket.getExitDate())));
        }
        switch (ticketType) {
            case CARRIES:
                objects.add(getSingleText("CHECADOR BANCO:"));
                objects.add(getSingleText(ticket.getUsernameBank()));
                break;
            case SUPPLIES:
                break;
            case ROYALTIES:
            case ROAD_IMPROVEMENT:
            case FREE_CARRY:
            case INTERN_CARRY:
                objects.add(getSingleText("CHECADOR BANCO:"));
                objects.add(getSingleText(employee.getEmployeeName()));
                break;
        }
        objects.add(getSingleText("ORIGEN:"));
        objects.add(getSingleText(ticket.getOrigin().getCadenamiento() + " - " +
                ticket.getOrigin().getNombreBanco()));

        switch (ticketType) {
            case ROYALTIES:
                break;
            case ROAD_IMPROVEMENT:
            case CARRIES:
            case FREE_CARRY:
            case INTERN_CARRY:
            case SUPPLIES:
                objects.add(getSingleText("DESTINO:"));
                objects.add(getSingleText(ticket.getDestiny().getCadenamiento() + " - " +
                        ticket.getDestiny().getNombreBanco()));
                objects.add(getSingleText("DISTANCIA:" + MapCalculator.round(ticket.getDistance(),
                        2) + " KM"));
                break;
        }
        objects.add(getSingleText("COORDENADAS DE SALIDA:"));
        objects.add(getSingleText(MapCalculator.round(ticket.getExitCoordinates().latitude.floatValue(),
                6) +
                ", " + (MapCalculator.round(ticket.getExitCoordinates().longitude.floatValue(),
                6))));

        switch (ticketType) {
            case SUPPLIES:
            case CARRIES:
                objects.add(getSingleText("COORDENADAS DE LLEGADA:"));
                objects.add(getSingleText(MapCalculator.round(
                        ticket.getArrivalCoordinates().latitude.floatValue(), 6) +
                        ", " + (MapCalculator.round(ticket.getArrivalCoordinates().longitude.floatValue(),
                        6))));
                objects.add(getSingleText("FECHA DE LLEGADA:"));
                objects.add(getSingleText(dateFormat.format(ticket.getArrivalDate())));
                objects.add(getSingleText("CHECADOR TIRO:"));
                objects.add(getSingleText(ticket.getUserNameThrow()));
                break;
        }

        if (ticketType == TicketType.MATERIAL_REQUEST) {
            objects.add(getSingleText("VIGENCIA:" + dateFormat.format(ticket.getExpirationDate())));
            objects.add(getSingleText("Este boleto no ampara ningun"));
            objects.add(getSingleText("tipo de pago."));
            objects.add(getSingleText(""));

        }
        if(reprint){
            objects.add(getSingleText(""));
            objects.add(getSingleText(""));
            objects.add(new PrintObject()
                    .setContent("REIMPRESION "+reprintCounter)
                    .setPrintType(PrintType.SINGLE_TEXT)
                    .setTextHeight(30)
                    .setTextWidth(30));
        }
        objects.add(getSingleText(""));
        objects.add(getSingleText(""));
        objects.add(getSingleText(""));

        //if (ticketType != TicketType.INTERN_CARRY) {
        PrintObject codebarBelow = new PrintObject();
        codebarBelow
                .setCodeBarHeight(50)
                .setCodebarOrientation(CodebarOrientation.HORIZONTAL)
                .setContent(ticket.getSheetNumber())
                .setPrintType(PrintType.CODE_BAR)
                .setX(5);

        objects.add(codebarBelow);

        //}



        return objects;

    }

    private String getTicketType(TicketType ticketType) {
        switch (ticketType) {
            case ROYALTIES:
                return "REGALIA";
            case ROAD_IMPROVEMENT:
                return "MEJORA CAMINOS";
            case CARRIES:
                return "ACARREOS";
            case SUPPLIES:
                return "SUMINISTRO MAT.";
            case FREE_CARRY:
                return "DESPERDICIO";
            case INTERN_CARRY:
                return "ACARREO INTERNO";
            case MATERIAL_REQUEST:
                return "SOLICITUD MAT.";
        }
        return "";
    }

    private String removeAccents(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }

    private String[] getPartedName(String name) {
        String[] partedName = name.split(" ");
        String[] newName = new String[2];
        if (partedName.length > 2) {

            newName[0] = partedName[0];
            newName[1] = partedName[3];
        } else if (partedName.length == 2) {
            newName[0] = partedName[0];
            newName[1] = partedName[1];
        }
        return newName;
    }

    private PrintObject getSingleText(String text) {

        text = removeAccents(text);

        return new PrintObject()
                .setContent(text)
                .setPrintType(PrintType.SINGLE_TEXT)
                .setTextHeight(20)
                .setTextWidth(20);
    }

    private void addMultilineObjects(String text, ArrayList<PrintObject> objects, int limit) {
        if (text.length() > limit) {
            objects.add(getSingleText(text.substring(0, limit)));
            objects.add(getSingleText(text.substring(limit)));
        } else {
            objects.add(getSingleText(text));
        }
    }
}
