package org.rit.swen440.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A product Category supported by the Order System
 */
@Data
public class Category {
    private String name;
    private String description;

    private Map<String, Product> products;

    public Optional<Product> findProduct(String name) {
        return products.entrySet()
                .stream()
                .filter(entry -> entry.getValue().getTitle().equalsIgnoreCase(name))
                .map(Map.Entry::getValue)
                .findFirst();
    }
}
