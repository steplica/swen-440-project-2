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
    @Getter
    private int skuCode;
    @Getter
    private int itemCount;
    @Getter
    private int threshold;
    @Getter
    private int reorderAmount;
    @Getter
    private String title;
    @Getter
    private String description;
    @Getter
    private BigDecimal cost;
}
