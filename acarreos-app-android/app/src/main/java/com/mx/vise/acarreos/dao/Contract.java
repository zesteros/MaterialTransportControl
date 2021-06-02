package com.mx.vise.acarreos.dao;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por aloza el viernes 04 de enero del 2019
 *
 * @author Angelo de Jesus Loza Martinez
 * @version Acarreos APP 1.0
 */
public final class Contract {

    public static final String DATABASE_K = "Vyc3aNdr0yt@krR30$";

    public Contract() {
    }

    public static class SCRIPT {

        public static final String TABLE_NAME_POINTS = "points";

        public static final String COLUMN_ID_POINT = "id_point";
        public static final String COLUMN_POINT_TYPE = "point_type";
        public static final String COLUMN_BANK_NAME = "bank_name";
        public static final String COLUMN_RADIO = "radio";
        public static final String COLUMN_CHAINAGE = "chainage";
        public static final String COLUMN_IS_BANK_TOO = "is_bank_too";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_REG_DATE = "reg_date";
        public static final String COLUMN_UPLOAD_ESTATUS = "upload_status";
        public static final String COLUMN_STATUS = "status";

        public static final String COLUMN_ADD_USER = "add_user";
        public static final String COLUMN_UPLOAD_DATE = "upload_date";
        public static final String COLUMN_BUILDING = "building";

        public static final String COLUMN_ID_POINT_SERVER = "id_point_server";


        public static final String COLUMN_UPD_DATE_SERVER = "upd_date_server";
        public static final String COLUMN_ESTATUS_SERVER = "server_estatus";

        public static final String SQL_CREATE_ENTRIES_POINTS =
                "CREATE TABLE " + SCRIPT.TABLE_NAME_POINTS + " (" +
                        SCRIPT.COLUMN_ID_POINT + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        SCRIPT.COLUMN_POINT_TYPE + " INTEGER," +
                        SCRIPT.COLUMN_BANK_NAME + " TEXT," +
                        SCRIPT.COLUMN_RADIO + " FLOAT," +
                        SCRIPT.COLUMN_CHAINAGE + " TEXT," +
                        SCRIPT.COLUMN_IS_BANK_TOO + " INTEGER," +
                        SCRIPT.COLUMN_LATITUDE + " FLOAT," +
                        SCRIPT.COLUMN_LONGITUDE + " FLOAT," +
                        SCRIPT.COLUMN_REG_DATE + " DATETIME," +
                        SCRIPT.COLUMN_UPLOAD_ESTATUS + " TEXT," +
                        SCRIPT.COLUMN_UPLOAD_DATE + " DATETIME," +
                        SCRIPT.COLUMN_ADD_USER + " INTEGER," +
                        SCRIPT.COLUMN_BUILDING + " TEXT," +
                        SCRIPT.COLUMN_ID_POINT_SERVER + " INTEGER," +
                        SCRIPT.COLUMN_UPD_DATE_SERVER + " DATETIME," +
                        SCRIPT.COLUMN_ESTATUS_SERVER + " TEXT);";

        public static final String COLUMN_ID_MATERIAL_NAVISION = "id_material_navision";
        public static final String COLUMN_ACRONYM = "acronym";
        public static final String COLUMN_ADD_DATE = "add_date";
        public static final String COLUMN_UPD_DATE = "upd_date";
        public static final String COLUMN_DONWLOAD_DATE = "download_date";

        public static final String COLUMN_ID_MATERIAL_SERVER = "id_material_server";
        public static final String COLUMN_ID_MATERIAL_LOCAL = "id_material_local";

        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_UNIT_OF_MEASURE = "unit_of_measure";
        public static final String SQL_CREATE_ENTRIES_MATERIALS =
                "CREATE TABLE " + SCRIPT.TABLE_NAME_MATERIALS + " (" +
                        SCRIPT.COLUMN_ID_MATERIAL_LOCAL + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        SCRIPT.COLUMN_ID_MATERIAL_SERVER + " INTEGER," +
                        SCRIPT.COLUMN_ID_MATERIAL_NAVISION + " TEXT," +
                        SCRIPT.COLUMN_BUILDING + " TEXT," +
                        SCRIPT.COLUMN_ACRONYM + " TEXT," +
                        SCRIPT.COLUMN_ADD_USER + " INTEGER," +
                        SCRIPT.COLUMN_ADD_DATE + " DATETIME," +
                        SCRIPT.COLUMN_UPD_DATE + " DATETIME," +
                        SCRIPT.COLUMN_ESTATUS_SERVER + " TEXT," +
                        SCRIPT.COLUMN_UPLOAD_ESTATUS + " TEXT," +
                        SCRIPT.COLUMN_DONWLOAD_DATE + " DATETIME," +
                        SCRIPT.COLUMN_DESCRIPTION + " TEXT," +
                        SCRIPT.COLUMN_UNIT_OF_MEASURE + " TEXT);";

