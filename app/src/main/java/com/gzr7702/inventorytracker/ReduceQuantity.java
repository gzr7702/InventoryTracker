package com.gzr7702.inventorytracker;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.gzr7702.inventorytracker.data.InventoryContract;

/**
 * Loader class used to reduce the quentity in the database when the "sale" button is pressed.
 */

public class ReduceQuantity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    Uri mUri;
    Context mContext;
    int mQuantity;
    int mRowsAffected;

    public ReduceQuantity(Context context, Uri uri, int quantity) {
        mUri = uri;
        mContext = context;
        mQuantity = quantity;
    }

    public int getRowsAffected() {
        return mRowsAffected;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v("ReduceQuantity", "start");

        int newQuantity = mQuantity--;
        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, newQuantity);
        mRowsAffected = mContext.getApplicationContext().getContentResolver().update(mUri, values, null, null);

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
