package com.unilog.blockchain.utils;

import org.ethereum.solidity.compiler.CompilationResult;

import java.io.IOException;

public interface CompilerService {

    CompilationResult.ContractMetadata compileUserContract() throws IOException;

    CompilationResult.ContractMetadata compileRegistryContract() throws IOException;

}
