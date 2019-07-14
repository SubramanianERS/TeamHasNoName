package helpers;

import beans.Block;
import beans.Transaction;

import java.util.ArrayList;
import java.util.List;

public class BlockChainCreationHelper {
    public static List<List<Block>> createBlockChains(List<List<Transaction>> transactions) {
        List<List<Block>> blockChains = new ArrayList<>();
        List<Block> oneBlockChain;
        int transactionNumber;
        List<Transaction> fourTransactions = null;
        for (List<Transaction> oneTransactionChain : transactions) {
            transactionNumber = 0;
            oneBlockChain = new ArrayList<>();
            for (Transaction oneTransaction : oneTransactionChain) {
                if (transactionNumber % 4 == 0) {
                    if (oneBlockChain.size() == 0) {
                        oneBlockChain.add(new Block("0"));
                    } else {
                        oneBlockChain.add(new Block(oneBlockChain.get(oneBlockChain.size() - 1).getHash()));
                    }
                    fourTransactions = new ArrayList<>();
                }
                fourTransactions.add(oneTransaction);
                transactionNumber++;
                if (transactionNumber % 4 == 0) {
                    oneBlockChain.get(oneBlockChain.size() - 1).setTransactions(fourTransactions);
                    oneBlockChain.get(oneBlockChain.size() - 1).setMerkleTreeRoot(MerkleTreeHashHelper.getMerkleRootHash(fourTransactions));
                    MiningHelper.mineBlock(5, oneBlockChain.get(oneBlockChain.size() - 1));
                }
            }
            if (transactionNumber % 4 != 0) {
                oneBlockChain.get(oneBlockChain.size() - 1).setTransactions(fourTransactions);
                oneBlockChain.get(oneBlockChain.size() - 1).setMerkleTreeRoot(MerkleTreeHashHelper.getMerkleRootHash(fourTransactions));
            }
            blockChains.add(oneBlockChain);
        }
        System.out.println(blockChains);
        return blockChains;
    }
}
