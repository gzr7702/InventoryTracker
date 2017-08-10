package com.gzr7702.inventorytracker;

import android.database.Cursor;

/**
 * Created by rob on 7/27/17.
 */

public interface ReduceQuantity {
    public void reduce(int quantity, Cursor cursor);
}
