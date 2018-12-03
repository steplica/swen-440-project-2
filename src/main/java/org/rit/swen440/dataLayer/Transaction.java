package org.rit.swen440.dataLayer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    private int itemSkuCode;
    private String itemTitle;
    private int quantity;
    private BigDecimal cost;
}
