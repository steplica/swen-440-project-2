package org.rit.swen440.presentation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

public class Categories {
    ArrayList<Category> allCategories;

    public Categories() {
        allCategories = new ArrayList<Category>();
        getDirs();
    }

    public void getDirs() {
        File dir = new File("./orderSys"); // Must have app running in proper dir
        FileFilter filter = ((File path) -> path.isDirectory());
        File[] folders = dir.listFiles(filter);

        // System.out.println("listFiles gives:" + folders + "count:" + folders.length);
        // ArrayList<String> strFolders = new ArrayList<String>(folders.length);

        for (File folder : folders) {
            String f = folder.getName();
            //strFolders.add(folder.getName());
            String desc = getDesc("ordersys/" + f);
            Category cat = new Category();
            cat.desc = desc;
            cat.name = folder.getName();
            allCategories.add(cat);
            // System.out.println("adding:" + folder.getName());
        }
        // System.out.println("Done with org.rit.swen440.presentation.Categories");
    }

    public ArrayList<Category> ListCategories() {
        return allCategories;
    }

    public String getDesc(String dir) {
        File file = new File(dir);//Must have app running is proper dir
        FilenameFilter fnf = ((File cats, String name) -> name.toLowerCase().endsWith(".cat"));
        File[] fcats = file.listFiles(fnf);
        String desc = "";
        for (File f : fcats) {
            try {
                String path = f.getAbsolutePath();
                String str = "";
                BufferedReader in = new BufferedReader(new FileReader(path));
                while ((str = in.readLine()) != null) {
                    // System.out.println(str);
                    desc += "\n" + str;
                }
                in.close();
            } catch (IOException e) {
            }
        }
        return desc;
    }
}
