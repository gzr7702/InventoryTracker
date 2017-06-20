package com.gzr7702.inventorytracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    ListView mListView;
    InventoryAdapter mAdapter;
    //ArrayList<InventoryItem> dummyList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), NewEntryActivity.class);
                startActivity(i);
            }
        });

        mListView = (ListView) findViewById(R.id.inventory_list);

        // Account for empty view
        // TODO: uncomment to implement empty view
        //TextView emptyStateTextView = (TextView) findViewById(R.id.empty_view);
        //listView.setEmptyView(emptyStateTextView);

        // ListView Item Click Listener
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String itemValue = (String) mListView.getItemAtPosition(position);

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
                        .show();

            }

        });

        /*
        dummyList.add(new InventoryItem("Flibbit", 10, 3.33, R.drawable.item));
        dummyList.add(new InventoryItem("Item Number 12", 1, 23.33, R.drawable.item));
        dummyList.add(new InventoryItem("Tic Tac", 33, 9.99, R.drawable.item));
        dummyList.add(new InventoryItem("Drawer", 14, 13.99, R.drawable.item));

        mAdapter = new InventoryAdapter(this.getApplicationContext(), dummyList);
        mListView.setAdapter(mAdapter);
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
