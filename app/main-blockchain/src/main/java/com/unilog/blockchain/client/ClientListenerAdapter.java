package com.unilog.blockchain.client;

import org.ethereum.core.Block;
import org.ethereum.core.TransactionReceipt;
import org.ethereum.db.ByteArrayWrapper;
import org.ethereum.listener.EthereumListenerAdapter;
import org.ethereum.net.eth.message.StatusMessage;
import org.ethereum.net.rlpx.Node;
import org.ethereum.net.server.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bouncycastle.util.encoders.Hex.decode;

public class ClientListenerAdapter extends EthereumListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientListenerAdapter.class);

    private Client client;

    private Map<ByteArrayWrapper, TransactionReceipt> receivedTransactions
            = Collections.synchronizedMap(new HashMap<>());

    public ClientListenerAdapter(final Client client) {
        this.client = client;
    }

    /**
     * Each block received from the network is checked for our pending transactions.
     * If the block contains our transactions then they will be removed from pending transactions.
     *
     * @param block    - Incoming block from the network.
     * @param receipts - transaction receipts from the mined transactions.
     */
    @Override
    public void onBlock(final Block block, final List<TransactionReceipt> receipts) {
        LOGGER.info("Number of transactions in block: {}", receipts.size());
        LOGGER.info("Block number: {}", block.getNumber());
        for (TransactionReceipt receipt : receipts) {
            LOGGER.info("Transactions have been found in block...");
            ByteArrayWrapper txHashW = new ByteArrayWrapper(receipt.getTransaction().getHash());
            receivedTransactions.put(txHashW, receipt);
        }
    }

    @Override
    public void onNodeDiscovered(final Node node) {
        client.getNodesDiscovered().add(node);
        LOGGER.info("node discovered: {}", node);
    }

    @Override
    public void onEthStatusUpdated(final Channel channel, final StatusMessage statusMessage) {
        client.getEthNodes().put(channel.getNode(), statusMessage);
    }

    @Override
    public void onPeerAddedToSyncPool(final Channel peer) {
        client.getSyncPeers().add(peer);
        LOGGER.info("peer {} has been added to transaction pool", peer);

    }

    @Override
    public void onSyncDone(final SyncState state) {
        client.setSynced(true);
        LOGGER.info("On SyncDone method called!");
    }

    public boolean hasTransactionBeenMined(final String transactionHash) {
        LOGGER.info("Checking whether transaction has been mined: {}", transactionHash);
        ByteArrayWrapper key = new ByteArrayWrapper(decode(transactionHash));
        if (receivedTransactions.get(key) != null) {
            LOGGER.info("Transaction has been mined, removing transaction from pending transactions. "
                    + "Tx Hash: {}", transactionHash);
            receivedTransactions.remove(key);
            return true;
        }
        LOGGER.info("Transaction has not yet been mined.");
        return false;
    }
}
