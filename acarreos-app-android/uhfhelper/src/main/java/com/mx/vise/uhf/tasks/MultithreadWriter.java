package com.mx.vise.uhf.tasks;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.cw.phychipsuhfsdk.UHFHXAPI;
import com.cw.serialportsdk.cw;
import com.mx.vise.androiduihelper.UIHelper;
import com.mx.vise.uhf.R;
import com.mx.vise.uhf.UHFTagReadWrite;
import com.mx.vise.uhf.entities.Key;
import com.mx.vise.uhf.entities.Tag;
import com.mx.vise.uhf.interfaces.OnTagWriteListener;
import com.mx.vise.uhf.tag.Sector;
import com.mx.vise.uhf.tag.TagData;

import java.util.ArrayList;

import static com.mx.vise.uhf.UHFHelper.progressBar;
import static com.mx.vise.uhf.UHFTagReadWrite.ERROR_CODE;
import static com.mx.vise.uhf.interfaces.TagWriteStatus.AP_DAMAGED;
import static com.mx.vise.uhf.interfaces.TagWriteStatus.OVERWRITE_INTENT;
import static com.mx.vise.uhf.tasks.ReadingTagTask.CODE;

public class MultithreadWriter {

    private static final String TAG = "VISE";
    private Context mContext;
    private boolean manageApi;
    private UHFHXAPI mApi;
    private TagData tagData;
    private OnTagWriteListener onTagWriteListener;
    private UHFTagReadWrite uhfTagReadWrite;
    private boolean canOverwrite;
    private boolean erase;
    private long startTime;
    private boolean isVirgin;

    public MultithreadWriter(Context context,
                             boolean manageApi,
                             UHFHXAPI api,
                             TagData tagData,
                             OnTagWriteListener onTagWriteListener,
                             boolean canOverwrite,
                             boolean erase) {
        this.mContext = context;
        this.manageApi = manageApi;
        this.mApi = api;
        this.tagData = tagData;
        this.onTagWriteListener = onTagWriteListener;
        this.canOverwrite = canOverwrite;
        this.erase = erase;
    }

    /**
     * @return if the tag is virgin
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void initWriter() {
        Log.i(TAG, "initWriter: start init");

        /*
         * Start time
         * */

        startTime = System.currentTimeMillis();