        public static final String TABLE_NAME_MATERIALS_BY_POINT = "materials_by_point";

        public static final String TABLE_NAME_MATERIALS = "materials";


        public static final String COLUMN_ID_MATERIAL_BY_POINT_LOCAL = "id_material_by_point_local";

        public static final String COLUMN_ID_MATERIAL_BY_POINT_SERVER = "id_material_by_point_server";

        public static final String SQL_CREATE_ENTRIES_MATERIALS_BY_POINT =
                "CREATE TABLE " + SCRIPT.TABLE_NAME_MATERIALS_BY_POINT + " (" +
                        SCRIPT.COLUMN_ID_MATERIAL_BY_POINT_LOCAL + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        SCRIPT.COLUMN_ID_MATERIAL_BY_POINT_SERVER + " INTEGER," +
                        SCRIPT.COLUMN_ID_MATERIAL_SERVER + " INTEGER," +
                        SCRIPT.COLUMN_ID_POINT_SERVER + " INTEGER," +
                        SCRIPT.COLUMN_ADD_USER + " INTEGER," +
                        SCRIPT.COLUMN_ADD_DATE + " DATETIME," +
                        SCRIPT.COLUMN_UPD_DATE + " DATETIME," +
                        SCRIPT.COLUMN_ESTATUS_SERVER + " TEXT," +
                        SCRIPT.COLUMN_UPLOAD_ESTATUS + " TEXT," +
                        SCRIPT.COLUMN_DONWLOAD_DATE + " DATETIME);";

        public static final String SQL_DELETE_ENTRIES_POINTS =
                "DROP TABLE IF EXISTS " + SCRIPT.TABLE_NAME_POINTS + ";";


        public static final String SQL_DELETE_ENTRIES_MATERIALS =
                "DROP TABLE IF EXISTS " + SCRIPT.TABLE_NAME_MATERIALS + ";";

        public static final String SQL_DELETE_ENTRIES_REPRINTS =
                "DROP TABLE IF EXISTS " + SCRIPT.TABLE_NAME_REPRINTS + ";";

        public static final String SQL_DELETE_ENTRIES_TICKETS =
                "DROP TABLE IF EXISTS " + SCRIPT.TABLE_NAME_TICKETS + ";";

        public static final String SQL_DELETE_ENTRIES_MATERIALS_BY_POINT =
                "DROP TABLE IF EXISTS " + SCRIPT.TABLE_NAME_MATERIALS_BY_POINT + ";";

        public static final String TABLE_NAME_DISTANCES_BY_POINT = "distances_by_point";
        public static final String SQL_DELETE_ENTRIES_DISTANCES =
                "DROP TABLE IF EXISTS " + SCRIPT.TABLE_NAME_DISTANCES_BY_POINT + ";";
        public static final String COLUMN_TICKET_TYPE = "ticket_type";
        public static final String COLUMN_REAR_LICENCE_PLATE = "rear_license_plate";
        public static final String COLUMN_INCREASE = "increase";
        public static final String COLUMN_CAPACITY = "capacity";
        public static final String COLUMN_MATERIAL_ID = "material_id";
        public static final String COLUMN_ORIGIN_POINT = "point_origin_id";
        public static final String COLUMN_EXIT_COORDINATES = "exit_coordinates";
        public static final String COLUMN_DISCOUNT = "discount";
        public static final String COLUMN_EXIT_DATE = "exit_date";
        public static final String COLUMN_USER_ID_BANK = "user_id_bank";
        public static final String COLUMN_USERNAME_BANK = "username_bank";
        public static final String COLUMN_SHEET_NUMBER = "sheet_number";
        public static final String COLUMN_DISTANCE = "distance";
        public static final String COLUMN_ARRIVAL_DATE = "arrival_date";
        public static final String COLUMN_USER_ID_THROW = "id_user_throw";
        public static final String COLUMN_USERNAME_THROW = "user_name_throw";
        public static final String COLUMN_ARRIVAL_COORDINATES = "arrival_coordinates";
        public static final String COLUMN_EXPIRATION_DATE = "expiration_date";

