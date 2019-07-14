package helpers;

import beans.Transaction;

import java.util.ArrayList;
import java.util.List;

public class MerkleTreeHashHelper {
    public static String getMerkleRootHash(List<Transaction> transactions) {

        List<String> currentLevelHashedTransactions = new ArrayList<>();
        List<String> newLevelHashedTransactions = new ArrayList<>();

        for (Transaction oneTransaction : transactions) {
            currentLevelHashedTransactions.add(HashHelper.generateHash(HashHelper.generateHash(oneTransaction.toString())));
        }


        while (currentLevelHashedTransactions.size() != 1) {
            if (currentLevelHashedTransactions.size() % 2 != 0) {
                currentLevelHashedTransactions.add(currentLevelHashedTransactions.get(currentLevelHashedTransactions.size() - 1));
            }
            for (int i = 0; i < currentLevelHashedTransactions.size(); i += 2) {
                newLevelHashedTransactions.add(HashHelper.generateHash(HashHelper.generateHash(currentLevelHashedTransactions.get(i)
                        + currentLevelHashedTransactions.get(i + 1))));
            }

            currentLevelHashedTransactions = new ArrayList<>(newLevelHashedTransactions);
            newLevelHashedTransactions = new ArrayList<>();
        }

        return currentLevelHashedTransactions.get(0);

    }
}
