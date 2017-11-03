package com.unilog.blockchain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ContractServiceImpl implements ContractService {

    private FileReaderService fileReaderService = new FileReaderServiceImpl();

    private static final Logger LOGGER = LoggerFactory.getLogger(ContractServiceImpl.class);

    public String getContractContent(final String contractName) {
        LOGGER.info("Attempting to read contract from resources.");
        return fileReaderService.readFile(contractName);
    }

}
