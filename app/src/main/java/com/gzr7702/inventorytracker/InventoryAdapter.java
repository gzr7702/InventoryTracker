package com.gzr7702.inventorytracker;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static com.gzr7702.inventorytracker.data.InventoryProvider.LOG_TAG;

public class InventoryAdapter extends CursorAdapter {
    private static final String TAG = "InventoryAdapter";
    private static final int REDUCE_QUANTITY_LOADER = 1;
    Context mContext;
    ImageView mPicView;

    public InventoryAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.inventory_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.item_name);
        final TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        mPicView = (ImageView) view.findViewById(R.id.thumbnail);
        Button saleButton = (Button) view.findViewById(R.id.sale_button);

        // Find the columns of pet attributes that we're interested in
        int idColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRICE);
        int thumbnailColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PHOTO);

        // Read the item attributes from the Cursor for the current item
        final int itemId = cursor.getInt(idColumnIndex);
        String itemName = cursor.getString(nameColumnIndex);
        final int quantity = cursor.getInt(quantityColumnIndex);
        float price = cursor.getFloat(priceColumnIndex);
        String thumbnailPath = cursor.getString(thumbnailColumnIndex);

        // Update the TextViews with the attributes for the current pet
        nameTextView.setText(itemName);
        final String quantityString = String.valueOf(quantity);
        quantityTextView.setText(quantityString);
        final String priceString = "$" + String.valueOf(price);
        priceTextView.setText(priceString);

        final Uri photoUri = Uri.parse(thumbnailPath);
        mPicView.setImageURI(photoUri);

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0) {
                    int newQuantity = quantity - 1;
                    Uri itemUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, itemId);
                    ReduceQuantity reduceQuantity = new ReduceQuantity(context, itemUri, newQuantity);
                    reduceQuantity.onCreateLoader(REDUCE_QUANTITY_LOADER, null);
                    if (reduceQuantity.getRowsAffected() == 1) {
                        quantityTextView.setText(Integer.toString(newQuantity));
                    }
                } else {
                    String message = "You have no items left to sell!";
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
