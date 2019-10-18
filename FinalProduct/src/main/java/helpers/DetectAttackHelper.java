package helpers;

import beans.Block;
import beans.Transaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Helper class to detect 51 per cent attack.
 */
public class DetectAttackHelper {

    /**
     * Detect cyclic transactions.
     *
     * @return the response containing the result of the detection.
     * @throws ParseException when exception occurs
     */
    public static HashMap<String, List<Block>> detectAttack(List<List<Block>> blockChains) throws ParseException {

        System.out.println("BlockChains are"+blockChains);
        //Format the transaction data to detect cycles
        List<List<Transaction>> transactionChains = getTransactionsFromBlockChain(blockChains);

        List<Map<String, List<Map<String, Object>>>> nodeChains = formatTransactionData(transactionChains);

        int longestAuthenticBlockchainIndex = -1;
        List<String> nodesVisited;
        String currentNode;
        List<List<String>> cycles;
        int index = 0;
        for (Map<String, List<Map<String, Object>>> nodes : nodeChains) {
            nodesVisited = new ArrayList<>();
            cycles = new ArrayList<>();
            for (String key : nodes.keySet()) {
                currentNode = key;
                nodesVisited.add(currentNode);
                getAllCycles(cycles, nodesVisited, currentNode, nodes);
            }

            List<List<String>> fraudsters = detect51PerCentAttack(cycles, nodes, transactionChains.get(index).size());
            System.out.println("Fraudsters are " + fraudsters);
            if (cycles.size() == 0) {
                if (longestAuthenticBlockchainIndex == -1 || transactionChains.get(index).size() > transactionChains.get(longestAuthenticBlockchainIndex).size()) {
                    longestAuthenticBlockchainIndex = index;
                }
            }
            index++;
        }

        HashMap<String, List<Block>> validatedBlockchains = new HashMap<>();
        validatedBlockchains.put("valid", blockChains.get(longestAuthenticBlockchainIndex));
        validatedBlockchains.put("invalid", blockChains.get(longestAuthenticBlockchainIndex == 0 ? 1 : 0));
        return validatedBlockchains;
    }

    /**
     * Format the transaction data
     *
     * @return the formatted transaction data
     * @throws ParseException when exception occurs
     */
    public static List<Map<String, List<Map<String, Object>>>> formatTransactionData(List<List<Transaction>> transactionChains) throws ParseException {

        List<Map<String, List<Map<String, Object>>>> nodeChains = new ArrayList<>();
        Map<String, List<Map<String, Object>>> nodes;
        List<Map<String, Object>> adjacentNodes;
        Map<String, Object> node;
        Map<String, Object> transaction;
        List<Map<String, Object>> transactions;
        boolean nodeFound;

        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");

        for (List<Transaction> oneTransactionChain : transactionChains) {

            nodes = new HashMap<>();
            for (Transaction oneTransaction : oneTransactionChain) {
                nodeFound = false;
                transaction = new HashMap<>();
                transaction.put("time", format.parse(oneTransaction.getTime()));
                transaction.put("transactionAmount", oneTransaction.getTransactionAmount());
                adjacentNodes = nodes.get(oneTransaction.getSource());
                transactions = new ArrayList<>();
                if (adjacentNodes == null) {
                    adjacentNodes = new ArrayList<>();

                } else {
                    for (Map<String, Object> oneAdjacentNode : adjacentNodes) {
                        if (oneAdjacentNode.get("node").equals(oneTransaction.getDestination())) {
                            nodeFound = true;
                            transactions = (ArrayList) oneAdjacentNode.get("transactions");
                            if (transactions == null) {
                                transactions = new ArrayList<>();
                            }
                            transactions.add(transaction);
                            oneAdjacentNode.put("transactions", transactions);
                        }
                    }
                }
                if (!nodeFound) {
                    node = new HashMap<>();
                    transactions.add(transaction);
                    node.put("transactions", transactions);
                    node.put("visited", false);
                    node.put("node", oneTransaction.getDestination());
                    adjacentNodes.add(node);
                    nodes.put(oneTransaction.getSource(), adjacentNodes);
                }


            }
            nodeChains.add(nodes);
        }
        return nodeChains;

    }

