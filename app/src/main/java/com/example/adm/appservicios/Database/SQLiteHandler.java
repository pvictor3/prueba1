package com.example.adm.appservicios.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by bustrack on 20/2/18.
 */

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    //Database Name
    private static final String DATABASE_NAME = "android_api";

    //Database Version
    private static final int DATABASE_VERSION = 1;

    //Login table name
    private  static final String TABLE_USER = "user";
    //Docs table name
    private  static final String TABLE_DOCS = "docs";
    //Direcciones table name
    private static final String TABLE_ADDRESS = "addresses";

    //Login TAble Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_TELEFONO = "telefono";
    private static final String KEY_UID = "uid";
    private static final String KEY_DOCS_STATE = "documentos";
    private static final String KEY_PROFILE_TYPE = "profile_type";
    private static final String KEY_PROFILE_PICTURE = "profile_picture";
    private static final String KEY_LOG = "logueado";

    /*Crear tabla de Usuario*/
    private static final String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + KEY_ID + " INTEGER PRIMARY KEY, "
            + KEY_NAME + " TEXT, "
            + KEY_TELEFONO + " TEXT UNIQUE, "
            + KEY_UID + " TEXT, "
            + KEY_DOCS_STATE + " TEXT, "
            + KEY_PROFILE_TYPE + " TEXT, "
            + KEY_PROFILE_PICTURE + " BLOB,"
            + KEY_LOG + " TEXT"
            + ");";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //aquí creamos la tabla de usuario
        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "CREATE_LAST_ITEM_TABLE created");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        //Create tables again
        onCreate(db);

    }

    /**
     * Storing user details in database
     */
    public void addUser(String name, String telefono, String uid, String profile_type, String log){
        Log.i("insert:", "insertando");
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_TELEFONO, telefono);
        values.put(KEY_UID, uid);
        values.put(KEY_PROFILE_TYPE, profile_type);
        values.put(KEY_LOG, log);

        long id = db.insert(TABLE_USER, null, values);
        //db.close(); //Closing database connection

        Log.i("informacion", "New user inserted into sqlite" +id);
    }
}
