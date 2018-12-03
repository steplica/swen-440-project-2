package org.rit.swen440.dataLayer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.text.DecimalFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    private static DecimalFormat costFormat = new DecimalFormat("#,###.00");

    private int itemSkuCode;
    private String itemTitle;
    private int quantity;
    private BigDecimal cost;

    @Override
    public String toString() {
        return String.format("%d %s (sku %d) for a total of $%s", quantity, itemTitle, itemSkuCode, costFormat.format(cost));
    }
}
