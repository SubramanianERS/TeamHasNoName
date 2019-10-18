package delegate;

import beans.Block;
import beans.Transaction;
import helpers.BlockChainCreationHelper;
import helpers.ChainValidationHelper;
import helpers.DetectAttackHelper;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

public class BlockChainDelegate {

    public static HashMap<String, List<Block>> createBlockChain(List<List<Transaction>> transactions) throws ParseException {

        List<List<Block>> blockChains = BlockChainCreationHelper.createBlockChains(transactions);

        HashMap<String, List<Block>> validatedBlockchains = DetectAttackHelper.detectAttack(blockChains);

//        ChainValidationHelper.isChainValid(validatedBlockchains.get("valid"));

        return validatedBlockchains;

    }
}
