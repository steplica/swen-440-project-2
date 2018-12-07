package org.rit.swen440;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rit.swen440.controlLayer.Controller;
import org.rit.swen440.dataLayer.Product;
import org.rit.swen440.dataLayer.Transaction;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static junit.framework.TestCase.fail;

/*
 *
 */
public class PerformanceTest
{

    public static final String TEST_DATABASE_DIRECTORY = Paths.get("orderSys/test").toString();
    public static final Path TEST_DATABASE_PATH = Paths.get(TEST_DATABASE_DIRECTORY + "/database.json");
    public static final Path BACKUP_DATABASE_PATH = Paths.get(TEST_DATABASE_DIRECTORY + "/database.backup.json");
    public static final String TEST_CATEGORY_NAME = "TestCategory";
    public static final String TEST_PRODUCT_NAME = "Gucci Gang - Lil Pump";

    public static Controller controller;
    public static Product testProduct;

    @BeforeClass
    public static void setup() throws IOException {
        Files.copy(TEST_DATABASE_PATH, BACKUP_DATABASE_PATH, StandardCopyOption.REPLACE_EXISTING);
        controller = new Controller(TEST_DATABASE_DIRECTORY);
        testProduct = controller.getProduct(TEST_CATEGORY_NAME, TEST_PRODUCT_NAME).get();
    }

    @AfterClass
    public static void cleanup() throws IOException {
        Files.copy(BACKUP_DATABASE_PATH, TEST_DATABASE_PATH, StandardCopyOption.REPLACE_EXISTING);
        Files.delete(BACKUP_DATABASE_PATH);
    }

    private static void createTestTransaction() {
        Transaction testTransaction = new Transaction(testProduct.getSkuCode(), testProduct.getTitle(), 1, testProduct.getCost());
        controller.addTransaction(testTransaction);
    }

    private static void logElapsedTime(String action, long elapsedNanoTime) {
        System.out.printf("Elapsed time to %s: %f seconds\n", action, elapsedNanoTime / 1_000_000_000.0);
    }

    /**
     * adds 500 transaction entries to the database then queries them and checks how long each operation takes
     */
    @Test
    public void AddFiveHundredAndQuery() {
        // ensure empty transactions database
        List<Transaction> startingTransactions = controller.getTransactions();
        if (startingTransactions.size() > 0) {
            fail("Transactions already existed in the database before test");
        }

        // measure create performance
        long startCreateTime = System.nanoTime();
        for (int i = 0; i < 500; i++) {
            createTestTransaction();
        }
        long elapsedCreateTime = System.nanoTime() - startCreateTime;
        logElapsedTime("create 500 transactions", elapsedCreateTime);

        // measure query performance
        long startQueryTime = System.nanoTime();
        List<Transaction> createdTransactions = controller.getTransactions();
        long elapsedQueryTime = System.nanoTime() - startQueryTime;
        if (createdTransactions.size() != 500) {
            fail("Did not create all 500 transactions");
        }
        logElapsedTime("query 500 transactions", elapsedQueryTime);
    }
}
