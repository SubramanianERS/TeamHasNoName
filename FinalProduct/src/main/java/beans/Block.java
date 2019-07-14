package beans;

import helpers.MiningHelper;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Block {
    private String hash;
    private String previousHash;
    private long timeStamp;
    private int nonce;
    private List<Transaction> transactions;
    private String merkleTreeRoot;

    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = MiningHelper.calculateHash(this);
    }
}
