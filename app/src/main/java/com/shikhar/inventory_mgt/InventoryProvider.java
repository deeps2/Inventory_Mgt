package com.shikhar.inventory_mgt;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class InventoryProvider extends ContentProvider  {

    private InventoryDbHelper mDbHelper;

    private static final int INVENTORY = 100; //for whole inventory table

    private static final int ITEM_ID = 101; //for a specific item inside inventory table

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH); //UriMatcher object to match a content URI to a corresponding code.

    static { // calls to addURI() go here, for all of the content URI patterns that the provider should recognise
        //IMPORTANT: CONTENT_AUTHORITY and 'NOT' BASE_CONTENT_URI(content://com.example.android.inventory_mgt) or CONTENT_URI(content://com.example.android.inventory_mgt/inventory)
        //because syntax requires 3 argument -> [authority(string), path(string), code(int)]  code is UriMatcherCode 100,101 that u will define
        //Content Uri: content://com.android.contacts/contact/contacts         (DataType is Uri)
        //             <scheme>  <content authority>          <type of data>

        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, INVENTORY);  //com.example.android.inventory_mgt/inventory -> 100
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY + "/#", ITEM_ID); //com.example.android.inventory_mgt/inventory/# -> 100
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case ITEM_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        //set notification URI on the cursor so we know what content URI the cursor was created for
        //If the data at this URI changes then we know that we need to update the cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
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
