package com.unilog.blockchain.service;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class FileReaderServiceImpl implements FileReaderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileReaderServiceImpl.class);

    @Override
    public String readFile(final String fileName) {
        try {
            return StringUtils.normalizeSpace(
                    IOUtils.toString(
                            this.getClass().getClassLoader().getResourceAsStream(fileName)));
        }
        catch (IOException e) {
            LOGGER.error("File could not be loaded from resource: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public String readConfigFile(final String fileName) throws IOException {
        return IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(fileName));
    }
}
