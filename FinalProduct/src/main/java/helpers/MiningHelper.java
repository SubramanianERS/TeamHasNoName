package helpers;

import beans.Block;

public class MiningHelper {

    public static void mineBlock(int difficulty, Block block) {
        String target = new String(new char[difficulty]).replace('\0', '0'); //Create a string with difficulty * "0"
        while (!block.getHash().substring(0, difficulty).equals(target)) {
            block.setNonce(block.getNonce()+1);
            block.setHash(calculateHash(block));
        }
    }

    public static String calculateHash(Block block) {
        String calculatedhash = HashHelper.generateHash(
                block.getPreviousHash() +
                        block.getTimeStamp() +
                        block.getNonce()
        );
        return calculatedhash;
    }
}
