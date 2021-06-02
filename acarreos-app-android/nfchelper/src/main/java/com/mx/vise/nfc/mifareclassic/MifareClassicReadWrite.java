package com.mx.vise.nfc.mifareclassic;

import android.app.ProgressDialog;
import android.content.Context;
import android.nfc.TagLostException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.util.SparseArray;

import androidx.core.content.FileProvider;

import com.mx.vise.androidwscon.utils.AESencrp;
import com.mx.vise.nfc.R;
import com.mx.vise.nfc.interfaces.OnKeyWritedListener;
import com.mx.vise.nfc.interfaces.OnTagReadListenerBase;
import com.mx.vise.nfc.interfaces.OnVirginTagDetectedListener;
import com.mx.vise.nfc.interfaces.WriteKeyStatus;
import com.mx.vise.nfc.pojos.KeyPOJO;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.VIBRATOR_SERVICE;

public class MifareClassicReadWrite {


    private static final String TAG = "VISE";
    private static final int SIZE_OF_TAG_IN_BLOCKS = 48;
    public static final int SIZE_OF_TAG_IN_SECTORS = 16;
    private static final int AMOUNT_ZEROS_WHEN_IS_CLEAN = 1504;
    public static final String DEFAULT_KEY = "FFFFFFFFFFFF";
    private static final int FLAG_SECTOR_INDEX = 15;
    private static SparseArray<String[]> mRawDump;
    private static Handler mHandler = new Handler();


    /**
     * Metodo para leer el tag, con las llaves dadas.
     *
     * @param context      el contexto
     * @param readListener el listener de lectura
     * @param keys         las llaves para poder leer
     */
    public static void readTag(Context context, OnTagReadListenerBase readListener, List<KeyPOJO> keys,
                               boolean readVirgin) {

        /*
         * Verifica que hay un tag
         * */
        final MCReader reader = Common.checkForTagAndCreateReader(context);

        /*
         * Si no hay tag regresa
         * */
        if (reader == null)
            return;

        new Thread(() -> {
            // Get key map from glob. variable.
            /*
             * obtiene los datos autenticandose
             * */
            mRawDump = reader.readAsMuchAsPossible(keys, readVirgin);


            reader.close();

            if (mRawDump == null) {
                mHandler.post(() -> readListener.onTagReadFailed(OnTagReadListenerBase.MiFareClassicReadStatus.TAG_REMOVED_WHILE_READING, null));
                return;
            }
            /*
             * Si no se pudo leer nada entonces las llaves son invalidas
             * */
            if (mRawDump.size() == 0) {
                /*
                 * Si ya se intento con la contraseña de defecto y sigue sin funcionar entonces
                 * el tag no tiene contraseña conocida
                 * */
                if (keys.get(0).getKeyA().equals(DEFAULT_KEY)) {
                    mHandler.post(() -> readListener.onTagReadFailed(OnTagReadListenerBase.MiFareClassicReadStatus.UKNOWN_KEYS, null));
                    return;
                }
                /*
                 * Prueba con contraseñas de defecto
                 *
                 * */
                for (KeyPOJO key : keys) {
                    try {
                        key.setKeyA(DEFAULT_KEY);
                        key.setKeyB(DEFAULT_KEY);
                        keys.set(keys.indexOf(key), key);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                /*
                 * Vuelve a leer pero con la contraseña default
                 * */
                readTag(context, readListener, keys, false);
                return;
            }
            /*
             * Si llego hasta aqui es porque se pudo leer,
             * si la primera llave es la contraseña default entonces es virgen
             * */
            if (keys != null)
                if (keys.get(0).getKeyA().equals(DEFAULT_KEY) && !readVirgin) {
                    /*
                     *
                     * */
                    String middleData = null;
                    if (mRawDump != null) {
                        if (mRawDump.get(0) != null) {
                            if (mRawDump.get(0).length > 2) {
                                if (mRawDump.get(0)[3] != null) {
                                    String keyData = mRawDump.get(0)[3];
                                    if (keyData.length() > 20) {
                                        middleData = mRawDump.get(0)[3].substring(12, 20);
                                    }
                                }
                            }
                        }
                    }
                    if (readListener instanceof OnVirginTagDetectedListener &&
                            middleData != null) {

                        String finalMiddleData = middleData;
                        mHandler.post(() ->
                                ((OnVirginTagDetectedListener) readListener).onVirginTagDetected(finalMiddleData));

                    }
                    return;
                }


            mHandler.post(() -> createTagDump(mRawDump, readListener, readVirgin));
        }).start();
    }

    /**
     * Metodo para escribir datos en el tag, se encripta, se parte y se escriben en hexadecimal
     *
     * @param context            el contexto
     * @param dataToWrite        los datos a escribir
     * @param readSectors        los sectores leidos completos
     * @param onTagWriteListener el listener para ejecutar eventos
     */
    public static void writeData(Context context,
                                 String dataToWrite,
                                 ArrayList<Sector> readSectors,
                                 OnTagWriteListener onTagWriteListener) {

        new AsyncTask() {
            private ProgressDialog mReadingTagProgressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mReadingTagProgressDialog = ProgressDialog
                        .show(
                                context,
                                context.getString(R.string.wait_please),
                                context.getString(R.string.writing_tag),
                                true,
                                false);
            }

            @Override
            protected Object doInBackground(Object[] objects) {
                /*
                 * Extraer datos a escribir en sectores
                 * */

                ArrayList<Sector> sectors = convertDataToWriteInSectors(readSectors, dataToWrite);


                final MCReader reader = Common.checkForTagAndCreateReader(context);
                if (reader == null) {
                    Log.i(TAG, "readTag: reader null");
                    return null;
                }

                for (Sector sector : sectors) {
                    for (Block block : sector.getBlocks()) {

                        Vibrator v = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            //deprecated in API 26
                            v.vibrate(300);
                        }
                        int result = reader.writeBlock(
                                sector.getSectorNumber(),
                                block.getPosition(),
                                Common.hexStringToByteArray(block.getData()),
                                Common.hexStringToByteArray(sector.getKey().substring(0, 12)),
                                false);
                        // Error handling.
                        if (result == 2 || result == -1) {
                            reader.close();
                            return result;
                        }

                    }
                }
                reader.close();
                return 0;
            }

            @Override
            protected void onPostExecute(Object o) {
                mReadingTagProgressDialog.dismiss();

                switch ((Integer) o) {
                    case 2:
                        onTagWriteListener.onTagWriteFailed(OnTagReadListenerBase.MiFareClassicWriteStatus.NOT_BLOCK_IN_SECTOR);
                        break;

                    case -1:
                        onTagWriteListener.onTagWriteFailed(OnTagReadListenerBase.MiFareClassicWriteStatus.ERROR_WRITING_SECTOR);
                        break;
                    case 0:
                        onTagWriteListener.onTagWriteSuccess();
                        break;
                }
            }
        }.execute();


    }