        public static final String TABLE_NAME_TICKETS = "tickets";

        public static final String COLUMN_ID_TICKET_LOCAL = "id_ticket_local";
        public static final String COLUMN_ID_TICKET_SERVER = "id_ticket_server";

        public static final String COLUMN_DESTINY_POINT = "destiny_point";

        public static final String COLUMN_ID_ACTIVO_TAG = "id_activo";
        public static final String COLUMN_TID_TAG = "tid";
        public static final String COLUMN_ADD_DATE_TAG = "add_date";
        public static final String COLUMN_SYNC_DATE_TAG = "sync_date";
        public static final String TABLE_NAME_TAGS = "tags";
        public static final String COLUMN_ID_TAG = "id_tag";

        public static final String SQL_CREATE_ENTRIES_TAGS = "CREATE TABLE " + SCRIPT.TABLE_NAME_TAGS + " (" +

                SCRIPT.COLUMN_ID_TAG + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SCRIPT.COLUMN_ID_ACTIVO_TAG + " INTEGER," +
                SCRIPT.COLUMN_TID_TAG + " TEXT," +
                SCRIPT.COLUMN_ADD_DATE_TAG + " DATETIME, " +
                SCRIPT.COLUMN_SYNC_DATE_TAG + " DATETIME);";

        public static final String SQL_DELETE_ENTRIES_TAGS =
                "DROP TABLE IF EXISTS " + SCRIPT.TABLE_NAME_TAGS + ";";

        public static final String SQL_CREATE_ENTRIES_TICKETS =
                "CREATE TABLE " + SCRIPT.TABLE_NAME_TICKETS + " (" +
                        SCRIPT.COLUMN_ID_TICKET_LOCAL + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        SCRIPT.COLUMN_ID_TICKET_SERVER + " INTEGER," +
                        SCRIPT.COLUMN_BUILDING + " TEXT," +
                        SCRIPT.COLUMN_TICKET_TYPE + " INTEGER," +
                        SCRIPT.COLUMN_REAR_LICENCE_PLATE + " TEXT," +
                        SCRIPT.COLUMN_INCREASE + " FLOAT," +
                        SCRIPT.COLUMN_CAPACITY + " FLOAT," +
                        SCRIPT.COLUMN_MATERIAL_ID + " INTEGER," +
                        SCRIPT.COLUMN_ORIGIN_POINT + " INTEGER," +
                        SCRIPT.COLUMN_EXIT_COORDINATES + " TEXT," +
                        SCRIPT.COLUMN_DISCOUNT + " FLOAT," +
                        SCRIPT.COLUMN_EXIT_DATE + " DATETIME," +
                        SCRIPT.COLUMN_USER_ID_BANK + " INTEGER," +
                        SCRIPT.COLUMN_USERNAME_BANK + " TEXT," +
                        SCRIPT.COLUMN_SHEET_NUMBER + " TEXT," +
                        SCRIPT.COLUMN_DISTANCE + " FLOAT," +
                        SCRIPT.COLUMN_ARRIVAL_DATE + " DATETIME," +
                        SCRIPT.COLUMN_USER_ID_THROW + " INTEGER," +
                        SCRIPT.COLUMN_USERNAME_THROW + " TEXT," +
                        SCRIPT.COLUMN_ARRIVAL_COORDINATES + " TEXT," +
                        SCRIPT.COLUMN_DESTINY_POINT + " INTEGER," +
                        SCRIPT.COLUMN_EXPIRATION_DATE + " DATETIME," +
                        SCRIPT.COLUMN_UNIT_OF_MEASURE + " TEXT," +
                        SCRIPT.COLUMN_ADD_USER + " INTEGER," +
                        SCRIPT.COLUMN_ADD_DATE + " DATETIME," +
                        SCRIPT.COLUMN_UPD_DATE + " DATETIME," +
                        SCRIPT.COLUMN_ESTATUS_SERVER + " TEXT," +
                        SCRIPT.COLUMN_UPLOAD_DATE + " DATETIME," +
                        SCRIPT.COLUMN_UPLOAD_ESTATUS + " TEXT," +
                        SCRIPT.COLUMN_DONWLOAD_DATE + " DATETIME);";

