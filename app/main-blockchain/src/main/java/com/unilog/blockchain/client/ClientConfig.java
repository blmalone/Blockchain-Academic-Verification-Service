package com.unilog.blockchain.client;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Configuration
public class ClientConfig {

    @Bean
    Client client() throws Exception {
        ThreadFactory namedThreadFactory = getThreadFactory("Client");
        Client client = new Client();
        Executors.newSingleThreadExecutor(namedThreadFactory).
                submit(client::start);
        return client;
    }

    /**
     * Creating an Ethereum node solely tasked with mining transactions into blocks.
     */
    @Bean
    ClientMiner clientMiner() throws Exception {
        ThreadFactory namedThreadFactory = getThreadFactory("ClientMiner");
        ClientMiner clientMiner = new ClientMiner();
        Executors.newSingleThreadExecutor(namedThreadFactory).
                submit(clientMiner::start);
        return clientMiner;
    }

//    @Bean
//    NetworkListener networkListener() throws Exception {
//        ThreadFactory namedThreadFactory = getThreadFactory("NetworkListener");
//        NetworkListener networkListener = new NetworkListener();
//        Executors.newSingleThreadExecutor(namedThreadFactory).
//                submit(networkListener::start);
//        return networkListener;
//    }

    private ThreadFactory getThreadFactory(final String threadName) {
        return new ThreadFactoryBuilder()
                .setNameFormat(threadName).build();
    }
}
