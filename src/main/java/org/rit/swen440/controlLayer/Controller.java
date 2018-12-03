package org.rit.swen440.controlLayer;

import org.rit.swen440.dataLayer.Category;
import org.rit.swen440.persistenceLayer.Database;
import org.rit.swen440.dataLayer.Product;
import org.rit.swen440.dataLayer.Transaction;
import org.rit.swen440.util.FileUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controls access to data, on start-up scans directories and builds internal
 * representation of Categories and Items within each Category.  Isolates the
 * Categories and products from information on the underlying file system.
 */
public class Controller {
    private static Database DATABASE;

    public enum PRODUCT_FIELD {
        NAME,
        DESCRIPTION,
        COST,
        INVENTORY
    }

    public Controller(String directory) {
        initializeDatabase(directory);
    }

    private static void initializeDatabase(String directory) {
        if (DATABASE == null) {
            String databaseFilepath = directory + "/database.json";
            DATABASE = FileUtils.readJsonAsObject(databaseFilepath, Database.class);
            DATABASE.setDatabaseFilepath(databaseFilepath);
        }
    }

    /**
     * Get a list of all Category names
     *
     * @return list of Categories
     */
    public List<String> getCategories() {
        return DATABASE.getCategories()
                .stream()
                .map(Category::getName)
                .collect(Collectors.toList());
    }

    /**
     * get a list of transactions in the system
     *
     * @return a list of transactions
     */
    public List<Transaction> getTransactions() {
        return DATABASE.getTransactions();
    }

    /**
     * Get the description of the named Category
     *
     * @param category name
     * @return description
     */
    public String getCategoryDescription(String category) {
        Optional<Category> match = DATABASE.getCategories()
                .stream()
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

    public void addTransaction(Transaction transaction) {
        DATABASE.addTransaction(transaction);
    }

    /**
     * Get the Category that matches the provided Category name
     *
     * @param name
     * @return Category, if present
     */
    private Optional<Category> findCategory(String name) {
        return DATABASE.getCategories()
                .stream()
                .filter(c -> c.getName()
                        .equalsIgnoreCase(name))
                .findFirst();
    }

    public Optional<Product> getProduct(String category, String product) {
        return findCategory(category).map(c -> c.findProduct(product))
                .orElse(null);
    }
}
