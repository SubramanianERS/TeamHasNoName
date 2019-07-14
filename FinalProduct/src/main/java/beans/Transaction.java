package beans;

import lombok.Data;

@Data
public class Transaction {
    private String source;
    private String destination;
    private String time;
    private double transactionAmount;
    private String id;
}
