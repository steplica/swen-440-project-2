package org.rit.swen440.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.text.DecimalFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    private static DecimalFormat costFormat = new DecimalFormat("#,###.00");

    private String itemCategory;
    private int itemSkuCode;
    private String itemTitle;
    private int quantity;
    private BigDecimal cost;

    @Override
    public String toString() {
        return String.format("%d %s (category %s, sku %d) for a total of $%s", quantity, itemTitle, itemCategory, itemSkuCode, costFormat.format(cost));
    }
}
