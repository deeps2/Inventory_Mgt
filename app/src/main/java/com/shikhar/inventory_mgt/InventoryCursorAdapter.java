package com.shikhar.inventory_mgt;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import static android.R.attr.id;

public class InventoryCursorAdapter extends CursorAdapter {

    private final MainActivity activity;

    public InventoryCursorAdapter(MainActivity context, Cursor c) {
        super(context, c, 0/*flags*/);
        activity = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {

        ImageView itemImage = (ImageView) view.findViewById(R.id.item_image);
        TextView itemName = (TextView) view.findViewById(R.id.item_name);
        TextView price = (TextView) view.findViewById(R.id.price);
        TextView quantity = (TextView) view.findViewById(R.id.quantity);
        ImageView sale = (ImageView) view.findViewById(R.id.sell);


        itemImage.setImageURI(Uri.parse( //setImage correspoding to Uri
                cursor.getString( //get Value
                        cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_IMAGE //get Column Index
                        )
                )
        ));

        itemName.setText(cursor.getString(
                cursor.getColumnIndex(
                        InventoryContract.InventoryEntry.COLUMN_ITEM_NAME
                )
        ));

        price.setText(String.valueOf( //convert int to String so that it can be populated on TextView
                cursor.getInt( //get the int value
                        cursor.getColumnIndex(
                                InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE
                        )
                )
        ));

        final int mQuantity = cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY));
        quantity.setText(String.valueOf(mQuantity));

        final long itemId = cursor.getLong(cursor.getColumnIndex(InventoryContract.InventoryEntry._ID));
        final Uri currentItemUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, itemId);

        //decrease quantity by 1 when sale image is clicked
        sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  activity.decreaseQuantity(mQuantity, itemId);
                activity.update(currentItemUri, mQuantity);
            }
        });
    }
}
