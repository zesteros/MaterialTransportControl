package com.mx.vise.zebraprinterandroid;

import android.util.Log;


import com.mx.vise.zebraprinterandroid.entities.CodebarOrientation;
import com.mx.vise.zebraprinterandroid.entities.PrintObject;
import com.mx.vise.zebraprinterandroid.exceptions.TicketIndexOutOfBoundsException;
import com.zebra.sdk.comm.Connection;

import java.text.Normalizer;
import java.util.ArrayList;

import static com.mx.vise.zebraprinterandroid.entities.PrintType.LINE;
import static com.mx.vise.zebraprinterandroid.entities.PrintType.SUBTITLE;
import static com.mx.vise.zebraprinterandroid.entities.PrintType.TITLE;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por aloza el sábado 13 de octubre del 2018
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreosandroid
 */
public class PrinterHelper {

    public static int SPACE_LINE = 25;

    public static int START_LINE = 50;

    public static int TITLE_SPACE_LINE = 80;

    public static int LABEL_WIDTH = 400;

    public static int TITLE_X_ADJUSTMENT = -20;

    public static int TITLE_Y_ADJUSTMENT = 80;

    public static int TITLE_FONT_SIZE = 40;
    public static int SUBTITLE_FONT_SIZE = 35;
    public static int TEXT_SIZE = 5;

    private static final String TAG = "VISE_PRINTER";

    private boolean autoIncrease;
    private Connection connection;

