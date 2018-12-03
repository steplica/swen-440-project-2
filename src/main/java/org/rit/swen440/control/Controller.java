package org.rit.swen440.control;

import org.rit.swen440.dataLayer.Category;
import org.rit.swen440.dataLayer.Database;
import org.rit.swen440.dataLayer.Product;
import org.rit.swen440.dataLayer.Transaction;
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

    /**
     * Parse a subdirectory and create a product object for each product within it
     *
     * @param path the subdirectory we're working in
     * @return a set of products
     */
    private Set<Product> loadProducts(Path path) {
        DirectoryStream.Filter<Path> productFilter = new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept(Path path) throws IOException {
                return !Files.isDirectory(path) && !path.toString()
                        .toLowerCase()
                        .endsWith("cat");
            }
        };

        Set<Product> products = new HashSet<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, productFilter)) {
            for (Path productFile : stream) {
                // Read the product file
                try (BufferedReader reader = Files.newBufferedReader(productFile, Charset.forName("US-ASCII"))) {
                    Product product = new Product();
                    product.setSkuCode(Integer.valueOf(reader.readLine()));
                    product.setItemCount(Integer.valueOf(reader.readLine()));
                    product.setThreshold(Integer.valueOf(reader.readLine()));
                    product.setReorderAmount(Integer.valueOf(reader.readLine()));
                    product.setTitle(reader.readLine());
                    product.setDescription(reader.readLine());
                    product.setCost(new BigDecimal(reader.readLine()));

                    product.setPath(productFile);

                    products.add(product);
                } catch (Exception e) {
                    // Failed to read a product.  Log the error and continue
                    System.err.println("Failed to read file: " + path.toString());
                }
            }
        } catch (IOException | DirectoryIteratorException e) {
            System.err.println(e);
        }

        return products;
    }

    /**
     * Loop through the set of products and write out any updated products
     *
     * @param products set of products
     */
    private void writeProducts(List<Product> products) {
        for (Product product : products) {
            if (product.isUpdated()) {
                updateProduct(product);
            }
        }
    }

    /**
     * Write an updated product
     *
     * @param product the product
     */
    private void updateProduct(Product product) {
        try (BufferedWriter writer = Files.newBufferedWriter(product.getPath(), Charset.forName("US-ASCII"))) {
            writer.write(String.valueOf(product.getSkuCode()));
            writer.newLine();
            writer.write(String.valueOf(product.getItemCount()));
            writer.newLine();
            writer.write(String.valueOf(product.getThreshold()));
            writer.newLine();
            writer.write(String.valueOf(product.getReorderAmount()));
            writer.newLine();
            writer.write(product.getTitle());
            writer.newLine();
            writer.write(product.getDescription());
            writer.newLine();
            writer.write(product.getCost()
                                 .toString());
        } catch (IOException e) {
            System.err.println("Failed to write product file for:" + product.getTitle());
        }
    }
}
