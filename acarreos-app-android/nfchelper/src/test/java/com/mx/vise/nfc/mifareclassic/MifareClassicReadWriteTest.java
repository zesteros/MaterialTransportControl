package com.mx.vise.nfc.mifareclassic;

import org.junit.Test;

import static org.junit.Assert.*;

public class MifareClassicReadWriteTest {
    @Test
    public void testString() {
        String key = "1";
        while(key.length()<32){
            key+="0";
        }
        System.out.println(key);
    }
}