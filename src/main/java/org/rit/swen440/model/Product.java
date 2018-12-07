package org.rit.swen440.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;

/**
 * A record of each product type
 */
@Data
public class Product {
    @JsonIgnore
    private int skuCode;
    private int itemCount;
    private int threshold;
    private int reorderAmount;
    private String title;
    private String description;
    private BigDecimal cost;
}
