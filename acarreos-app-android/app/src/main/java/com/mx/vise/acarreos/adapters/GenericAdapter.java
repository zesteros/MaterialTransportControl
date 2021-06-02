package com.mx.vise.acarreos.adapters;

import java.io.Serializable;

public class GenericAdapter implements Serializable {

    private String value;
    private String text;

    public GenericAdapter(String value, String text){
        this.value = value;
        this.text = text;
    }

    public GenericAdapter(){}

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
