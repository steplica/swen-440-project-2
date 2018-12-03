package org.rit.swen440.dataLayer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Database {

    List<Category> categories;
    List<Transaction> transactions;
}
