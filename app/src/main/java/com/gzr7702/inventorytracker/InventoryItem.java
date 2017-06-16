package com.gzr7702.inventorytracker;

public class InventoryItem{
    private String name;
    private int quantity;
    private float price;
    private int itemPic;

    public InventoryItem (String name, int quantity, float price, int itemPic) {
        super();
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.itemPic = itemPic;
    }

    public String getName() {
        return this.name;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public float getPrice() {
        return this.price;
    }

    public int getItemPic() {
        return this.itemPic;
    }

}
