package com.mx.vise.acarreos.util;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.mx.vise.acarreos.singleton.SingletonGlobal;
import com.mx.vise.acarreos.util.gps.GPSTracker;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;

/**
 * Clase para obtener la fecha real acorde al gps o servidor ntp si aplica.
 */
public class TimeHelper {
    private static final String TAG = "VISE";

    public static final String TIME_SERVER = "time.google.com";


    public static void obtainValidDate(ObtainDateListener obtainDateListener, Context context) {

        getNTPTime(serverDate -> {
            Date localDate = new Date();
            Date gpsDate = getGpsTime();
            obtainDateListener.onDateObtained(compareDates(localDate, gpsDate, serverDate));
        }, context);
    }

    public static DateType obtainValidDate() {
        Date localDate = new Date();
        Date gpsDate = getGpsTime();
        return compareDates(localDate, gpsDate, null);
    }

    /**
     * @param localDate la fecha local
     * @param gpsDate   la fecha del gps
     * @param ntpDate   la fecha del servidor
     * @return la fecha valida
     */
    private static DateType compareDates(Date localDate, Date gpsDate, Date ntpDate) {
        /*
         *
         * Genera dos fechas la local (del telefono) con 5 minutos menos y 5 minutos más
         * */
        Calendar local = Calendar.getInstance();
        local.setTime(localDate);
        local.add(Calendar.MINUTE, 5);
        /*
         * Fecha local con 5 minutos mas
         * */
        Date endLocalDate = local.getTime();

        local.setTime(localDate);
        /*
         * fecha local con 5 minutos menos
         * */
        local.add(Calendar.MINUTE, -5);

        Date startLocalDate = local.getTime();

        /*
         * Si hay fecha de gps y el rango de fecha del gps se encuentra dentro
         * */
        if (gpsDate != null) {
            if (!gpsDate.after(endLocalDate) && !gpsDate.before(startLocalDate))
                return new DateType(localDate, DateResultType.LOCAL);
            else return new DateType(gpsDate, DateResultType.GPS);
        }

        if (ntpDate != null) {
            if (!ntpDate.after(endLocalDate) && !ntpDate.before(startLocalDate))
                return new DateType(localDate, DateResultType.GPS);
            else return new DateType(ntpDate, DateResultType.NTP);
        }
        return new DateType(localDate, DateResultType.LOCAL);
    }


    /**
     * @return la fecha del gps
     */
    private static Date getGpsTime() {
        GPSTracker gpsTracker = SingletonGlobal.getInstance().getGpsTracker();
        if (gpsTracker != null) {
            Location location = gpsTracker.getLocation();
            if (location != null) {
                return new Date(location.getTime());
            }
        }
        return null;
    }

    private static void getNTPTime(NTPReceiverListener listener, Context context) {
        new Thread(() -> {
            NTPUDPClient timeClient = new NTPUDPClient();
            InetAddress inetAddress;
            try {
                inetAddress = InetAddress.getByName(TIME_SERVER);

                TimeInfo timeInfo = timeClient.getTime(inetAddress);
                ((AppCompatActivity) context).runOnUiThread(() ->
                        listener.onNTPTimeReceived(
                                new Date(timeInfo.getMessage().getTransmitTimeStamp().getTime()))
                );
            } catch (UnknownHostException e) {
                ((AppCompatActivity) context).runOnUiThread(() -> listener.onNTPTimeReceived(null));
                Log.e(TAG, "getNTPTime: ", e);
            } catch (Exception e) {
                Log.e(TAG, "getNTPTime: ", e);
                ((AppCompatActivity) context).runOnUiThread(() -> listener.onNTPTimeReceived(null));
            }
        }).start();

    }

    private interface NTPReceiverListener {
        void onNTPTimeReceived(Date serverDate);
    }

    public interface ObtainDateListener {
        void onDateObtained(DateType dateType);
    }

    public enum DateResultType {
        LOCAL, GPS, NTP
    }

    public static class DateType {
        private Date date;
        private DateResultType result;

        public DateType(Date date, DateResultType result) {
            this.date = date;
            this.result = result;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public DateResultType getResult() {
            return result;
        }

        public void setResult(DateResultType result) {
            this.result = result;
        }
    }
}

