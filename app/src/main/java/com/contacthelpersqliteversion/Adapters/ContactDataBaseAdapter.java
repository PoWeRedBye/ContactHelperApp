package com.contacthelpersqliteversion.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.content.ContentValues;

import com.contacthelpersqliteversion.Model.Contact;

import java.util.ArrayList;
import java.util.List;


public class ContactDataBaseAdapter {

    private static final String TAG = "ContactDataBaseAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    public class MyFieldDB {
        public static final String ROWID = "id";
        public static final String NAME = "name";
        public static final String PHONE = "phone";
        public static final String MAIL = "mail";
        public static final String IMAGE = "photo";


        private static final String DATABASE_NAME = "PhoneBook";
        private static final String SQLITE_TABLE = "Contact";
        private static final int DATABASE_VERSION = 1;


        private static final String DATABASE_CREATE =
                "CREATE TABLE IF NOT EXISTS " + SQLITE_TABLE + " (" +
                        ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        NAME + "," +
                        PHONE + "," +
                        MAIL + "," +
                        IMAGE + "," +
                        " UNIQUE (" + ROWID + "));";

    }

    private final Context mCtx;


    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, MyFieldDB.DATABASE_NAME, null, MyFieldDB.DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, MyFieldDB.DATABASE_CREATE);
            db.execSQL(MyFieldDB.DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + MyFieldDB.SQLITE_TABLE);
            onCreate(db);
        }
    }

    public ContactDataBaseAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public ContactDataBaseAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    public long createContact(String name, String phone, String mail, String image) {

        ContentValues initialValues = new ContentValues();

        initialValues.put(MyFieldDB.NAME, name);
        initialValues.put(MyFieldDB.PHONE, phone);
        initialValues.put(MyFieldDB.MAIL, mail);
        initialValues.put(MyFieldDB.IMAGE, image);


        return mDb.insert(MyFieldDB.SQLITE_TABLE, null, initialValues);
    }

    public long updateContact(int id, String name,
                              String phone, String mail, String image) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MyFieldDB.NAME, name);
            contentValues.put(MyFieldDB.PHONE, phone);
            contentValues.put(MyFieldDB.MAIL, mail);
            contentValues.put(MyFieldDB.IMAGE, image);

            return mDb.update(MyFieldDB.SQLITE_TABLE, contentValues, MyFieldDB.ROWID + " =?", new String[]{String.valueOf(id)});
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void delRec(long id) {
        mDb.delete(MyFieldDB.SQLITE_TABLE, MyFieldDB.ROWID + " = " + id, null);
    }

    public List<Contact> getDataFromDB() {
        List<Contact> contactList = new ArrayList<Contact>();
        String query = "select * from " + MyFieldDB.SQLITE_TABLE;
        mDb = mDbHelper.getWritableDatabase();
        Cursor cursor = mDb.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(cursor.getString(0));
                contact.setName(cursor.getString(1));
                contact.setPhone(cursor.getString(2));
                contact.setMail(cursor.getString(3));
                contact.setPhoto(cursor.getString(4));

                contactList.add(contact);

            } while (cursor.moveToNext());
        }


        Log.d("contact data", contactList.toString());


        return contactList;
    }

}




