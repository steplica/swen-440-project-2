package org.rit.swen440.control;

import org.rit.swen440.dataLayer.Category;
import org.rit.swen440.dataLayer.Database;
import org.rit.swen440.dataLayer.Product;
import org.rit.swen440.util.FileUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controls access to data, on start-up scans directories and builds internal
 * representation of Categories and Items within each Category.  Isolates the
 * Categories and products from information on the underlying file system.
 */
public class Controller {
    private static final String DATABASE_FILENAME = "database.json";

    private List<Category> categories = new ArrayList<>();

    public enum PRODUCT_FIELD {
        NAME,
        DESCRIPTION,
        COST,
        INVENTORY
    }

    public Controller(String directory) {
        loadCategories(directory);
    }

    /**
     * Load the Category information
     *
     * @param directory root directory
     */
    private void loadCategories(String directory) {
        final String databaseFilepath = directory + "/" + DATABASE_FILENAME;
        Database database = FileUtils.readJsonAsObject(databaseFilepath, Database.class);
        categories = database.getCategories();
    }

    /**
     * Get a list of all Category names
     *
     * @return list of Categories
     */
    public List<String> getCategories() {
        return categories.stream()
                .map(Category::getName)
                .collect(Collectors.toList());
    }

    /**
     * Get the description of the named Category
     *
     * @param category name
     * @return description
     */
    public String getCategoryDescription(String category) {
        Optional<Category> match = categories.stream()
                .filter(c -> c.getName()
                        .equalsIgnoreCase(category))
                .findFirst();
        return match.map(Category::getDescription)
                .orElse(null);
    }

    /**
     * Return a list of Products based on the provided Category.
     *
     * @param categoryName Name of Category to use
     * @return List of Products in the Category
     */
    public List<String> getProducts(String categoryName) {
        Optional<Category> category = findCategory(categoryName);

        return category.map(c -> c.getProducts()
                .stream()
                .map(Product::getTitle)
                .collect(Collectors.toList()))
                .orElse(null);
    }

    public String getProductInformation(String category, String product, PRODUCT_FIELD field) {
        Optional<Product> selectedProduct = getProduct(category, product);
        switch (field) {
            case NAME:
                return selectedProduct.map(Product::getTitle)
                        .orElse(null);
            case DESCRIPTION:
                return selectedProduct.map(Product::getDescription)
                        .orElse(null);
            case COST:
                return selectedProduct.map(p -> String.format("%.2f", p.getCost()))
                        .orElse(null);
            case INVENTORY:
                return selectedProduct.map(p -> String.valueOf(p.getItemCount()))
                        .orElse(null);
        }

        return null;
    }

    /**
     * Get the Category that matches the provided Category name
     *
     * @param name
     * @return Category, if present
     */
    private Optional<Category> findCategory(String name) {
        return categories.stream()
                .filter(c -> c.getName()
                        .equalsIgnoreCase(name))
                .findFirst();
    }

    private Optional<Product> getProduct(String category, String product) {
        return findCategory(category).map(c -> c.findProduct(product))
                .orElse(null);
    }
}
