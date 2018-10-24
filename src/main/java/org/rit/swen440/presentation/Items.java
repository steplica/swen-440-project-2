package org.rit.swen440.presentation;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class Items {
    ArrayList<Item> allItems;

    public Items(String dir) {
        allItems = new ArrayList<Item>();
        getItems(dir);
    }

    public void getItems(String dir) {
        File cats = new File(dir);//Must have app running in proper dir
        FilenameFilter fnf = new FilenameFilter() {

            @Override
            public boolean accept(File cats, String name) {
                return !name.toLowerCase().endsWith(".cat");
            }
        };
        File[] fcats = cats.listFiles(fnf);
        for (File f : fcats) {
            //System.out.println("Reading:" + f.getName());
            Item it = new Item(f.getAbsolutePath());
            allItems.add(it);
        }
    }

    public ArrayList<Item> ListItems() {
        return allItems;
    }
}
