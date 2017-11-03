package com.unilog.app.service;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Referencing https://github.com/ipfs/java-ipfs-api
 * IPFS is a distributed file system that is commonly used alongside Ethereum.
 */
@Configuration
public class IPFSDistributedFileSystem implements DistributedFileSystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(IPFSDistributedFileSystem.class);

    @Autowired
    private IPFS ipfs;

    @Override
    public String publishFileToNetwork(final String transcript) {
        NamedStreamable.ByteArrayWrapper fileToSubmit = new NamedStreamable.ByteArrayWrapper("transcript.txt",
                transcript.getBytes());
        String contentAddress = "";
        try {
            MerkleNode addResult = ipfs.add(fileToSubmit);
            LOGGER.info("Successfully published to IPFS network, content hash generated: {}",
                    addResult.hash.toBase58());
            contentAddress = addResult.hash.toBase58();
        } catch (IOException e) {
            LOGGER.error("An error occurred publishing a file to the IPFS network");
        }
        return contentAddress;
    }

    @Override
    public byte[] retrieveFileFromNetwork(final String contentAddress) throws IOException {
        Multihash filePointer = Multihash.fromBase58(contentAddress);
        return ipfs.cat(filePointer);
    }

    @Bean
    public IPFS ipfs() {
        IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");
        return ipfs;
    }
}
