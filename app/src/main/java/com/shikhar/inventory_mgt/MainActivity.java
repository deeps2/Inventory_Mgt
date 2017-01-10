package com.shikhar.inventory_mgt;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.app.LoaderManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int INVENTORY_LOADER = 1;

    InventoryCursorAdapter mCursorAdapter;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryContract.InventoryEntry._ID,   //IMPORTANT: without this onLoadFinished fail(even though i don't want to display it in list_items inside ListView)
                InventoryContract.InventoryEntry.COLUMN_ITEM_IMAGE,
                InventoryContract.InventoryEntry.COLUMN_ITEM_NAME,
                InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE,
                InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this, InventoryContract.InventoryEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //update with new cursor containing updated Inventory data
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

        //setup FAB to open EditorActivity
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
                Uri currentItemUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, id);
                                                                //content://com.example.android.inventory_mgt/inventory + /id
                intent.setData(currentItemUri);
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
                 showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete Whole List??");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getContentResolver().delete(InventoryContract.InventoryEntry.CONTENT_URI, null, null);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //to update the database (decrease quantity by 1) when sale button is clicked in list items
    public void update(Uri currentItemUri, int mQuantity, TextView quantityView){

        int decreasedQuantity = 0;
        if(mQuantity > 0)
            decreasedQuantity = mQuantity - 1;

        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY, decreasedQuantity);

        //this is an EXISTING pet, so update the pet with content URI: mCurrentPetUri
        // and pass in the new ContentValues which has only quantity parameter as this method will be called
        // only when sale button is clicked(which affects only the quantity).
        // Also, Pass in null for the selection and selection args because mCurrentPetUri will already
        // identify the correct row in the database that we want to modify
        getContentResolver().update(currentItemUri, values, null, null);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade);
        quantityView.startAnimation(animation);

        //shortcut: db.update(StockContract.StockEntry.TABLE_NAME, values, selection, selectionArgs);
        //but by doing this you will be doing direct access to DB from the activity.
        //Direct access to DB should be done by ContentProvider(which in this case is InventoryProvider)
    }
}

