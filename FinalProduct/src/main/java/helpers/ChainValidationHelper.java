package helpers;

import beans.Block;

import java.util.List;

public class ChainValidationHelper {

    public static Boolean isChainValid(List<Block> blocks) {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[5]).replace('\0', '0');

        //loop through blockchain to check hashes:
        for(int i=1; i < blocks.size(); i++) {
            currentBlock = blocks.get(i);
            previousBlock = blocks.get(i-1);
            //compare registered hash and calculated hash:
            if(!currentBlock.getHash().equals(MiningHelper.calculateHash(currentBlock)) ){
                return false;
            }
            //compare previous hash and registered previous hash
            if(!previousBlock.getHash().equals(MiningHelper.calculateHash(previousBlock)) ) {
                return false;
            }
            //check if hash is solved
            if(!currentBlock.getHash().substring(0, 5).equals(hashTarget)) {
                return false;
            }
        }
        return true;
    }
}