    public ArrayList<PrintObject> example() {
        /*
         * Se debe crear una lista de elementos que contendra el ticket
         * */
        ArrayList<PrintObject> objects = new ArrayList<>();

        /*
         * Para el titulo y subtitulo se debe de crear un objeto con tipo titulo, establecer la x y y inicial
         * y agregar a la lista
         * */
        PrintObject title = new PrintObject();
        title.setContent("VISE");
        title.setPrintType(TITLE);
        title.setX(115);
        title.setY(50);
        objects.add(title);

        PrintObject subtitle = new PrintObject();
        subtitle
                .setPrintType(SUBTITLE)
                .setContent("ACARREOS")
                .setX(90)
                .setY(100);
        objects.add(subtitle);

        /*
         * Para las lineas solo decir que es linea y su largo
         * */
        try {
            objects.add(new PrintObject().setPrintType(LINE).setLineLengthX(300));
        } catch (TicketIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        objects.add(new PrintObject().setTitle("Obra").setContent("Bacheo Qro San Luis").isDoubleLine(true));
        objects.add(new PrintObject().setTitle("No.obra").setContent("OBRAS-1634"));
        objects.add(new PrintObject().setTitle("Tipo Camion").setContent("TORTON"));
        objects.add(new PrintObject().setTitle("Tarjeta circulacion").setContent("TA718253627891").isDoubleLine(true));
        objects.add(new PrintObject().setTitle("Placa trasera").setContent("GUN1721").isDoubleLine(true));
        objects.add(new PrintObject().setTitle("Placa delantera").setContent("JUA7182").isDoubleLine(true));


        return objects;

    }

    public static String stripAccents(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }

    public String formatObjectToPrinter(boolean isTSC, ArrayList<PrintObject> printObjects) {

        TITLE_SPACE_LINE = isTSC ? 30 : TITLE_SPACE_LINE;

        /*
         * XA anuncia el comienzo de impresión, PON orientacion, PW ancho MN rastreo de medios, LL largo x, LH Inicio x,y
         * */
        String toPrint = "^XA^PON^PW" + LABEL_WIDTH + "^MNN^LL%d^LH0,0\r\n";

        int totalHeaderHeight = 0;

        if (isTSC) {
            toPrint += "^FO120,80\r\n" + "^A0,N,60,60" + "\r\n" + "^FDVISE^FS" + "\r\n";
            totalHeaderHeight = 80;
            totalHeaderHeight += TITLE_SPACE_LINE;
        }


        for (PrintObject printObject : printObjects) {
            if (printObject.getPrintType() != null) {
                switch (printObject.getPrintType()) {
                    case TITLE:

                        int x = !isTSC ? printObject.getX() : printObject.getX() + TITLE_X_ADJUSTMENT;
                        int y = !isTSC ? printObject.getY() : printObject.getY() + TITLE_Y_ADJUSTMENT;

                        toPrint += "^FO" + x + "," + y + "\r\n" + "^A0,N," +
                                (printObject.getTextHeight() == 0 ?
                                        TITLE_FONT_SIZE :
                                        printObject.getTextHeight())
                                + "," +
                                (printObject.getTextWidth() == 0 ?
                                        TITLE_FONT_SIZE :
                                        printObject.getTextWidth()) +
                                "\r\n" + "^FD " + printObject.getContent() + "^FS" +
                                "\r\n";
                        totalHeaderHeight = y;
                        totalHeaderHeight += TITLE_SPACE_LINE;
                        continue;

                    case SUBTITLE:
                        toPrint += "^FO" + printObject.getX() + "," + totalHeaderHeight +
                                "\r\n" + "^A0,N," +
                                (printObject.getTextHeight() == 0 ?
                                        SUBTITLE_FONT_SIZE :
                                        printObject.getTextHeight()) +
                                "," +
                                (printObject.getTextWidth() == 0 ?
                                        SUBTITLE_FONT_SIZE :
                                        printObject.getTextWidth()) +
                                "\r\n" + "^FD" + printObject.getContent() +
                                "^FS" + "\r\n";
                        totalHeaderHeight = printObject.getY();
                        totalHeaderHeight += TITLE_SPACE_LINE;
                        continue;

                    case LINE:
                        toPrint += "^FO" + START_LINE + "," + totalHeaderHeight + "\r\n" + "^GB" +
                                printObject.getLineLengthX() + ",5,5,B,0^FS" + "\r\n";
                        totalHeaderHeight += SPACE_LINE;
                        continue;

                    case BOX:
                        toPrint += "^FO" + START_LINE + "," + totalHeaderHeight + "\r\n" + "^GB" +
                                printObject.getLineLengthX() + "," + printObject.getLineLengthY() +
                                ",2,B^FS" + "\r\n";
                        totalHeaderHeight += printObject.getLineLengthY() + 10;
                        continue;
                    case CODE_BAR:
                        int codebarY =
                                printObject.getCodebarOrientation() == CodebarOrientation.VERTICAL ?
                                printObject.getY() :
                                totalHeaderHeight;

                        toPrint += "^FO" + printObject.getX() + "," + codebarY + "^BY3" + "\r\n";
                        toPrint += "^BC" + printObject.getCodebarOrientationZpl() + "," +
                                printObject.getCodeBarHeight() + ",N,N,N,A\r\n";
                        toPrint += "^FD" + printObject.getContent() + "^FS\r\n";

                        totalHeaderHeight +=
                                printObject.getCodebarOrientation() == CodebarOrientation.HORIZONTAL ?
                                        printObject.getCodeBarHeight() + 10 :
                                        0;
                        continue;
                    case SINGLE_TEXT:
                        toPrint += "^FO" + START_LINE + "," + totalHeaderHeight + "\r\n" + "^A0,N," +
                                printObject.getTextHeight() + "," + printObject.getTextWidth() +
                                "\r\n" + "^FD" + printObject.getContent() + "^FS" + "\r\n";
                        totalHeaderHeight += printObject.getTextHeight();
                        continue;
                }

            }
            if (printObject.getTitle() != null && printObject.getContent() != null) {
                toPrint += "^FO" + (printObject.getX() == 0 ? START_LINE : printObject.getX()) +
                        "," + totalHeaderHeight + "\r\n" + "^A0,N," + printObject.getTextHeight() +
                        "," + printObject.getTextWidth() + "\r\n" + "^FD" + printObject.getTitle() +
                        ":^FS" + "\r\n";
                totalHeaderHeight += printObject.isDoubleLine() ? SPACE_LINE : 0;
                toPrint += "^FO" +
                        (printObject.isDoubleLine() ? ((printObject.getX() == 0 ? START_LINE : printObject.getX()) +
                                (printObject.getDoubleLineX() == 0 ? 20 : printObject.getDoubleLineX())) : START_LINE +
                                printObject.getxSpace()) + "," + totalHeaderHeight + "\r\n" + "^A0,N," + printObject.getTextHeight() + "," + printObject.getTextWidth() + "\r\n" + "^FD" + printObject.getContent() + "^FS" + "\r\n";
                totalHeaderHeight += SPACE_LINE;
            }

        }
        toPrint += "^XZ";

        String body = toPrint.replace("^LL%d^", "^LL" + totalHeaderHeight);

        Log.d(TAG, body);

        return body;
    }

    public boolean isAutoIncrease() {
        return autoIncrease;
    }

    public void setAutoIncrease(boolean autoIncrease) {
        this.autoIncrease = autoIncrease;
    }

    public void setConnection(Connection printerConnection) {
        this.connection = printerConnection;
    }

    public Connection getConnection() {
        return connection;
    }
}
