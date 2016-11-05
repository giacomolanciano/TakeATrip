package com.takeatrip.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.takeatrip.Classes.Profilo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucagiacomelli on 01/03/16.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "TEST DatabaseHandler";


    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "TakeATripDB";

    // Contacts table name
    private static final String TABLE_USERS = "Users";




    // Contacts Table Columns names
    private static final String EMAIL = "id";
    private static final String USER_PWD = "pwd";
    private static final String NAME = "name";
    private static final String SURNAME = "surname";
    private static final String DATE = "date";
    private static final String NATIONALITY = "n";
    private static final String SESSO = "sex";
    private static final String USERNAME = "username";
    private static final String LAVORO = "job";
    private static final String DESCRIZIONE = "descryption";
    private static final String TIPO = "type";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + EMAIL + " TEXT PRIMARY KEY," + USER_PWD + " TEXT,"
                + NAME + " TEXT," + SURNAME + " TEXT,"+ DATE + " TEXT,"+ NATIONALITY + " TEXT," + SESSO + " TEXT,"
                + USERNAME + " TEXT," + LAVORO + " TEXT," + DESCRIZIONE + " TEXT," + TIPO + " TEXT)";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        // Create tables again
        onCreate(db);
    }

    public void addUser(Profilo profilo, String s) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(EMAIL, profilo.getEmail()); // User Name
        values.put(USER_PWD, s);
        values.put(NAME, profilo.getName());
        values.put(SURNAME, profilo.getSurname());
        values.put(DATE, profilo.getDataNascita());
        values.put(NATIONALITY, profilo.getNazionalita());
        values.put(SESSO, profilo.getSesso());
        values.put(USERNAME, profilo.getUsername());
        values.put(LAVORO, profilo.getLavoro());
        values.put(DESCRIZIONE, profilo.getDescrizione());
        values.put(TIPO, profilo.getTipo());


        Log.i(TAG, "contenuto db locale:" + db.toString());

        // Inserting Row
        db.insert(TABLE_USERS, null, values);

        Log.i(TAG, "contenuto db locale:" + db.toString());

        db.close(); // Closing database connection
    }

    public List<Profilo> getAllContacts() {
        List<Profilo> contactList = new ArrayList<Profilo>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_USERS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        Log.i(TAG, "contenuto db locale:" + db.toString());


        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Log.i(TAG, "contenuto cursore 0:" + cursor.getColumnName(0));
                Log.i(TAG, "contenuto cursore 1:" + cursor.getColumnName(1));
                Log.i(TAG, "contenuto cursore 2:" + cursor.getColumnName(2));
                Log.i(TAG, "contenuto cursore 3:" + cursor.getColumnName(3));
                Log.i(TAG, "contenuto cursore 4:" + cursor.getColumnName(4));
                Log.i(TAG, "contenuto cursore 5:" + cursor.getColumnName(5));
                Log.i(TAG, "contenuto cursore 6:" + cursor.getColumnName(6));
                Log.i(TAG, "contenuto cursore 7:" + cursor.getColumnName(7));
                Log.i(TAG, "contenuto cursore 8:" + cursor.getColumnName(8));
                Log.i(TAG, "contenuto cursore 9:" + cursor.getColumnName(9));
                Log.i(TAG, "contenuto cursore 10:" + cursor.getColumnName(10));
                Log.i(TAG, "count cursore :" + cursor.getCount());





                Profilo profile = new Profilo();
                profile.setEmail(cursor.getString(0));
                profile.setPassword(cursor.getString(1));
                profile.setName(cursor.getString(2));
                profile.setSurname(cursor.getString(3));

                profile.setUsername(cursor.getString(7));
                profile.setNazionalita(cursor.getString(5));
                profile.setSesso(cursor.getString(6));
                profile.setDataNascita(cursor.getString(4));

                profile.setLavoro(cursor.getString(8));
                profile.setDescrizione(cursor.getString(9));
                profile.setTipo(cursor.getString(10));


                // Adding contact to list
                contactList.add(profile);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    // Updating single contact
    public int updateContact(Profilo profilo, String s) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(EMAIL, profilo.getEmail()); // User Name
        values.put(USER_PWD, s);
        values.put(NAME, profilo.getName());
        values.put(SURNAME, profilo.getSurname());
        values.put(DATE, profilo.getDataNascita());
        values.put(NATIONALITY, profilo.getNazionalita());
        values.put(SESSO, profilo.getSesso());
        values.put(USERNAME, profilo.getUsername());
        values.put(LAVORO, profilo.getLavoro());
        values.put(DESCRIZIONE, profilo.getDescrizione());
        values.put(TIPO, profilo.getTipo());

        // updating row
        return db.update(TABLE_USERS, values, EMAIL + " = ?",
                new String[] { String.valueOf(profilo.getEmail()) });
    }



    public void deleteContact(Profilo p) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, EMAIL + " = ?",
                new String[]{String.valueOf(p.getEmail())});
        db.close();
    }


}