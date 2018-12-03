package org.rit.swen440.dataLayer;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.nio.file.Path;

/**
 * A record of each product type
 */
@Data
public class Product {
    private int skuCode;
    private int itemCount;
    private int threshold;
    private int reorderAmount;
    private String title;
    private String description;
    private BigDecimal cost;
}
