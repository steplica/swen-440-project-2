package org.rit.swen440;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rit.swen440.controller.Controller;
import org.rit.swen440.model.Product;
import org.rit.swen440.model.Transaction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static junit.framework.TestCase.fail;

public class PerformanceTest {

    public static final String TEST_DATABASE_DIRECTORY = Paths.get("orderSys/test")
            .toString();
    public static final Path TEST_DATABASE_PATH = Paths.get(TEST_DATABASE_DIRECTORY + "/database.json");
    public static final Path BACKUP_DATABASE_PATH = Paths.get(TEST_DATABASE_DIRECTORY + "/database.json.backup");
    public static final String TEST_CATEGORY_NAME = "TestCategory";
    public static final String TEST_PRODUCT_NAME = "Gucci Gang - Lil Pump";

    public static final int CREATE_N_TRANSACTIONS = 10_000_000;

    public static Controller controller;
    public static Product testProduct;

    @BeforeClass
    public static void setup() throws IOException {
        Files.copy(BACKUP_DATABASE_PATH, TEST_DATABASE_PATH, StandardCopyOption.REPLACE_EXISTING);
        controller = new Controller(TEST_DATABASE_DIRECTORY);
        testProduct = controller.getProduct(TEST_CATEGORY_NAME, TEST_PRODUCT_NAME)
                .get();
    }

    @AfterClass
    public static void cleanup() throws IOException {
        Files.delete(TEST_DATABASE_PATH);
    }

    private static void createTestTransaction() {
        Transaction testTransaction = Transaction.builder()
                .itemCategory("TestCategory")
                .itemSkuCode(1)
                .itemTitle(testProduct.getTitle())
                .quantity(1)
                .cost(testProduct.getCost())
                .build();
        controller.addTransaction(testTransaction);
    }

    private static void logElapsedTime(String action, long elapsedNanoTime) {
        System.out.printf("Elapsed time to %s: %f seconds\n", action, elapsedNanoTime / 1_000_000_000.0);
    }

    /**
     * adds transaction entries to the database then queries them and checks how long each operation takes
     */
    @Test
    public void performanceTest() {
        // ensure empty transactions database
        List<Transaction> startingTransactions = controller.getTransactions();
        if (startingTransactions.size() > 0) {
            fail("Transactions already existed in the database before test");
        }

        // measure create performance
        long startCreateTime = System.nanoTime();
        for (int i = 0; i < CREATE_N_TRANSACTIONS; i++) {
            createTestTransaction();
        }

        long elapsedCreateTime = System.nanoTime() - startCreateTime;
        logElapsedTime("create " + CREATE_N_TRANSACTIONS + " transactions", elapsedCreateTime);

        // measure query performance
        long startQueryTime = System.nanoTime();
        List<Transaction> createdTransactions = controller.getTransactions();
        long elapsedQueryTime = System.nanoTime() - startQueryTime;
        if (createdTransactions.size() != CREATE_N_TRANSACTIONS) {
            fail("Did not create all " + CREATE_N_TRANSACTIONS + " transactions");
        }
        logElapsedTime("query " + CREATE_N_TRANSACTIONS + " transactions", elapsedQueryTime);
    }
}
