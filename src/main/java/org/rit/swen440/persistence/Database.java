package org.rit.swen440.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rit.swen440.model.Category;
import org.rit.swen440.model.InsufficientQuantityException;
import org.rit.swen440.model.Product;
import org.rit.swen440.model.Transaction;
import org.rit.swen440.util.FileUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Database {

    @JsonIgnore
    private String databaseFilepath;

    private Map<String, Category> categories;
    private List<Transaction> transactions;

    public static Database initialize(String databaseFilepath) {
        Database database = FileUtils.readJsonAsObject(databaseFilepath, Database.class);
        database.setDatabaseFilepath(databaseFilepath);

        database.categories.replaceAll((categoryName, category) -> {
            category.setName(categoryName);
            category.getProducts()
                    .replaceAll((skuCode, product) -> {
                        product.setSkuCode(Integer.parseInt(skuCode));
                        return product;
                    });
            return category;
        });

        Runtime.getRuntime().addShutdownHook(new Thread(database::updateDatabaseFile));

        return database;
    }

    public void addTransaction(Transaction transaction) {
        updateInventory(transaction);
        transactions.add(transaction);
    }

    private void updateInventory(Transaction transaction) {
        Product product = categories.get(transaction.getItemCategory())
                .getProducts()
                .get(String.valueOf(transaction.getItemSkuCode()));

        if (transaction.getQuantity() > product.getItemCount()) {
            throw new InsufficientQuantityException();
        }

        product.setItemCount(product.getItemCount() - transaction.getQuantity());

        if (product.getItemCount() < product.getThreshold()) {
            product.setItemCount(product.getItemCount() + product.getReorderAmount());
        }
    }

    private void updateDatabaseFile() {
        FileUtils.writeObjectToJsonFile(databaseFilepath, this);
    }
}
