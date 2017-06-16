package com.gzr7702.inventorytracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InventoryAdapter extends ArrayAdapter<InventoryItem> {

    private final Context context;
    private final ArrayList<InventoryItem> mInventoryArrayList;
    private final String LOG_TAG = InventoryAdapter.class.getSimpleName();

    public InventoryAdapter(Context context, ArrayList<InventoryItem> inventoryArrayList) {

        super(context, R.layout.inventory_item, inventoryArrayList);

        this.context = context;
        this.mInventoryArrayList= inventoryArrayList;
    }

    // Class to use View Holder pattern
    static class ViewHolder {
        @BindView(R.id.item_name) TextView itemName;
        @BindView(R.id.quantity) TextView quantity;
        @BindView(R.id.thumbnail) ImageView picView;
        @BindView(R.id.price) TextView price;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = null;
        ViewHolder holder;

        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            rowView = inflater.inflate(R.layout.inventory_item, parent, false);
            holder = new ViewHolder(rowView);
            rowView.setTag(holder);

            holder.itemName.setText(mInventoryArrayList.get(position).getName());
            Integer quantity = (Integer) mInventoryArrayList.get(position).getQuantity();
            holder.quantity.setText(quantity.toString());
            Float price = (Float) mInventoryArrayList.get(position).getPrice();
            holder.price.setText(price.toString());

            // Dummy photo for now until we do it for realz
            holder.picView.setImageResource(R.drawable.item);


        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        return rowView;
    }
}


