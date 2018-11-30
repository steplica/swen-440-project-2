package org.rit.swen440.dataLayer;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A product Category supported by the Order System
 */
@Data
public class Category {
    private String name;
    private String description;

    private List<Product> products = new ArrayList<>();

    public Optional<Product> findProduct(String name) {
        return products.stream()
                       .filter(p -> p.getTitle().equalsIgnoreCase(name))
                       .findFirst();
    }
}
