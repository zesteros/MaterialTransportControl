package com.mx.vise.acarreos.webservice;

/**
 *
 */
public class WebServiceConstants {

    public static final String HOST = "https://tomcat.vise.com.mx";
    public static final String PROJECT = "acarreos_app_open";//"/acarreos_app_nfc";
    public static final String EFLOW_LOGIN_APP = "/eflow_login_android_open_v2";

    //public static final String HOST = "http://192.168.60.84:8080";
    //public static final String PROJECT = "/acarreos";
    //public static final String EFLOW_LOGIN_APP = "/eflow";

    public static final String REQUEST = "/requests";

    public static final String CONTROLLER_PUT_DATA = "/putData";

    public static final String CONTROLLER_GET_DATA = "/getData";
    public static final String CONTROLLER_PUT_TICKET = "/putTicket";
    public static final String CONTROLLER_REQUEST_REGISTER = "/requestImeiRegister";


    public static final String URL_REQUEST_DEVICE_REGISTER = HOST + EFLOW_LOGIN_APP+ "/requests/requestImeiRegister";

    private static final String CONTROLLER_PUT_CANCELED_TICKET = "/putCanceledTicket";

    public static final String URL_PUT_DATA = HOST + PROJECT + REQUEST + CONTROLLER_PUT_DATA;

    public static final String URL_PUT_TICKET = HOST + PROJECT + REQUEST + CONTROLLER_PUT_TICKET;


    public static final String URL_PUT_CANCELED_TICKET =  HOST + PROJECT + REQUEST + CONTROLLER_PUT_CANCELED_TICKET;

    public static  String URL_LOGIN_USER = HOST + "/eflow_login_android/validateEflowUser";
    private static final String CONTROLLER_PUT_REPRINT = "/putReprint";
    public static final String URL_PUT_REPRINT = HOST + PROJECT + REQUEST + CONTROLLER_PUT_REPRINT;

    private static final String CONTROLLER_SYNC_POINTS = "/syncPoints";

    public static final String URL_SYNC_POINTS = HOST + PROJECT + REQUEST + CONTROLLER_SYNC_POINTS;

    private static final String CONTROLLER_PUT_POINTS = "/putPoint";
    public static final String URL_PUT_POINT = HOST + PROJECT + REQUEST + CONTROLLER_PUT_POINTS;

    public static final String URL_GET_DATA = HOST + PROJECT + REQUEST + CONTROLLER_GET_DATA;

    public static void setUrlLogin(String s) {
        URL_LOGIN_USER = s;
    }

//"https://tomcat.vise.com.mx/combustiblevise/peticion/combustible/empleadoscom/v1.0/empleadologeo
}
