package com.shikhar.inventory_mgt;

import android.Manifest;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText itemName;
    private EditText itemPrice;
    private EditText itemQuantity;
    private EditText supplierName;
    private EditText supplierPhone;
    private EditText supplierEmail;
    private Button plusButton;
    private Button minusButton;
    private Button addImageButton;

    int MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1;

    private static final int EXISTING_ITEM_LOADER = 0;

    private boolean mItemHasChanged = false;

    /** Content URI for the existing item (null if it's a new item) */
    private Uri mCurrentItemUri;

    // OnTouchListener that listens for any user touches on a View, implying that user is modifying the view, and we change the mItemHasChanged boolean to true.
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();
        if(mCurrentItemUri == null) {
            setTitle("Add new Item");
           //TODO: invalidateOptionsMenu(); is this necessary as I am already hiding he delete option in onPrepareOptionsMenu
        }
        else {
            setTitle("Edit Item");
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        itemName = (EditText)findViewById(R.id.product_name);
        itemPrice = (EditText)findViewById(R.id.product_price);
        itemQuantity = (EditText)findViewById(R.id.item_quantity);
        supplierName = (EditText)findViewById(R.id.supplier_name);
        supplierPhone = (EditText)findViewById(R.id.supplier_phone);
        supplierEmail = (EditText)findViewById(R.id.supplier_email);
        plusButton = (Button)findViewById(R.id.plus);
        minusButton = (Button)findViewById(R.id.minus);
        addImageButton = (Button)findViewById(R.id.add_image_button);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        itemName.setOnTouchListener(mTouchListener);
        itemPrice.setOnTouchListener(mTouchListener);
        itemQuantity.setOnTouchListener(mTouchListener);
        supplierName.setOnTouchListener(mTouchListener);
        supplierPhone.setOnTouchListener(mTouchListener);
        supplierEmail.setOnTouchListener(mTouchListener);
        plusButton.setOnTouchListener(mTouchListener);
        minusButton.setOnTouchListener(mTouchListener);
        addImageButton.setOnTouchListener(mTouchListener);

        //handle clicks on +, - and add image button
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentQuantity = itemQuantity.getText().toString().trim();

                if (currentQuantity.isEmpty()){
                    itemQuantity.setText("1");
                }
                else if (currentQuantity.equals("100")){
                    Toast.makeText(EditorActivity.this, "Max Quantity Reached", Toast.LENGTH_SHORT).show();
                }
                else{
                    int currentItemQuantity = Integer.parseInt(currentQuantity);
                    itemQuantity.setText(String.valueOf(currentItemQuantity + 1));
                }
            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentQuantity = itemQuantity.getText().toString().trim();

                if (currentQuantity.isEmpty() || currentQuantity.equals("0")){
                    Toast.makeText(EditorActivity.this, "Quantity can't be negative", Toast.LENGTH_SHORT).show();
                }
                else {
                    int currentItemQuantity = Integer.parseInt(currentQuantity);
                    itemQuantity.setText(String.valueOf(currentItemQuantity - 1));
                }
            }
        });

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new Item, hide the "Delete" menu item.
        if (mCurrentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
