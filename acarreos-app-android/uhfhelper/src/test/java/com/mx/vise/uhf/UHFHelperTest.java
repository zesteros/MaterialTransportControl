package com.mx.vise.uhf;

import android.util.Log;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.xml.bind.DatatypeConverter;

import static org.junit.Assert.*;

public class UHFHelperTest {

    @Test
    public void getOffsetStep() {

        final int maxLength = 64;//!erase ? 127 : 128;
        final short length = getLength((short) 6);//regresa un length de 8 (brinco)
        final int amountOfCharByOffset = 8;//para nivel 3 se esperan cadenas de 32 caracteres

        String[] data = partDataInSectors("77652f7a38757649592b42456e6e7a4f77426f783648534f64504e33786d466748317578486f507671437358745a647a772f71363873644a35677a49364b563958712b67346551786b396e380a553261632f3661442b557977723950737042624d6c2b547a4f34416d334e6775347a4c544565577435566e7a7445352b4d78344f0a000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
                amountOfCharByOffset, maxLength);

        //partDataInSectors()
    }

    @Test
    public void testDivisionTable() {
        int level = 2;
        short startLength = 128;
        short startChars = 512;
        short startParts = 1;
        for (int i = 0; i < level; i++) {
            startLength /= 2;
            startChars /= 2;
            startParts *= 2;
        }
        if (true) ;

    }



    public short getLength(short level) {

        short length = 128;
        for (short i = 0; i < level; i++) {
            length /= 2;
        }
        // assertEquals(8, length);
        return length;
    }

    /**
     * @param data the data to part
     * @return
     */
    private String[] partDataInSectors(String data, int sizeOfEveryPart, int sizeOfTag) {
        if (data != null) {
            //Stores the length of the string
            int dataLength = data.length();
            //parts determines the variable that divide the string in 'n' equal parts
            int parts = dataLength / sizeOfEveryPart;

            /*
             * If the size is not even add zeros to make it even
             * */
            while (dataLength % parts != 0) {
                data += "0";
                dataLength = data.length();
            }

            int counter = 0;
            //Stores the array of string
            String[] newData = new String[sizeOfTag];
            //Check whether a string can be divided into n equal parts

            for (int i = 0; i < dataLength; i += sizeOfEveryPart) {
                //Dividing string in n equal part using substring()
                String part = data.substring(i, i + sizeOfEveryPart);
                newData[counter] = part;
                counter++;
            }
            /*
             * Create zeros part
             * */
            String fillZeros = "";
            for (int i = 0; i < sizeOfEveryPart; i++)
                fillZeros += "0";

            /*
             * If the size of string is different than the tag size add zeros parts
             * */
            for (int i = counter; i < sizeOfTag; i++)
                newData[i] = fillZeros;

            return newData;
        }
        return null;

    }
}