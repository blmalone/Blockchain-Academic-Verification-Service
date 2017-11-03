package com.unilog.blockchain.client;

import org.ethereum.config.SystemProperties;
import org.ethereum.core.Block;
import org.ethereum.facade.Ethereum;
import org.ethereum.facade.EthereumFactory;
import org.ethereum.net.eth.message.StatusMessage;
import org.ethereum.net.rlpx.Node;
import org.ethereum.net.server.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

@Service
public class NodeConnector {

    private static final int PEER_DISCOVERY_THRESHOLD = 30;
    private static final int MINIMUM_THREAD_SLEEP = 300;
    private static final int MAXIMUM_THREAD_SLEEP = 5000;
    private static final int PEER_DISCOVERY_LIMIT = 60;
    private static final int BLOCK_DISCOVERY_THRESHOLD = 300;
    private static final int MINIMUM_BLOCK_SLEEP = 1000;
    private static final int MAXIMUM_BLOCK_SLEEP = 60000;
    private static final int BLOCK_DISCOVERY_LIMIT = 330;
    private static final int SLEEP_PERIOD = 10000;
    private static final int HOME_NODE = 1;

    private Ethereum ethereum;

    private SystemProperties systemProperties = new SystemProperties();

    private static final Logger LOGGER = LoggerFactory.getLogger(Node.class);

    private List<Node> nodesDiscovered = new Vector<>();

    private Map<Node, StatusMessage> ethNodes = new Hashtable<>();

    private List<Channel> syncPeers = new Vector<>();

    private Block bestBlock = null;

    private boolean synced = false;

    private boolean syncComplete = false;

    public void start() {
        this.ethereum = EthereumFactory.createEthereum();
    }

    public void establishConnection() {
        try {

            if (systemProperties.peerDiscovery()) {
                LOGGER.info("WaitForDiscovery() method has been called.");
                waitForDiscovery();
            }
            else {
                LOGGER.info("Peer discovery disabled. We should actively connect to "
                        + "another peers or wait for incoming connections");
            }

            waitForAvailablePeers();

            waitForSyncPeers();

            waitForFirstBlock();

            waitForSync();

            onSyncDone();

        }
        catch (Exception e) {
            LOGGER.error("An Exception occurred : {}", e.getMessage());
        }
    }

    /**
     * Waits until any new nodes are discovered by the UDP discovery protocol.
     */
    protected void waitForDiscovery() throws Exception {
        LOGGER.info("Waiting for nodes discovery...");

        int bootNodes = systemProperties.peerDiscoveryIPList().size() + HOME_NODE;
        int count = 0;
        while (true) {
            Thread.sleep(count < PEER_DISCOVERY_THRESHOLD ? MINIMUM_THREAD_SLEEP : MAXIMUM_THREAD_SLEEP);
            if (nodesDiscovered.size() < bootNodes) {
                LOGGER.info("Discovery works, new nodes started being discovered.");
                return;
            }

            if (count >= PEER_DISCOVERY_THRESHOLD) {
                LOGGER.warn("Waiting for more nodes to be discovered");
            }
            if (count > PEER_DISCOVERY_LIMIT) {
                LOGGER.error("Looks like discovery failed, no nodes were found.\n"
                        + "Please check your Firewall/NAT UDP protocol settings.\n"
                        + "Your IP interface was detected as {}, please check "
                        + "if this interface is correct, otherwise set it manually"
                        + " via 'peer.discovery.bind.ip' option.", systemProperties.bindIp());
                throw new RuntimeException("Discovery failed.");
            }
            count++;
        }
    }

    /**
     * Discovering nodes is only the first step. No we need to find among discovered nodes
     * those ones which are live, accepting inbound connections, and has compatible subprotocol versions.
     */
    protected void waitForAvailablePeers() throws Exception {
        LOGGER.info("Waiting for available peers...");
        int count = 0;
        while (true) {
            Thread.sleep(count < PEER_DISCOVERY_THRESHOLD ? MINIMUM_THREAD_SLEEP : MAXIMUM_THREAD_SLEEP);
            LOGGER.info("Number of ethNodes(needs to be > 0) {}", ethNodes.size());
            if (ethNodes.size() > 0) {
                LOGGER.info("Available Eth nodes found: {}", ethNodes.size());
                return;
            }

            if (count >= PEER_DISCOVERY_THRESHOLD) {
                LOGGER.info("No Eth nodes found so far. Continuing to search...");
            }
            if (count > PEER_DISCOVERY_LIMIT) {
                LOGGER.error("No eth capable nodes found. Please check the logs.");
                throw new RuntimeException("Eth nodes failed.");
            }
            count++;
        }
    }

