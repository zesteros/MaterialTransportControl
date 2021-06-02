package com.mx.vise.acarreos.adapters;

import java.util.ArrayList;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por Angelo el martes 12 de febrero del 2019 a las 17:09
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreos_app-android
 */
public interface AdapterParser<T> {
    ArrayList<GenericAdapter> parseToAdapter(T classToParse);
}