        /*
         * Init UI
         * */
        ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                progressBar = UIHelper.createDialogWithBar(mContext);
                progressBar.getProgressTextView().setText(R.string.zero);
                progressBar.getTextView().setText(R.string.writing);
                progressBar.getAlertDialog().show();
                progressBar.getAlertDialog().setCancelable(false);

            }
        });

        /*
         * If manage api, open the serial (when the open serial was not called in onResume)
         * */
        if (manageApi)
            mApi.openHXUHFSerialPort(cw.getDeviceModel());


        /*
         * Suppose that the tag is new
         * */
        Tag tag = new Tag().setEPC(tagData.getEPC()).setAP(new Key(CODE));

        uhfTagReadWrite = new UHFTagReadWrite(tag, mApi);


        isVirgin = isVirgin(uhfTagReadWrite);
        /*
         * If is not virgin
         * */
        if (!isVirgin) {
            /*
             * If is corrupt finish
             * */
            if (uhfTagReadWrite.isCorruptAP()) {
                ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.getAlertDialog().cancel();
                        if (manageApi)
                            mApi.closeHXUHFSerialPort(cw.getDeviceModel());

                        onTagWriteListener.onTagWriteFailed(AP_DAMAGED);
                    }
                });
                return;
            } else {
                if (!canOverwrite()) {

                    ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.getAlertDialog().cancel();
                            if (manageApi) {
                                mApi.closeHXUHFSerialPort(cw.getDeviceModel());
                                //closeSerial();
                            }
                            onTagWriteListener.onTagWriteFailed(OVERWRITE_INTENT);
                        }
                    });
                    return;
                }
            }

            /*
             * Si no es virgen cambia por la nueva
             * */
            tag.setAP(new Key());

        }

        /*
         * muestra si es virgen al usuario
         * */
        final boolean finalIsVirgin = isVirgin;


        ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (finalIsVirgin)
                    progressBar.getTextView().setText("Tag nuevo detectado, configurando...");
                else progressBar.getTextView().setText("Tag configurado detectado.");
            }
        });
        secureIfNeeded();
        Log.i(TAG, "initWriter: isvirgin" + isVirgin);

    }

    public void secureIfNeeded() {
        /*
         * Si no es virgen aseguralo con pass
         * */
        if (isVirgin)
            if (!uhfTagReadWrite.secureTag()) {
                ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void run() {
                        progressBar.getAlertDialog().cancel();
                        if (manageApi) {
                            mApi.closeHXUHFSerialPort(cw.getDeviceModel());
                        }
                        onTagWriteListener.onTagWriteFailed(AP_DAMAGED);
                    }
                });
                return;
            }
        Log.i(TAG, "secureIfNeeded: called, finish");
        startWrite();

    }


    private void startWrite() {
        /*
         * Parametros de configuración
         * */
        final int maxLength = !erase ? 127 : 128;
        final short length = 1;
        final int amountOfCharByOffset = 4;


        /*
         * obten los datos por escribir
         * */
        final String[] dataToWrite = !erase ?
                partDataInSectors(tagData.getDataToWrite(), amountOfCharByOffset, maxLength) :
                partDataInSectors(getZeros(maxLength), amountOfCharByOffset, maxLength);

        Log.i(TAG, "startWrite: data to write length:" + dataToWrite.length);


        final ArrayList<Thread> threads = new ArrayList<>();

        final int amountOfThreads = 8;
        final int[] startIndex = {0};
        final int sum = 16;

        for (int i = 1; i < amountOfThreads + 1; i++) {
            final int finalI = i;
            threads.add(new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "run: thread no. " + finalI + " initialized indexes " + startIndex[0] + " to " + (startIndex[0] + sum));
                    tryWrite((short) startIndex[0], (short) (startIndex[0] + sum), length, dataToWrite);
                    startIndex[0] += (sum + 1);
                }
            }));

        }

        Log.i(TAG, "startWrite: threads length=" + threads.size());

        for (Thread thread : threads)
            thread.start();

        Log.i(TAG, "startWrite: threads started");

        new Thread(new Runnable() {
            private boolean noOneIsAlive;

            @Override
            public void run() {
                Log.i(TAG, "run: monitor threads started");
                int threadDeadCount = 0;
                while (!noOneIsAlive) {
                   //Log.i(TAG, "run: thread count=" + threadDeadCount);
                    for (Thread thread : threads) {
                        if (!thread.isAlive())
                            threadDeadCount++;
                        noOneIsAlive = threadDeadCount == amountOfThreads;
                    }
                }

                if (erase && !isVirgin) {
                    uhfTagReadWrite.unsecureTag();
                }


                ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void run() {
                        progressBar.getAlertDialog().cancel();
                        if (manageApi) {
                            mApi.closeHXUHFSerialPort(cw.getDeviceModel());
                            //closeSerial();
                        }
                        onTagWriteListener.onTagWriteSuccess();
                    }
                });
            }
        }).start();


    }


    private void tryWrite(short start, short end, short length, String[] dataToWrite) {

        Log.i(TAG, "tryWrite: thread write started indexes " + start + " to " + end);
        try {

            /*
             * Lista de sectores escritos o su primer intento
             * */
            final ArrayList<Sector> writedSector = new ArrayList<>();

            for (short i = start; i < end; i++) {

                Sector sector = new Sector()
                        .setData(dataToWrite[i])
                        .setLength(length)
                        .setOffset(i);

                sector.setSuccessWriting(uhfTagReadWrite.writeToUserData(sector));

                writedSector.add(sector);
            }
            /*
             * Obtiene los sectores fallados
             * */
            ArrayList<Sector> failedSectors = getSectorsByStatus(writedSector, false);

            /*Si hay sectores dañados reintenta*/
            if (!failedSectors.isEmpty()) {

                /*
                 * Mientras la cantidad de sectores fallados sean distintos a cero sigue tratando de escribirlos
                 * */
                boolean retry = false;
                int retries = 1;

                while (!retry) {
                    failedSectors = retryWriteSectors(failedSectors, uhfTagReadWrite, startTime, retries);
                    retry = failedSectors.isEmpty();
                    retries++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Sector> getSectorsByStatus(ArrayList<Sector> writedSector,
                                                 boolean writed) {
        ArrayList<Sector> sectors = new ArrayList<>();
        for (Sector sector : writedSector)
            if (sector.isSuccessWriting() == writed)
                sectors.add(sector);
        return sectors;
    }

    /**
     * @param uhfTagReadWrite the tag write class
     * @return if is the tag is virgin
     */
    private boolean isVirgin(UHFTagReadWrite uhfTagReadWrite) {
        /*
         * Si no la lee con la contraseña default cambia a la establecida
         * (ya se sabe que el tag no es virgen)
         * */
        String ap = null;

        while (ap == null) ap = uhfTagReadWrite.readAP();

        /*
         *
         * Si la ap no es 09 y la ap es la default entonces es virgen
         *
         * */
        return !ap.equals(ERROR_CODE) && ap.equals(new Key(CODE).key);

    }

    public void setCanOverwrite(boolean canOverwrite) {
        this.canOverwrite = canOverwrite;
    }

    /**
     * @param data the data to part
     * @return
     */
    private String[] partDataInSectors(String data, int partTo, int sizeOfTag) {
        if (data != null) {
            //Stores the length of the string
            int dataLength = data.length();
            //n determines the variable that divide the string in 'n' equal parts
            int parts = dataLength / partTo;

            while (dataLength % parts != 0) {
                data += "0";
                dataLength = data.length();
            }

            int counter = 0, amountOfChars = dataLength / parts;
            //Stores the array of string
            String[] newData = new String[sizeOfTag];
            //Check whether a string can be divided into n equal parts

            for (int i = 0; i < dataLength; i += amountOfChars) {
                //Dividing string in n equal part using substring()
                String part = data.substring(i, i + amountOfChars);
                newData[counter] = part;
                counter++;
            }
            String fillZeros = "";
            for (int i = 0; i < partTo; i++)
                fillZeros += "0";

            for (int i = counter; i < sizeOfTag; i++)
                newData[i] = fillZeros;

            return newData;
        }
        return null;

    }

    private boolean canOverwrite() {
        return canOverwrite;
    }


    private ArrayList<Sector> retryWriteSectors(final ArrayList<Sector> failedSectors, UHFTagReadWrite uhfTagReadWrite, final long startTime, final int retries) {

        final ArrayList<Sector> writedSector = new ArrayList<>();
        int i = 0;

        progressBar.getProgressBar().setProgress(0);

        for (final Sector failedSector : failedSectors) {
            final int finalI = i;
            ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.getProgressBar().setProgress(finalI * 100 / failedSectors.size());
                    progressBar.getProgressTextView().setText((finalI * 100 / failedSectors.size()) + "%");
                    progressBar.getTextView().setText(//"Reintentando escribir sectores erroneos" +
                            //"\n\nReintento " + retries +
                            //"\n\nSectores escritos exitosamente: " + getSectorsByStatus(writedSector, true).size() + " de " + failedSectors.size() +
                            //"\n\nSectores erroneos: " + getSectorsByStatus(writedSector, false).size() +
                            "\nTiempo transcurrido: " +
                            "" + ((System.currentTimeMillis() - startTime) / 1000) + " segundos." +
                            "");
                }
            });

            failedSector.setSuccessWriting(uhfTagReadWrite.writeToUserData(failedSector));

            writedSector.add(failedSector);

            i++;

        }
        return getSectorsByStatus(failedSectors, false);
    }

    private String getZeros(int maxLength) {
        String zeros = "";
        for (int i = 0; i < maxLength; i++)
            zeros += "0000";
        return zeros;
    }
}
