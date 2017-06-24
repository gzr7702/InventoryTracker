package com.gzr7702.inventorytracker;

import android.content.ContentValues;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gzr7702.inventorytracker.data.InventoryContract;

import butterknife.ButterKnife;


/*
    This activity is used to add a new inventory item
 */

public class NewEntryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        Button addButton = (Button) findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertItem();
            }
        });

    }

    // Temporarily add dummy data for now
    // TODO: get rid of this
    private void insertItem() {
        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME, "Screwdriver");
        values.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, 3);
        values.put(InventoryContract.InventoryEntry.COLUMN_PRICE, 9.99);
        values.put(InventoryContract.InventoryEntry.COLUMN_THUMBNAIL, R.drawable.item);

        Uri newUri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);
    }
}
