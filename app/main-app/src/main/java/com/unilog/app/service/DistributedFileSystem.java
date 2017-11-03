package com.unilog.app.service;

import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface DistributedFileSystem {

    String publishFileToNetwork(String transcript);

    byte[] retrieveFileFromNetwork(String contentAddress) throws IOException;

}
