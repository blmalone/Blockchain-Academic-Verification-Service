package com.unilog.blockchain.utils;

import com.unilog.blockchain.service.ContractService;
import com.unilog.blockchain.service.ContractServiceImpl;
import org.ethereum.solidity.compiler.CompilationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static org.ethereum.solidity.compiler.CompilationResult.ContractMetadata;
import static org.ethereum.solidity.compiler.CompilationResult.parse;
import static org.ethereum.solidity.compiler.SolidityCompiler.Options;
import static org.ethereum.solidity.compiler.SolidityCompiler.Result;
import static org.ethereum.solidity.compiler.SolidityCompiler.compile;

@Service
public class SolidityCompilerService implements CompilerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Compiler.class);
    private ContractService contractService = new ContractServiceImpl();
    private final String registryContract = "UnilogRegistry.sol";
    private final String userContract = "Unilog.sol";

    public ContractMetadata compileUserContract() throws IOException {
        return compileContract(userContract);
    }

    public ContractMetadata compileRegistryContract() throws IOException {
        return compileContract(registryContract);
    }

    private ContractMetadata compileContract(final String contractFileName) throws IOException {
        LOGGER.info("Compiling Solidity contract for use in a transaction.");
        final String contractContent = contractService.getContractContent(contractFileName);

        Result result = compileContract(contractContent, Options.ABI,
                Options.BIN);

        if (result.isFailed()) {
            throw new RuntimeException("Contract compilation failed:\n" + result.errors);
        }
        return getContractMetadata(result);
    }

    private ContractMetadata getContractMetadata(final Result result)
            throws IOException {
        CompilationResult compilationResult = parse(result.output);
        if (compilationResult.contracts.isEmpty()) {
            throw new RuntimeException("Compilation failed, no contracts returned:\n" + result.errors);
        }
        ContractMetadata metadata = compilationResult.contracts.values()
                .iterator().next();
        if (metadata.bin == null || metadata.bin.isEmpty()) {
            throw new RuntimeException("Compilation failed, no binary returned:\n" + result.errors);
        }
        return metadata;
    }

    private Result compileContract(final String contractContent, final Options abi,
                                   final Options bin) {
        Result result = null;
        try {
            result = compile(contractContent.getBytes(), true,
                    abi, bin);
        }
        catch (IOException e) {
            LOGGER.error("Failure to read contract :\n" + e);
        }
        return result;
    }

}
