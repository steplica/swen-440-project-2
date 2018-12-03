package org.rit.swen440.dataLayer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rit.swen440.util.FileUtils;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Database {

    private String databaseFilepath;
    private List<Category> categories;
    private List<Transaction> transactions;

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        FileUtils.writeObjectToJsonFile(databaseFilepath, this);
    }
}
