package org.rit.swen440.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Menu {
    private List<String> menuList;
    Scanner sc;

    public Menu() {
        sc = new Scanner(System.in);
    }

    public void loadMenu(List<String> menuItems) {
        menuList = menuItems;
    }

    public void addMenuItem(String item) {
        if (menuList == null) {
            menuList = new ArrayList<>();
        }
        menuList.add(item);
    }

    public void printMenu() {
        System.out.println("");
        for (int i = 0; i < menuList.size(); i++) {
            System.out.println(i + ": " + menuList.get(i));
        }
        System.out.println("");
    }

    public String getSelection() {
        String result = "x";

        sc.reset();
        result = sc.nextLine();
        return result;
    }
}
