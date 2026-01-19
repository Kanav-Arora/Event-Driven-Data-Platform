package com.yourcompany.flink.models;

import java.io.Serializable;

public class InventoryEvent implements Serializable {
    private String source;
    private String inventory_id;
    private int quantity;
    private String updated_at;
    private String snapshot;

    public InventoryEvent() {
    }

    public InventoryEvent(String source, String inventory_id, int quantity, String updated_at, String snapshot) {
        this.source = source;
        this.inventory_id = inventory_id;
        this.quantity = quantity;
        this.updated_at = updated_at;
        this.snapshot = snapshot;
    }

    @Override
    public String toString() {
        return "InventoryEvent{" +
                "source='" + source + '\'' +
                ", inventoryId='" + inventory_id + '\'' +
                ", quantity=" + quantity +
                ", updated_at=" + updated_at +
                ", snapshot=" + snapshot +
                '}';
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getInventoryId() {
        return inventory_id;
    }

    public void setInventoryId(String inventory_id) {
        this.inventory_id = inventory_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUpdatedAt() {
        return updated_at;
    }

    public void setUpdatedAt(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(String snapshot) {
        this.snapshot = snapshot;
    }

}
