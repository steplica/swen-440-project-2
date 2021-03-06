package org.rit.swen440;

import org.rit.swen440.view.MenuManager;

import java.io.FileInputStream;
import java.io.IOException;

public class Application {

    public static void main(String[] args) {
        try {
            System.getProperties()
                    .load(new FileInputStream("orderSys.properties"));
            MenuManager mgr = new MenuManager();
            boolean done;
            do {
                done = mgr.runCommandLineInterface();
            } while (!done);

            System.out.println("Thank you for shopping at Hippolyta.com!");
        } catch (IOException e) {
            System.err.println("orderSys.properties not found.");
        }
    }
}
