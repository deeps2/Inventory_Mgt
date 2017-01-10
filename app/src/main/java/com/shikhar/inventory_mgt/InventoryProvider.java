package com.shikhar.inventory_mgt;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import static com.shikhar.inventory_mgt.InventoryDbHelper.LOG_TAG;

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
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY: //no case for ITEM_ID as we can't have the id of an item which is going to be created
                return insertInventory(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertInventory(Uri uri, ContentValues values){
        //form validation is already done in Editor Activity so just insert the values

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id = db.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        //Notify all listeners that the data has changed for the pet content URI- content://com.example.android.inventory_mgt/inventory
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table, return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case INVENTORY:
                //Notify all listeners that the data has changed
                getContext().getContentResolver().notifyChange(uri, null);

                // Delete all rows that match the selection and selection args
                return database.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);
            case ITEM_ID:
                //Notify all listeners that the data has changed
                getContext().getContentResolver().notifyChange(uri, null);

                // Delete a single row given by the ID in the URI
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return database.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return updateInventory(uri, contentValues, selection, selectionArgs);
            case ITEM_ID:
                // For the ITEM_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateInventory(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateInventory(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        //form validation is already done in Editor Activity so just update the values

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //Notify all listeners that the data has changed for the pet content URI- content://com.example.android.inventory_mgt/inventory
        getContext().getContentResolver().notifyChange(uri, null);

        // Returns the number of database rows affected by the update statement
        return database.update(InventoryContract.InventoryEntry.TABLE_NAME, values, selection, selectionArgs);
    }


    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return InventoryContract.InventoryEntry.CONTENT_LIST_TYPE;
            case ITEM_ID:
                return InventoryContract.InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