        public static final String TABLE_NAME_REPRINTS = "reprints";
        public static final String COLUMN_COORDINATES = "coordinates";
        public static final String COLUMN_ID_REPRINT_LOCAL = "id_reprint";

        public static final String COLUMN_ID_REPRINT_SERVER = "id_reprint_server";



        public static final String SQL_CREATE_ENTRIES_REPRINT =
                "CREATE TABLE " + SCRIPT.TABLE_NAME_REPRINTS + " (" +
                        SCRIPT.COLUMN_ID_REPRINT_LOCAL + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        SCRIPT.COLUMN_ID_REPRINT_SERVER + " INTEGER," +
                        SCRIPT.COLUMN_ADD_USER + " INTEGER," +
                        SCRIPT.COLUMN_ADD_DATE + " DATETIME," +
                        SCRIPT.COLUMN_SHEET_NUMBER + " TEXT," +
                        SCRIPT.COLUMN_COORDINATES + " TEXT," +
                        SCRIPT.COLUMN_UPLOAD_DATE + " DATETIME," +
                        SCRIPT.COLUMN_UPLOAD_ESTATUS + " TEXT);";

        public static final String TABLE_NAME_KEYS = "keys";
        public static final String COLUMN_ID_KEY_LOCAL = "id_key_local";
        public static final String COLUMN_ID_KEY_SERVER = "id_key_server";
        public static final String COLUMN_KEY_A = "key_a";
        public static final String COLUMN_KEY_B = "key_b";
        public static final String COLUMN_SECTOR = "sector";
        public static final String COLUMN_VERSION = "version";

        public static final String SQL_CREATE_ENTRIES_KEYS =
                "CREATE TABLE " + SCRIPT.TABLE_NAME_KEYS + " (" +
                        SCRIPT.COLUMN_ID_KEY_LOCAL + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        SCRIPT.COLUMN_ID_KEY_SERVER + " INTEGER," +
                        SCRIPT.COLUMN_ADD_USER + " INTEGER," +
                        SCRIPT.COLUMN_ADD_DATE + " DATETIME," +
                        SCRIPT.COLUMN_KEY_A + " TEXT," +
                        SCRIPT.COLUMN_KEY_B+ " TEXT," +
                        SCRIPT.COLUMN_SECTOR+" INTEGER,"+
                        SCRIPT.COLUMN_VERSION+" INTEGER,"+
                        SCRIPT.COLUMN_UPD_DATE + " DATETIME," +
                        SCRIPT.COLUMN_UPLOAD_DATE + " DATETIME," +
                        SCRIPT.COLUMN_UPLOAD_ESTATUS + " TEXT);";

        public static final String COLUMN_ID_DISTANCE_LOCAL = "id_distance_local";

        public static final String COLUMN_ID_DISTANCE_SERVER = "id_distance_server";

        public static final String SQL_CREATE_ENTRIES_DISTANCES_BY_POINT =
                "CREATE TABLE " + SCRIPT.TABLE_NAME_DISTANCES_BY_POINT + " (" +
                        SCRIPT.COLUMN_ID_DISTANCE_LOCAL + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        SCRIPT.COLUMN_ID_DISTANCE_SERVER + " INTEGER," +
                        SCRIPT.COLUMN_ID_POINT_SERVER + " INTEGER," +
                        SCRIPT.COLUMN_DISTANCE + " FLOAT," +
                        SCRIPT.COLUMN_ADD_USER + " INTEGER," +
                        SCRIPT.COLUMN_ADD_DATE + " DATETIME," +
                        SCRIPT.COLUMN_UPD_DATE + " DATETIME," +
                        SCRIPT.COLUMN_ESTATUS_SERVER + " TEXT," +
                        SCRIPT.COLUMN_UPLOAD_DATE + " DATETIME," +
                        SCRIPT.COLUMN_UPLOAD_ESTATUS + " TEXT," +
                        SCRIPT.COLUMN_DONWLOAD_DATE + " DATETIME);";

    }

}