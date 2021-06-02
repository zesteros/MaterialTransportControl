package com.mx.vise.nfc.mifareclassic;

import java.util.ArrayList;

public class Sector {
    private int sectorNumber;
    private ArrayList<Block> blocks;
    private String key;

    public int getSectorNumber() {
        return sectorNumber;
    }


    public void setSectorNumber(int sectorNumber) {
        this.sectorNumber = sectorNumber;
    }


    public ArrayList<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(ArrayList<Block> blocks) {
        this.blocks = blocks;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}