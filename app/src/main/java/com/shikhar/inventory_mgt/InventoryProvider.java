package com.shikhar.inventory_mgt;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class InventoryProvider extends ContentProvider  {

    private InventoryDbHelper mDbHelper;

    private static final int INVENTORY = 100; //for whole inventory table

    private static final int ITEM_ID = 101; //for a specific item inside inventory table

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH); //UriMatcher object to match a content URI to a corresponding code.

    static { // calls to addURI() go here, for all of the content URI patterns that the provider should recognise
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, INVENTORY);  //com.example.android.inventory_mgt/inventory -> 100
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY + "/#", ITEM_ID); //com.example.android.inventory_mgt/inventory/# -> 100
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
