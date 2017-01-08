package com.shikhar.inventory_mgt;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InventoryDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = InventoryDbHelper.class.getSimpleName();

    //Name of the database file
    private static final String DATABASE_NAME = "inventory.db";
    //Database version. If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the inventory table
        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + InventoryContract.InventoryEntry.TABLE_NAME + " ("
                + InventoryContract.InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryContract.InventoryEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, "
                + InventoryContract.InventoryEntry.COULMN_ITEM_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE + " TEXT NOT NULL, "
                + InventoryContract.InventoryEntry.COLUMN_SUPPLIER_EMAIL + " TEXT NOT NULL, "
                + InventoryContract.InventoryEntry.COLUMN_ITEM_IMAGE + " TEXT NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
