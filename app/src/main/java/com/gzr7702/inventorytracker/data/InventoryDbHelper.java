package com.gzr7702.inventorytracker.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gzr7702.inventorytracker.data.InventoryContract.InventoryEntry;

/**
 * Helper class for database
 */

public class InventoryDbHelper extends SQLiteOpenHelper{
    static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 2;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE " + InventoryEntry.TABLE_NAME + " (" +
                InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                InventoryEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, " +
                InventoryEntry.COLUMN_COMPANY_NAME+ " TEXT NOT NULL, " +
                InventoryEntry.COLUMN_PHONE_NUMBER+ " TEXT NOT NULL, " +
                InventoryEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " +
                InventoryEntry.COLUMN_PRICE + " DOUBLE NOT NULL DEFAULT 0.00, " +
                InventoryEntry.COLUMN_THUMBNAIL + " INTEGER NOT NULL DEFAULT 0" +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + InventoryEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