    /**
     * When live nodes found SyncManager should select from them the most
     * suitable and add them as peers for syncing the blocks.
     */
    protected void waitForSyncPeers() throws Exception {
        LOGGER.info("Transactor: waitForSyncPeers()");
        int count = 0;
        while (true) {
            LOGGER.info("Number of syncPeers (needs to be > 0) {}", syncPeers.size());
            Thread.sleep(count < PEER_DISCOVERY_THRESHOLD ? MINIMUM_THREAD_SLEEP : MAXIMUM_THREAD_SLEEP);

            if (syncPeers.size() > 0) {
                LOGGER.info("At least one sync peer found.");
                return;
            }

            if (count >= PEER_DISCOVERY_THRESHOLD) {
                LOGGER.info("No sync peers found so far. Continuing to search...");
            }
            if (count > PEER_DISCOVERY_LIMIT) {
                LOGGER.error("No sync peers found. Please check the logs.");
                throw new RuntimeException("Sync peers failed.");
            }
            count++;
        }
    }

    /**
     * Waits until blocks import started.
     */
    protected void waitForFirstBlock() throws Exception {
        Block currentBest = ethereum.getBlockchain().getBestBlock();
        LOGGER.info("Current BEST block: {}", currentBest.getShortDescr());
        LOGGER.info("Waiting for blocks start importing (may take a while)...");
        int count = 0;
        while (true) {
            Thread.sleep(count < BLOCK_DISCOVERY_THRESHOLD ? MINIMUM_BLOCK_SLEEP : MAXIMUM_BLOCK_SLEEP);

            if (bestBlock != null && bestBlock.getNumber() > currentBest.getNumber()) {
                LOGGER.info("[v] Blocks import started.");
                return;
            }

            if (count >= BLOCK_DISCOVERY_THRESHOLD) {
                LOGGER.info("Still no blocks. Be patient...");
            }
            if (count > BLOCK_DISCOVERY_LIMIT) {
                LOGGER.error("No blocks have been imported in a long time. "
                        + "Please check logs for any issues.");
                throw new RuntimeException("Block import failed.");
            }
            count++;
        }
    }

    /**
     * Waits until the whole blockchain sync is complete.
     */
    private void waitForSync() throws Exception {
        LOGGER.info("Waiting for the whole blockchain sync (will take up to several hours for the whole chain)...");
        while (true) {
            Thread.sleep(SLEEP_PERIOD);

            if (synced) {
                LOGGER.info("Sync complete! The best block: {}", bestBlock.getShortDescr());
                syncComplete = true;
                return;
            }
            LOGGER.info("Blockchain sync in progress. Last imported block: {}" , bestBlock.getShortDescr());
        }
    }

    protected void onSyncDone() throws Exception {
        LOGGER.info("Transactor: onSyncDone method has been called.");
    }

    protected Ethereum getEthereum() {
        return ethereum;
    }

    protected void setEthereum(final Ethereum ethereum) {
        this.ethereum = ethereum;
    }

    protected SystemProperties getSystemProperties() {
        return systemProperties;
    }

    protected void setSystemProperties(final SystemProperties systemProperties) {
        this.systemProperties = systemProperties;
    }

    protected static Logger getLOGGER() {
        return LOGGER;
    }

    protected List<Node> getNodesDiscovered() {
        return nodesDiscovered;
    }

    protected void setNodesDiscovered(final List<Node> nodesDiscovered) {
        this.nodesDiscovered = nodesDiscovered;
    }

    protected Map<Node, StatusMessage> getEthNodes() {
        return ethNodes;
    }

    protected void setEthNodes(final Map<Node, StatusMessage> ethNodes) {
        this.ethNodes = ethNodes;
    }

    protected List<Channel> getSyncPeers() {
        return syncPeers;
    }

    protected void setSyncPeers(final List<Channel> syncPeers) {
        this.syncPeers = syncPeers;
    }

    protected Block getBestBlock() {
        return bestBlock;
    }

    protected void setBestBlock(final Block bestBlock) {
        this.bestBlock = bestBlock;
    }

    public boolean isSynced() {
        return synced;
    }

    protected void setSynced(final boolean synced) {
        this.synced = synced;
    }

    protected boolean isSyncComplete() {
        return syncComplete;
    }

    protected void setSyncComplete(final boolean syncComplete) {
        this.syncComplete = syncComplete;
    }

}

