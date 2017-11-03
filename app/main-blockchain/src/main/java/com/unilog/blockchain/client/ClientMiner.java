package com.unilog.blockchain.client;

import com.typesafe.config.ConfigFactory;
import com.unilog.blockchain.service.FileReaderService;
import com.unilog.blockchain.service.FileReaderServiceImpl;
import org.ethereum.config.SystemProperties;
import org.ethereum.core.Block;
import org.ethereum.facade.EthereumFactory;
import org.ethereum.mine.Ethash;
import org.ethereum.mine.MinerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

public class ClientMiner extends NodeConnector implements MinerListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientMiner.class);

    private FileReaderService fileReaderService = new FileReaderServiceImpl();

    private static final String CLIENT_MINER_CONFIG = "clientMiner.conf";

    @Override
    public void start() {
        setEthereum(EthereumFactory.createEthereum(ClientMiner.class));
        if (getSystemProperties().isMineFullDataset()) {
            LOGGER.info("Generating Full Dataset. This can take up to 10 min if not cached.");
            // calling this for indication of the dataset generation
            Ethash ethash = Ethash.getForBlock(getSystemProperties(),
                    getEthereum().getBlockchain().getBestBlock().getNumber());
            ethash.getFullDataset();
            LOGGER.info("Full dataset generated.");
        }
        getEthereum().getBlockMiner().addListener(this);
        getEthereum().getBlockMiner().startMining();
        LOGGER.info("MinerEthereum : Has started mining");
    }

    @Override
    public void miningStarted() {
        LOGGER.info("Miner started");
    }

    @Override
    public void miningStopped() {
        LOGGER.info("Miner stopped");
    }

    @Override
    public void blockMiningStarted(final Block block) {
        LOGGER.info("Start mining block: {}", block.getShortDescr());
    }

    @Override
    public void blockMined(final Block block) {
        LOGGER.info("Block mined by MinerEthereum!");
    }

    @Override
    public void blockMiningCanceled(final Block block) {
        LOGGER.info("Cancel mining block: {}", block.getShortDescr());
    }

    /**
     * Helps us override the default system properties for the ethereum instance.
     */
    @Bean
    public SystemProperties systemProperties() throws IOException {
        SystemProperties props = new SystemProperties();
        props.overrideParams(ConfigFactory.parseString(
                fileReaderService.readConfigFile(CLIENT_MINER_CONFIG).replaceAll("'", "\"")));
        setSystemProperties(props);
        return props;
    }
}