    /**
     * Mètodo para escribir las llaves cuando el tag se es virgen o no
     *
     * @param context    el contexto
     * @param middleData los datos de en medio
     * @param newKeys    las nuevas llaves a escribir
     */
    @Deprecated
    public static void writeKeys(Context context, String middleData, List<KeyPOJO> newKeys, OnKeyWritedListener onKeyWritedListener) {

        final MCReader reader = Common.checkForTagAndCreateReader(context);
        if (reader == null) {
            Log.i(TAG, "readTag: reader null");
            return;
        }
        int i = 0;
        writeIntent:
        for (KeyPOJO keyPOJO : newKeys) {

            String key = keyPOJO.getKeyA().substring(0, 12) + middleData + keyPOJO.getKeyB().substring(0, 12);
            int result = reader.writeBlock(
                    i,
                    3,
                    Common.hexStringToByteArray(key),
                    Common.hexStringToByteArray(DEFAULT_KEY),
                    false);
            switch (result) {
                case 2:
                    //onTagWriteListener.onTagWriteFailed(OnTagReadListenerBase.MiFareClassicWriteStatus.NOT_BLOCK_IN_SECTOR);
                    onKeyWritedListener.onKeyWriteFailed(WriteKeyStatus.NOT_BLOCK_IN_SECTOR);
                    break writeIntent;

                case -1:
                    //onTagWriteListener.onTagWriteFailed(OnTagReadListenerBase.MiFareClassicWriteStatus.ERROR_WRITING_SECTOR);
                    onKeyWritedListener.onKeyWriteFailed(WriteKeyStatus.ERROR_WRITING_SECTOR);
                    break writeIntent;
                case 0:
                    //Log.i(TAG, "writeKeys: writingkey success");
                    break;
                case 3:
                    //Log.i(TAG, "writeKeys: data length wrong:"+Common.hexStringToByteArray(key).length);
                    break;
                default:
                    //Log.i(TAG, "writeKeys: nothing");
                    break;
            }
            i++;

        }
        onKeyWritedListener.onKeyWriteSuccess();

    }

