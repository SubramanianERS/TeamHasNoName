package delegate;

import beans.Block;
import beans.Transaction;
import helpers.BlockChainCreationHelper;
import helpers.ChainValidationHelper;
import helpers.DetectAttackHelper;

import java.text.ParseException;
import java.util.List;

public class BlockChainDelegate {

    public static List<Block> createBlockChain(List<List<Transaction>> transactions) throws ParseException {

        List<List<Block>> blockChains = BlockChainCreationHelper.createBlockChains(transactions);

        List<Block> longestAuthenticBlockChain = DetectAttackHelper.detectAttack(blockChains);

        ChainValidationHelper.isChainValid(longestAuthenticBlockChain);

        return longestAuthenticBlockChain;

    }
}
