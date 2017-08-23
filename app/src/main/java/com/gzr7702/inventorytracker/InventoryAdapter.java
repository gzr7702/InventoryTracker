package com.gzr7702.inventorytracker;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gzr7702.inventorytracker.data.InventoryContract;

import java.io.File;

public class InventoryAdapter extends CursorAdapter {
    private ReduceQuantity mListener;

    public InventoryAdapter(Context context, Cursor cursor, ReduceQuantity listener) {
        super(context, cursor);
        mListener = listener;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.inventory_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        // Find individual views that we want to modify in the list item layout
        // TODO: add butterknife
        TextView nameTextView = (TextView) view.findViewById(R.id.item_name);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        ImageView picTextView = (ImageView) view.findViewById(R.id.thumbnail);
        Button saleButton = (Button) view.findViewById(R.id.sale_button);

        // Find the columns of pet attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRICE);
        int thumbnailColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PHOTO);

        // Read the pet attributes from the Cursor for the current pet
        String itemName = cursor.getString(nameColumnIndex);
        final int quantity = cursor.getInt(quantityColumnIndex);
        float price = cursor.getFloat(priceColumnIndex);
        String thumbnail = cursor.getString(thumbnailColumnIndex);

        // Update the TextViews with the attributes for the current pet
        nameTextView.setText(itemName);
        final String quantityString = String.valueOf(quantity);
        quantityTextView.setText(quantityString);
        final String priceString = "$" + String.valueOf(price);
        priceTextView.setText(priceString);

        if (thumbnail == null) {
            picTextView.setImageResource(R.drawable.item);
        } else {
            // TODO: not displaying photo
            String message = "path: " + thumbnail;
            Log.v("InventoryAdapter", message);
            File imgFile = new  File(thumbnail);

            if(imgFile.exists()){
                Bitmap bitmapPhoto = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                picTextView.setImageBitmap(bitmapPhoto);
            }
        }

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0) {
                    mListener.reduce(quantity, cursor);
                } else {
                    String message = "You have no items left to sell!";
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
