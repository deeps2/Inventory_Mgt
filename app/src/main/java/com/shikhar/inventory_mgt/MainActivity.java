package com.shikhar.inventory_mgt;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int INVENTORY_LOADER = 1;

    InventoryCursorAdapter mCursorAdapter;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                //TODO InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_ITEM_IMAGE,
                InventoryContract.InventoryEntry.COLUMN_ITEM_NAME,
                InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE,
                InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this, InventoryContract.InventoryEntry.CONTENT_URI, projection, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //update with new cursor containing updated pet data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Callbacks called when data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup FAB top open EditorActivity
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView inventoryListView  = (ListView)findViewById(R.id.list_view);
        //set the empty view on ListView
        View emptyView = findViewById(R.id.empty_inventory);
        inventoryListView.setEmptyView(emptyView);

        mCursorAdapter = new InventoryCursorAdapter(this, null);
        inventoryListView.setAdapter(mCursorAdapter);

        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);

        //open editor activity when an item is clicked inside list view
        inventoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Intent intent =  new Intent(MainActivity.this, EditorActivity.class);

                //Form the content URI that represents the specific item that was clicked on by appending ID to the CONTENT_URI
                Uri currentitemUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, id);
                                                                //content://com.example.android.inventory_mgt/inventory + /id
                intent.setData(currentitemUri);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                //TODO ###################show dialogue#####################3
                deleteAllItems();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllItems() {
        getContentResolver().delete(InventoryContract.InventoryEntry.CONTENT_URI, null, null);
    }

    //TODO: delete this whole comment later
    //decrease the quantity by 1 when sale image is clicked in list view
   /* public void decreaseQuantity(int mQuantity, long itemId){
        int newQuantity = 0;
        InventoryDbHelper mDbHelper;

        mDbHelper = new InventoryDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        if (mQuantity > 0)
            newQuantity = mQuantity -1;

        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY, newQuantity);

        String selection = InventoryContract.InventoryEntry._ID + "=?";
        String[] selectionArgs = new String[] { String.valueOf(itemId) };

          //try by using content resolver
           // getContentResolver().
        // db.update(StockContract.StockEntry.TABLE_NAME, values, selection, selectionArgs);
    }*/

    //to update the database (decrease quantity by 1) when sale button is clicked in list items
    public void update(Uri currentItemUri, int mQuantity){

        int decreasedQuantity = 0;
        if(mQuantity > 0)
            decreasedQuantity = mQuantity - 1;

        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY, decreasedQuantity);

        //this is an EXISTING pet, so update the pet with content URI: mCurrentPetUri
        // and pass in the new ContentValues which has only quantity parameter as this method will be called
        // only when sale button is clicked(which affects only the quantity).
        // ALso, Pass in null for the selection and selection args because mCurrentPetUri will already
        // identify the correct row in the database that we want to modify
        int rowsAffected = getContentResolver().update(currentItemUri, values, null, null);
        //shortcut: db.update(StockContract.StockEntry.TABLE_NAME, values, selection, selectionArgs);
        //but by doing this you will be doing direct access to DB from the activity.
        //Direct access to DB should be done by ContentProvider(which in this case is InventoryProvider)

        //TODO: write Toast for rows affected ....is there any use of that POJO class, see other inventory code

        //swap adapter
        //TODO: see swapCursor is necessary from here as provider class me setNotificationUri() to hoga hee
        //problem is is pets app i was not updating anything in list_items INSIDE 1st activity
        //but here i am decreasing quantity by clicking on sale button

        //TODO: so swapcursor or setadapter again???
        //because when u come back from editor activity to main activity then listview will be refreshed like petsapp.

        //TODO: or adapter.setnotifydatasetchanged() call???which one to do
        //mCursorAdapter.swapCursor(dbHelper.readStock()); // this needs a cursor..see other inventory app

    }
}

