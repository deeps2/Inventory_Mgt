package com.shikhar.inventory_mgt;

import android.Manifest;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import static android.R.attr.bitmap;

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
    private ImageView productImage;

    private String picturePath;
    private byte[] imageBytes; //to store image in DB as BLOB

    private static int RESULT_LOAD_IMAGE = 1;

    private static final int EXISTING_ITEM_LOADER = 0;

    private boolean mItemHasChanged = false;

    /** Content URI for the existing item (null if it's a new item) */
    private Uri mCurrentItemUri;

    // Storage Permissions variables
    private static String[] PERMISSIONS_STORAGE = { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE };

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
        productImage = (ImageView)findViewById(R.id.product_image);

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

        //when item quantity field inside EditText is changed (for input validation)
        itemQuantity.addTextChangedListener(new TextValidator(itemQuantity) {
            @Override
            public void validate(TextView textView, String text) {
                /* Validation code here */
                if(!text.isEmpty()) {
                    if (Integer.parseInt(text) > 100 || Integer.parseInt(text) < 0) {
                        Toast.makeText(EditorActivity.this,"Quantity should be between 0-100",Toast.LENGTH_SHORT).show();
                        itemQuantity.setText("0");
                    }
                }
            }
        });

        //handle clicks on +, - and add image button
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentQuantity = itemQuantity.getText().toString().trim();

                if (currentQuantity.isEmpty()){
                    itemQuantity.setText("1");
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

                    if(!currentQuantity.isEmpty()) {
                        int currentItemQuantity = Integer.parseInt(currentQuantity);
                        itemQuantity.setText(String.valueOf(currentItemQuantity - 1));
                    }
                    else
                        itemQuantity.setText("0");
            }
        });

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ask permissions
                ActivityCompat.requestPermissions(EditorActivity.this, PERMISSIONS_STORAGE, 1);
            }
        });

    }

    //get user input and save the item in DB
    private void saveItem(){
        //read from input fields
        String itemNameString = itemName.getText().toString().trim();
        String itemPriceString = itemPrice.getText().toString().trim();
        String itemQuantityString = itemQuantity.getText().toString().trim();
        String supplierNameString = supplierName.getText().toString().trim();
        String supplierPhoneString = supplierPhone.getText().toString().trim();
        String supplierEmailString = supplierEmail.getText().toString().trim();
       // String productImageUri = picturePath;

        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME, itemNameString);

        int itemPriceInt = 0;
        if (!TextUtils.isEmpty(itemPriceString))
            itemPriceInt = Integer.parseInt(itemPriceString);
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE, itemPriceInt);

        int itemQuantityInt = 0;
        if (!TextUtils.isEmpty(itemQuantityString))
            itemQuantityInt = Integer.parseInt(itemQuantityString);
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY, itemQuantityInt);

        values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneString);
        values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_EMAIL, supplierEmailString);

        //have to convert it to BLOB before inserting
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_IMAGE, imageBytes);

        if(mCurrentItemUri == null){//new Item
            Uri newUri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);

            if(newUri == null)
                Toast.makeText(this, "Error while saving new Item", Toast.LENGTH_SHORT);
            else
                Toast.makeText(this, "New Item added", Toast.LENGTH_SHORT);
        }
        else { //existing Item. so update it
            int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);
            if (rowsAffected == 0)
                Toast.makeText(this,"Error with updating Item",Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Item Updated",Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteItem() {
        // Only perform the delete if this is an existing item.
        if (mCurrentItemUri != null) {
            // Call the ContentResolver to delete the Item at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentItemUri
            // content URI already identifies the Item that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);

            if (rowsDeleted == 0)
                Toast.makeText(this, "Error with deleting Item", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Item Deleted", Toast.LENGTH_SHORT).show();

        }
        // Close the activity
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all Item attributes, define a projection that contains all columns from inventory table
        String[] projection = {
               // InventoryContract.InventoryEntry._ID,  //TODO i think not needed
                InventoryContract.InventoryEntry.COLUMN_ITEM_NAME,
                InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE,
                InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE,
                InventoryContract.InventoryEntry.COLUMN_SUPPLIER_EMAIL,
                InventoryContract.InventoryEntry.COLUMN_ITEM_IMAGE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentItemUri,        // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1)
            return;

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor as we are in editor activity for a single Item)

        if (cursor.moveToFirst()) {

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        itemName.setText("");
        itemPrice.setText("");
        itemQuantity.setText("");
        supplierName.setText("");
        supplierEmail.setText("");
        supplierPhone.setText("");
        productImage.setImageResource(R.drawable.add_image);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, Do the related task you need to do.
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                } else {
                    // permission denied
                    Toast.makeText(EditorActivity.this, "Permission denied to read your Int/Ext storage", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //to load the image from gallery into ImageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            productImage.setImageBitmap(bitmap);

            //convert bitmap to byte[] to store in DB later
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            imageBytes = stream.toByteArray();
           // productImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {

            case R.id.action_save:
                saveItem();
                finish();
                return true;

            case R.id.action_delete_item:
                // Pop up confirmation dialog for delete
                showDeleteConfirmationDialog();
                return true;

            case R.id.order_more:
                //TODO open dialog and then intent according to option chosen

            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the Item hasn't changed, continue with navigating up to parent activity
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() { //same as R.id.home above
        // If the Item hasn't changed, continue with handling back button press
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog();
    }

    private void showUnsavedChangesDialog( ) {
        // Create an AlertDialog.Builder and set the message, and click listeners for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing?");

        builder.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners for the postive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this Item?");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new Item, hide the "Delete" menu item.
        if (mCurrentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete_item);
            menuItem.setVisible(false);
        }
        return true;
    }

    //TODO write code when save is clicked and fields are not filled..color them with red or set drawable in edit text or make some view visible "!" vala
    //TODO delete item/ALL options and other options jo bhee hai jaise back pe dialog popup

    //to validate the item quantity inside the EditText
    public abstract class TextValidator implements TextWatcher {
        private final TextView textView; //This will work as every EditText is a TextView as well

        public TextValidator(TextView textView) {
            this.textView = textView;
        }

        public abstract void validate(TextView textView, String text);

        @Override
        final public void afterTextChanged(Editable s) {
            String text = textView.getText().toString().trim();
            validate(textView, text);
        }

        @Override
        final public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* Don't care */ }

        @Override
        final public void onTextChanged(CharSequence s, int start, int before, int count) { /* Don't care */ }
    }
}
