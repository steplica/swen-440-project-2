package org.rit.swen440.presentation;

import org.rit.swen440.control.Controller;
import org.rit.swen440.dataLayer.Product;
import org.rit.swen440.dataLayer.Transaction;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MenuManager {
    int currentLevel = 0;
    String currentCategoryName;
    String currentItemName;
    Category currentCategory;
    Item currentItem;
    private Controller controller;

    public MenuManager() {
        controller = new Controller(System.getProperty("fileSystemRoot"));
    }

    public boolean loadLevel(int level) {
//        System.out.println("Loading level:" + currentLevel);
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
                System.out.println("Returning to main org.rit.swen440.presentation.Menu");
                currentLevel = 0;
                Level0();
                break;
        }

        return false;
    }

    public void Level0() {
        Menu m = new Menu();
        List<String> categories = controller.getCategories();
        m.loadMenu(categories);
        m.addMenuItem("'q' to Quit");
        System.out.println("The following org.rit.swen440.presentation.Categories are available");
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

        //Items it = new Items("orderSys/" + currentCategory.getName());

        // List<Item> itemList = controller.getProducts(currentCategoryName);
        List<String> itemList = controller.getProducts(currentCategoryName);
        List<String> l = new ArrayList<>();
        System.out.println("");
        itemList.forEach(item -> {
            l.add(controller.getProductInformation(currentCategoryName, item, Controller.PRODUCT_FIELD.NAME)
                          + "($" + controller.getProductInformation(currentCategoryName, item, Controller.PRODUCT_FIELD.COST) + ")");
        });

        m.loadMenu(l);
        m.addMenuItem("'q' to quit");
        System.out.println("The following Items are available");
        m.printMenu();
        String result = m.getSelection();
        try {
            int iSel = Integer.parseInt(result);//Item  selected
            currentItemName = itemList.get(iSel);
            //currentItem = itemList.get(iSel);
            //Now read the file and print the org.rit.swen440.presentation.Items in the catalog
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
        currentLevel = 0;
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
            Transaction resultingTransaction = new Transaction(product.getSkuCode(), product.getTitle(), orderCount, totalCost);
            System.out.println("\nThank you for your purchase!");
            System.out.println("You ordered: " + resultingTransaction);
        } catch(NumberFormatException e) {
            System.out.println("System error: invalid number, please try again");
            return false;
        }
        return true;
    }
}
