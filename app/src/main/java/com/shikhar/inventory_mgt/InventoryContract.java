package com.shikhar.inventory_mgt;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class InventoryContract {

    // To prevent someone from accidentally instantiating the contract class, give it an empty constructor.
    private InventoryContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.android.inventory_mgt";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_INVENTORY = "inventory";

    public static final class InventoryEntry implements BaseColumns {
                                        //content://com.example.android.inventory_mgt/inventory
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        public final static String TABLE_NAME = "inventory";
        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_ITEM_NAME ="name";
        public final static String COLUMN_ITEM_PRICE = "price";
        public final static String COLUMN_ITEM_QUANTITY = "quantity";
        public final static String COLUMN_SUPPLIER_NAME = "supplier";
        public final static String COLUMN_SUPPLIER_PHONE = "phone";
        public final static String COLUMN_SUPPLIER_EMAIL = "email";
        public final static String COLUMN_ITEM_IMAGE = "image";
        //NOTE: all of these are strings as these are DB, table and column Names. That doesn't mean that all Columns have String datatypes
        //for datatypes of Columns see InventoryDbHelper onCreate();


        //CURSOR_DIR_BASE_TYPE (which maps to the constant "vnd.android.cursor.dir")
        //vnd.android.cursor.dir/com.example.android.inventory_mgt/inventory
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        /// CURSOR_ITEM_BASE_TYPE (which maps to the constant “vnd.android.cursor.item”).
        //vnd.android.cursor.item/com.example.android.inventory_mgt/inventory
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;
    }
}
