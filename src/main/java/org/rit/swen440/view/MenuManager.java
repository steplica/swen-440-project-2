package org.rit.swen440.view;

import org.rit.swen440.controller.Controller;
import org.rit.swen440.model.InsufficientQuantityException;
import org.rit.swen440.model.Product;
import org.rit.swen440.model.Transaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MenuManager {
    private int currentLevel = 0;
    private String currentCategoryName;
    private String currentItemName;
    private Controller controller;

    public MenuManager() {
        controller = new Controller(System.getProperty("fileSystemRoot"));
    }

    public boolean runCommandLineInterface() {
        Menu m = new Menu();
        m.addMenuItem("Make order");
        m.addMenuItem("View transactions");
        m.addMenuItem("'q' to Quit");
        System.out.println("What would you like to do?");
        m.printMenu();

        String result;
        try {
            result = m.getSelection();
        } catch (Exception e) {
            result = "q";
        }
        if (result.equals("q")) {
            return true;
        } else if (result.equals("0")) {
            boolean done;
            do {
                done = orderProductFlow();
            } while (!done);
        } else if (result.equals("1")) {
            viewTransactions();
        }
        return false;
    }

    public void viewTransactions() {
        List<Transaction> transactions = controller.getTransactions();
        System.out.println("Your transactions:");
        for(Transaction transaction: transactions) {
            System.out.println(transaction);
        }
        System.out.println();
    }

    public boolean orderProductFlow() {
        switch (currentLevel) {
            case -1:
                return true;
            case 0:
                Level0();
                break;
            case 1:
                Level1();
                break;
            case 2:
                Level2();
                break;
            default:
                System.out.println("\nReturning to Menu");
                currentLevel = 0;
                return true;
        }

        return false;
    }

    public void Level0() {
        Menu m = new Menu();
        List<String> categories = controller.getCategories();
        m.loadMenu(categories);
        m.addMenuItem("'q' to return to Menu");
        System.out.println("The following categories are available");
        m.printMenu();
        String result = "0";
        try {
            result = m.getSelection();
        } catch (Exception e) {
            result = "q";
        }
        if (Objects.equals(result, "q")) {
            currentLevel--;
        } else {
            currentLevel++;
            int iSel = Integer.parseInt(result);

            currentCategoryName = categories.get(iSel);
            System.out.println("\nYour Selection was:" + currentCategoryName);
        }
    }

    public void Level1() {
        Menu m = new Menu();
        List<String> itemList = controller.getProducts(currentCategoryName);
        List<String> l = new ArrayList<>();
        System.out.println("");
        itemList.forEach(item -> {
            l.add(controller.getProductInformation(currentCategoryName, item, Controller.PRODUCT_FIELD.NAME)
                          + "($" + controller.getProductInformation(currentCategoryName, item, Controller.PRODUCT_FIELD.COST) + ")");
        });

        m.loadMenu(l);
        m.addMenuItem("'q' to quit");
        System.out.println("The following items are available");
        m.printMenu();
        String result = m.getSelection();
        try {
            int iSel = Integer.parseInt(result);//Item  selected
            currentItemName = itemList.get(iSel);
            System.out.println("You want Item from the catalog: " + currentItemName);
        } catch (Exception e) {
            result = "q";
        }
        if (result == "q")
            currentLevel--;
        else {
            boolean orderSuccessful = OrderQty(currentCategoryName, currentItemName);
            if (orderSuccessful) {
                currentLevel++;
            }
        }
    }

    public void Level2() {
        System.out.println("This transaction was added to the database \n");
        currentLevel--;
    }

    public boolean OrderQty(String category, String item) {
        System.out.println("Please select a quantity");
        Optional<Product> maybeProduct = controller.getProduct(category, item);
        if (!maybeProduct.isPresent()) {
            System.out.println("System error: order failed, please try again");
            return false;
        }
        Product product = maybeProduct.get();
        System.out.println(product.getTitle() + " availability:" + product.getItemCount());
        System.out.print(":");
        Menu m = new Menu();
        String result = m.getSelection();
        try {
            Integer orderCount = Integer.parseInt(result);
            BigDecimal totalCost = product.getCost().multiply(new BigDecimal(orderCount));
            Transaction resultingTransaction = new Transaction(category, product.getSkuCode(), product.getTitle(), orderCount, totalCost);
            controller.addTransaction(resultingTransaction);
            System.out.println("\nThank you for your purchase!");
            System.out.println("You ordered: " + resultingTransaction);
        } catch(NumberFormatException e) {
            System.out.println("System error: invalid number, please try again");
            return false;
        } catch (InsufficientQuantityException e) {
            System.out.println("Quantity not available, please choose a different quantity and try again");
            return false;
        }
        return true;
    }
}