    public static List<List<Transaction>> getTransactionsFromBlockChain(List<List<Block>> blockChains) {
        List<List<Transaction>> transactionChains = new ArrayList<>();
        List<Transaction> oneTransactionChain;
        for (List<Block> oneBlockChain : blockChains) {
            oneTransactionChain = new ArrayList<>();
            for (Block oneBlock : oneBlockChain) {
                oneTransactionChain.addAll(oneBlock.getTransactions());
            }
            transactionChains.add(oneTransactionChain);
        }
        return transactionChains;
    }

    /**
     * Get all cycles in the blockchain
     *
     * @param cycles       the list containing all cycles
     * @param nodesVisited the nodes visited
     * @param currentNode  the current node being visited
     * @param nodes        the blockchain transactionn data
     */
    public static void getAllCycles(List<List<String>> cycles, List<String> nodesVisited, String currentNode, Map<String, List<Map<String, Object>>> nodes) {
        String nodeValue;

        if (nodes.get(currentNode) != null) {

            for (Map<String, Object> oneAdjacentNode : nodes.get(currentNode)) {
                if (!(Boolean) oneAdjacentNode.get("visited")) {
                    oneAdjacentNode.put("visited", true);
                    nodeValue = oneAdjacentNode.get("node").toString();
                    if (nodesVisited.get(0).equals(nodeValue)) {
                        List<String> nodesVisitedCopy = new ArrayList<>(nodesVisited);
                        nodesVisitedCopy.add(nodeValue);
                        cycles.add(nodesVisitedCopy);
                    } else if (!nodesVisited.contains(nodeValue)) {
                        currentNode = nodeValue;
                        nodesVisited.add(nodeValue);
                        getAllCycles(cycles, nodesVisited, currentNode, nodes);
                    }
                }
            }
            currentNode = nodesVisited.get(nodesVisited.size() - 1);
            if (nodes.get(currentNode) != null) {
                for (Map<String, Object> oneAdjacentNode : nodes.get(currentNode)) {
                    oneAdjacentNode.put("visited", false);
                }
            }
            nodesVisited.remove(nodesVisited.size() - 1);
        } else {
            if(nodesVisited.size()>1) {
                currentNode = nodesVisited.get(nodesVisited.size() - 2);
                nodesVisited.remove(nodesVisited.size() - 1);
            } else {
                nodesVisited = new ArrayList<>();
            }
        }
    }

    /**
     * Detect 51 per cent attack with the cycles formed
     *
     * @param cycles           the list containing all cycles
     * @param nodes            the blockchain transaction data
     * @param noOfTransactions the number of transactions
     * @return the cycles if present proving a 51 per cent attack is underway
     */
    public static List<List<String>> detect51PerCentAttack(List<List<String>> cycles, Map<String, List<Map<String, Object>>> nodes, int noOfTransactions) {

        Map<Date, Integer> relatedNodes;
        List<Map<String, Object>> adjacentNodes;
        List<Map<String, Object>> transactions;
        int i;
        List<List<String>> fraudsters = new ArrayList<>();
        String from, to;
        Date date;
        long timeDifference;

        for (List<String> oneCycle : cycles) {
            relatedNodes = new HashMap<>();
            i = 0;
            while (i < oneCycle.size() - 1) {
                from = oneCycle.get(i);
                to = oneCycle.get(i + 1);
                adjacentNodes = nodes.get(from);
                for (Map<String, Object> oneAdjacentNode : adjacentNodes) {
                    if (oneAdjacentNode.get("node").equals(to)) {
                        transactions = (ArrayList) oneAdjacentNode.get("transactions");
                        for (Map<String, Object> oneTransaction : transactions) {
                            date = (Date) oneTransaction.get("time");
                            for (Date key : relatedNodes.keySet()) {
                                timeDifference = Math.abs(key.getTime() - date.getTime());
                                //If the time difference between transactions in a cycle is less than 10 seconds, it states that a 51 per cent attack is underway
                                if (timeDifference < 10000) {
                                    relatedNodes.put(key, relatedNodes.get(key) + 1);
                                }
                            }
                            if (!relatedNodes.containsKey(date)) {
                                relatedNodes.put(date, 1);
                            }
                        }
                    }
                }
                i++;
            }
            for (Date key : relatedNodes.keySet()) {
                //The number of cyclic transactions should constitute more than half of the total number of transactions. This is to avoid false positives
                if (relatedNodes.get(key) > noOfTransactions / 2) {
                    fraudsters.add(oneCycle);
                }
            }
        }

        return fraudsters;
    }

}

