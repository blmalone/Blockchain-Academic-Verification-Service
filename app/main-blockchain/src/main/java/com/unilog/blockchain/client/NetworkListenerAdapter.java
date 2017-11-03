package com.unilog.blockchain.client;

import org.ethereum.core.Block;
import org.ethereum.core.TransactionReceipt;
import org.ethereum.listener.EthereumListenerAdapter;
import org.ethereum.net.eth.message.StatusMessage;
import org.ethereum.net.rlpx.Node;
import org.ethereum.net.server.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NetworkListenerAdapter extends EthereumListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkListenerAdapter.class);

    private NetworkListener networkListener;

    public NetworkListenerAdapter(final NetworkListener networkListener) {
        this.networkListener = networkListener;
    }

    @Override
    public void onNodeDiscovered(final Node node) {
        networkListener.getNodesDiscovered().add(node);
        LOGGER.info("node discovered: {}", node);
    }

    @Override
    public void onEthStatusUpdated(final Channel channel, final StatusMessage statusMessage) {
        networkListener.getEthNodes().put(channel.getNode(), statusMessage);
    }

    @Override
    public void onPeerAddedToSyncPool(final Channel peer) {
        networkListener.getSyncPeers().add(peer);
        LOGGER.info("peer {} has been added to transaction pool", peer);

    }

    @Override
    public void onBlock(final Block block, final List<TransactionReceipt> receipts) {
        networkListener.onBlock(block, receipts);
    }
}
