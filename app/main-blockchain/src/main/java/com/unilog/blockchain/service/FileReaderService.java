package com.unilog.blockchain.service;

import java.io.IOException;

public interface FileReaderService {

    /**
     * Reads a generic file from resources in the classpath removing whitespace from beginning and end.
     * @param fileName file to find and read
     * @return String representation of the file
     */
    String readFile(final String fileName);

    /**
     * Reads a custom config file from resources in classpath where whitespace is significant.
     * @param fileName - Config file to find and read
     * @return String representation of the file.
     * @throws IOException
     */
    String readConfigFile(final String fileName) throws IOException;

}
