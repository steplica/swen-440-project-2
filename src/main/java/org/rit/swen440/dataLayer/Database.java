package org.rit.swen440.dataLayer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rit.swen440.util.FileUtils;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Database {

    private String databaseFilepath;
    private List<Category> categories;
    private List<Transaction> transactions;

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        updateInventory(transaction);
        updateDatabaseFile();
    }

    private void updateInventory(Transaction transaction) {
        categories = categories.stream()
                .map(category -> {
                    category.setProducts(category.getProducts()
                                                 .stream()
                                                 .map(product -> {
                                                     if (product.getSkuCode() == transaction.getItemSkuCode()) {
                                                         product = placeProductOrder(product, transaction);
                                                     }
                                                     return product;
                                                 })
                                                 .collect(Collectors.toList()));
                    return category;
                })
                .collect(Collectors.toList());
    }

    private Product placeProductOrder(Product product, Transaction transaction) {
        if (transaction.getQuantity() > product.getItemCount()) {
            throw new InsufficientQuantityException();
        }

        product.setItemCount(product.getItemCount() - transaction.getQuantity());

        if (product.getItemCount() < product.getThreshold()) {
            product.setItemCount(product.getItemCount() + product.getReorderAmount());
        }

        return product;
    }

    private void updateDatabaseFile() {
        FileUtils.writeObjectToJsonFile(databaseFilepath, this);
    }
}
