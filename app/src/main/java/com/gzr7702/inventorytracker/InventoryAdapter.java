package com.gzr7702.inventorytracker;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gzr7702.inventorytracker.data.InventoryContract;

public class InventoryAdapter extends CursorAdapter {

    public InventoryAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.inventory_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Find individual views that we want to modify in the list item layout
        // TODO: add butterknife
        TextView nameTextView = (TextView) view.findViewById(R.id.item_name);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        ImageView picTextView = (ImageView) view.findViewById(R.id.thumbnail);

        // Find the columns of pet attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRICE);
        int thumbnailColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_THUMBNAIL);

        // Read the pet attributes from the Cursor for the current pet
        String itemName = cursor.getString(nameColumnIndex);
        int quantitiy = cursor.getInt(quantityColumnIndex);
        float price = cursor.getFloat(priceColumnIndex);
        int thumbnail = cursor.getInt(thumbnailColumnIndex);

        // Update the TextViews with the attributes for the current pet
        nameTextView.setText(itemName);
        String quantityString = String.valueOf(quantitiy);
        quantityTextView.setText(quantityString);
        String priceString = String.valueOf(price);
        priceTextView.setText(priceString);

        if (thumbnail == 0) {
            //TODO: change to generic picture
            picTextView.setImageResource(R.drawable.item);
        } else {
            //TODO: change to pic from camera
            picTextView.setImageResource(R.drawable.item);
        }
    }
}