    /**
     * @param readSectors los sectores leidos
     * @param dataToWrite los datos a escribir
     * @return los sectores a escribir ya con posiciones
     */
    private static ArrayList<Sector> convertDataToWriteInSectors(ArrayList<Sector> readSectors, String dataToWrite) {


        ArrayList<Sector> sectors = new ArrayList<>();

        /*
         * Remove the last sector for flag purposes
         * */
        readSectors.remove(readSectors.size() - 1);
        try {
            /*
             * Encripta
             * */
            dataToWrite = AESencrp.encrypt(dataToWrite);

            /*
             * Convierte a hex
             * */
            dataToWrite = stringToHex(dataToWrite);

            String[] dataParted = partDataInBlocks(dataToWrite, 32, 44);
            int i = 0;
            for (Sector sector : readSectors) {
                for (Block block : sector.getBlocks()) {
                    block.setData(dataParted[i]);
                    i++;
                }
                sectors.add(sector);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return sectors;
    }

    /**
     * @param text the string
     * @return the hex value
     */
    public static String stringToHex(String text) {
        if (text != null) {
            //Change encoding according to your need
            try {
                return String.format("%04x", new BigInteger(1, text.getBytes("UTF8")));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * @param data los datos a particionar
     * @return los sectores en forma de array
     */
    private static String[] partDataInBlocks(String data, int amountOfCharacters, int amountOfBlocks) {
        if (data != null) {
            //Stores the length of the string
            int dataLength = data.length();
            /*
             * divide el string entre las partes que se quieren, si no
             * es par entonces agrega ceros hasta que sea
             * */
            while (dataLength % amountOfCharacters != 0) {
                data += "0";
                dataLength = data.length();
            }
            /*
             * contador de posicion
             * */
            int counter = 0;
            //Stores the array of string
            amountOfBlocks = amountOfBlocks < dataLength / amountOfCharacters ? dataLength / amountOfCharacters : amountOfBlocks;
            String[] newData = new String[amountOfBlocks];
            //Check whether a string can be divided into n equal parts

            for (int i = 0; i < dataLength; i += amountOfCharacters) {
                //Dividing string in n equal part using substring()
                String part = data.substring(i, i + amountOfCharacters);
                newData[counter] = part;
                counter++;
            }
            /*
             * las celdas del arreglo que estan vacias se
             * llenaran con ceros
             * */
            String fillZeros = "";
            for (int i = 0; i < amountOfCharacters; i++)
                fillZeros += "0";

            for (int i = counter; i < amountOfBlocks; i++)
                newData[i] = fillZeros;

            return newData;
        }
        return null;

    }

    /**
     * @param context
     * @param sectorKey
     * @param flag
     * @param block
     */
    public static void writeFlag(Context context, KeyPOJO sectorKey, String flag, int block) {

        final MCReader reader = Common.checkForTagAndCreateReader(context);
        if (reader == null) {
            return;
        }

        while (flag.length() < 32)
            flag += "0";

        if (block < 0)
            return;
        if (block > 2)
            return;


        int result = reader.writeBlock(
                FLAG_SECTOR_INDEX,
                block,
                Common.hexStringToByteArray(flag),
                Common.hexStringToByteArray(sectorKey.getKeyA().substring(0, 12)),
                false);

        // Error handling.
        switch (result) {
            case 2:
                Log.i(TAG, "writeData: not block in sector");
                break;
            case -1:
                Log.i(TAG, "writeData:error writing");
                break;
        }
        reader.close();

    }

    /**
     * @param context
     * @param sectorKey
     * @return
     */
    public static String readFlag(Context context, KeyPOJO sectorKey, int block) {
        final MCReader reader = Common.checkForTagAndCreateReader(context);
        if (reader == null)
            return null;
        try {
            String[] result = reader.readSector(FLAG_SECTOR_INDEX, Common.hexStringToByteArray(sectorKey.getKeyA().substring(0, 12)), false);
            if (result != null && result.length > 2) {
                return result[block];
            }
        } catch (TagLostException e) {
            e.printStackTrace();
        }
        reader.close();

        return null;

    }


    /**
     * @param rawDump    los datos leidos
     * @param listener   el listener de lectura del tag
     * @param readVirgin
     */
    private static void createTagDump(SparseArray<String[]> rawDump, OnTagReadListenerBase listener, boolean readVirgin) {
        ArrayList<Sector> sectors = new ArrayList<>();
        for (int i = 0; i < SIZE_OF_TAG_IN_SECTORS; i++) {

            String[] val = rawDump.get(i);

            if (val != null) {

                /*
                 * Crea nuevo sector si hay informacion disponible en ese
                 * sector (si la contraseña fue correcta y la lectura)
                 * */
                Sector sector = new Sector();
                sector.setSectorNumber(i);

                ArrayList<Block> blocks = new ArrayList<>();
                /*
                 * Recorre los sectores (agrega sus bloques)
                 * */
                for (int j = 0; j < val.length; j++) {
                    Block block = new Block();
                    block.setData(val[j]);
                    block.setPosition(j);
                    if (j == 3) {
                        sector.setKey(val[j]);
                    }
                    blocks.add(block);

                }
                sector.setBlocks(blocks);

                sectors.add(sector);

            }
        }
        if (sectors.size() < SIZE_OF_TAG_IN_SECTORS) {
            listener.onTagReadFailed(OnTagReadListenerBase.MiFareClassicReadStatus.TAG_REMOVED_OR_ANY_KEY_INVALID, sectors);
        } else {
            extractData(sectors, listener, readVirgin);
        }


    }

    /**
     * @param sectors           the sectors, the listener
     * @param onTagReadListener
     * @param readVirgin
     */
    private static void extractData(ArrayList<Sector> sectors, OnTagReadListenerBase onTagReadListener, boolean readVirgin) {
        /*
         * remove the uuid row
         * */
        if (sectors != null && sectors.get(0) != null && sectors.get(0).getBlocks() != null)
            sectors.get(0).getBlocks().remove(0);

        ArrayList<Sector> userDataSectors = new ArrayList<>();

        String data = "";
        for (Sector sector : sectors) {


            ArrayList<Block> newBlocks = new ArrayList<>();
            for (Block block : sector.getBlocks()) {
                if (block.getPosition() < 3) {
                    newBlocks.add(block);
                    data += block.getData();
                }
            }
            sector.setBlocks(newBlocks);
            userDataSectors.add(sector);
        }

        if (!tagHasData(data) && !readVirgin) {
            onTagReadListener.onTagReadFailed(OnTagReadListenerBase.MiFareClassicReadStatus.USER_DATA_IS_EMPTY, sectors);
        } else {
            try {
                data = !readVirgin ? AESencrp.decrypt(hexToString(data).replace(" ", "")) : data;
                Sector flagsSector = sectors.get(sectors.size() - 1);
                onTagReadListener.onTagReadSuccess(data, userDataSectors, flagsSector);
            } catch (Exception e) {
                onTagReadListener.onTagReadFailed(OnTagReadListenerBase.MiFareClassicReadStatus.USER_DATA_CORRUPTED, sectors);
                e.printStackTrace();
            }
        }
    }

    public static boolean tagHasData(String data){
        int countZeros = 0;
        for (int i = 0; i < data.length(); i++)
            if (data.charAt(i) == '0')
                countZeros++;
        if (countZeros == AMOUNT_ZEROS_WHEN_IS_CLEAN)
            return false;

        return true;
    }

    /**
     * @param hex the hex string
     * @return a UTF formed string
     */
    public static String hexToString(String hex) {
        StringBuilder sb = new StringBuilder();
        char[] hexData = hex.toCharArray();
        for (int count = 0; count < hexData.length - 1; count += 2) {
            int firstDigit = Character.digit(hexData[count], 16);
            int lastDigit = Character.digit(hexData[count + 1], 16);
            int decimal = firstDigit * 16 + lastDigit;
            sb.append((char) decimal);
        }
        return sb.toString();
    }

}
