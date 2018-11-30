package org.rit.swen440.dataLayer;

import java.math.BigDecimal;

public class Transaction {

    private int itemSkuCode;
    private int quantity;
    private BigDecimal cost;

    public Transaction(int itemSkuCode, int quantity, BigDecimal cost) {
        this.itemSkuCode = itemSkuCode;
        this.quantity = quantity;
        this.cost = cost;
    }

    public int getItemSkuCode() {
        return itemSkuCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getCost() {
        return cost;
    }

}
