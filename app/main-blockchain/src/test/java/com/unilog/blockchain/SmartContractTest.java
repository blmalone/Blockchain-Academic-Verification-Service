package com.unilog.blockchain;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.ethereum.config.SystemProperties;
import org.ethereum.config.blockchain.FrontierConfig;
import org.ethereum.crypto.ECKey;
import org.ethereum.util.blockchain.SolidityCallResult;
import org.ethereum.util.blockchain.SolidityContract;
import org.ethereum.util.blockchain.StandaloneBlockchain;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

@RunWith(MockitoJUnitRunner.class)
public class SmartContractTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartContractTest.class);
    private static final String SMART_CONTRACT_REGISTRY = "UnilogRegistry.sol";
    //Using the test version of the contract, the difference is a change of the time unit from weeks to seconds.
    private static final String SMART_CONTRACT_USER = "UnilogTestVersion.sol";
    private static boolean setUpIsDone = false;
    private static StandaloneBlockchain blockchain = new StandaloneBlockchain().withAutoblock(true);
    private static SolidityContract registryContract;
    private static SolidityContract userContract;
    private static final ECKey hackerIdentity = new ECKey();
    private static final ECKey artifactOwner = new ECKey();
    private static ECKey legitimateOwner;
    private static final long initalAccountBalance = 250000000000000000L;

    @Test
    public void testAddRegistryContractSuccess() {
        setUp(false);
        Assert.assertEquals(BigInteger.valueOf(1), registryContract.callConstFunction("version")[0]);
    }

    @Test
    public void testAddUserContractSuccess() {
        setUp(false);
        Assert.assertEquals(BigInteger.valueOf(1), userContract.callConstFunction("version")[0]);
    }


    @Test
    public void testAddContractToRepositoryVerifySuccess() {
        setUp(false);
        blockchain.createBlock();
        registryContract.callFunction("addContract", userContract.getAddress());
        Assert.assertTrue((Boolean) registryContract.callConstFunction("contains", userContract.getAddress())[0]);
    }

    @Test
    public void testAddContractToRepositoryWithHackerIdentity() {
        setUp(false);
        blockchain.createBlock();
        blockchain.sendEther(hackerIdentity.getAddress(), BigInteger.valueOf(initalAccountBalance));

        byte[] someContractAddress = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        blockchain.setSender(hackerIdentity);
        blockchain.createBlock();
        registryContract.callFunction("addContract", someContractAddress);
        Assert.assertFalse((Boolean) registryContract.callConstFunction("contains", someContractAddress)[0]);
        blockchain.setSender(legitimateOwner);
    }

    @Test
    public void testGetContractThatHasNotBeenRegistered() {
        setUp(false);
        byte[] someContractAddress = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2};
        blockchain.createBlock();
        Assert.assertFalse((Boolean) registryContract.callConstFunction("contains", someContractAddress)[0]);
    }

    @Test
    public void testAddContractThenDisableContractSuccess() {
        setUp(false);
        byte[] registeredContractAddress = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3};
        blockchain.createBlock();
        registryContract.callFunction("addContract", registeredContractAddress);
        blockchain.createBlock();
        registryContract.callFunction("disableContract", registeredContractAddress);
        Assert.assertFalse((Boolean) registryContract.callConstFunction("contains", registeredContractAddress)[0]);
    }

    @Test
    public void testAddContractThenDisableContractFail() {
        setUp(false);
        blockchain.createBlock();
        blockchain.sendEther(hackerIdentity.getAddress(), BigInteger.valueOf(initalAccountBalance));

        byte[] registeredContractAddress = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 4};
        blockchain.createBlock();
        registryContract.callFunction("addContract", registeredContractAddress);
        blockchain.setSender(hackerIdentity);
        blockchain.createBlock();
        registryContract.callFunction("disableContract", registeredContractAddress);
        Assert.assertTrue((Boolean) registryContract.callConstFunction("contains", registeredContractAddress)[0]);
        blockchain.setSender(legitimateOwner);
    }

    //####################### Tests concerning the user contract #######################

    @Test
    public void testAddArtifactToUserContractByUnilog() {
        setUp(true);
        byte[] sampleIPFSContentAddress = "QmW2WQi7j6c7UgJTarActp7tDNikE4B2qXtFCfLPdsgaTQ".getBytes();
        blockchain.createBlock();
        SolidityCallResult result = userContract.callFunction("addArtifact", sampleIPFSContentAddress, 0);
        Assert.assertEquals(BigInteger.valueOf(1), result.getReturnValue());
    }

    @Test
    public void testAddArtifactToUserContractByHacker() {
        setUp(false);
        blockchain.createBlock();
        blockchain.sendEther(hackerIdentity.getAddress(), BigInteger.valueOf(initalAccountBalance));

        byte[] sampleIPFSContentAddress = "QmW2WQi7j6c7UgJTarActp7tDNikE4B2qXtFCfLPdsgaTQ".getBytes();
        blockchain.setSender(hackerIdentity);
        blockchain.createBlock();
        SolidityCallResult result = userContract.callFunction("addArtifact", sampleIPFSContentAddress, 0);
        Assert.assertFalse(result.isSuccessful());
        Assert.assertEquals(BigInteger.valueOf(0), result.getReturnValue());
        blockchain.setSender(legitimateOwner);
    }

    @Test
    public void testAddArtifactWithExpirationDate() {
        setUp(true);
        byte[] sampleIPFSContentAddress = "HmW2WQi7j6c7UgJTarActp7tDNikE4B2qXtFCfLPdsgaTK".getBytes();
        blockchain.createBlock();
        SolidityCallResult result = userContract.callFunction("addArtifact", sampleIPFSContentAddress, 1000);
        Assert.assertEquals(BigInteger.valueOf(1), result.getReturnValue());
    }

    @Test
    public void testAddArtifactWithExpirationDateThenVerifyBeforeItExpires() throws UnsupportedEncodingException {
        setUp(true);
        byte[] sampleIPFSContentAddress = "XcF2WQi2j7c7UgJTarActp7tDNikE4B2qXtFCfLPdsbcTK".getBytes();
        blockchain.createBlock();
        SolidityCallResult result = userContract.callFunction("addArtifact", sampleIPFSContentAddress, 1000);
        blockchain.createBlock();
        BigInteger artifactId = ((BigInteger) result.getReturnValue()).subtract(BigInteger.valueOf(1));
        SolidityCallResult result2 = userContract.callFunction("verifyArtifact", artifactId);
        String actual = new String((byte[]) result2.getReturnValue(), "UTF-8");
        Assert.assertEquals("XcF2WQi2j7c7UgJTarActp7tDNikE4B2qXtFCfLPdsbcTK", actual);
    }

    @Test
    public void testAddArtifactWithExpirationDateThenVerifyAfterItExpires() throws UnsupportedEncodingException {
        setUp(true);
        byte[] sampleIPFSContentAddress = "XcF2WQi2j7c7UgJTarActp7tDNikE4B2qXtFCfLPdsbcTK".getBytes();
        blockchain.createBlock();
        SolidityCallResult result = userContract.callFunction("addArtifact", sampleIPFSContentAddress, 1);
        blockchain.createBlock();
        BigInteger artifactId = ((BigInteger) result.getReturnValue()).subtract(BigInteger.valueOf(1));
        SolidityCallResult result2 = userContract.callFunction("verifyArtifact", artifactId);
        String actual = new String((byte[]) result2.getReturnValue(), "UTF-8");
        Assert.assertEquals("Expired!", actual);
    }

    @Test
    public void testAddArtifactWithExpirationDateThenVerifyAfterItExpiresTwice() throws UnsupportedEncodingException {
        setUp(true);
        byte[] sampleIPFSContentAddress = "XcF2WQi2j7c7UgJTarActp7tDNikE4B2qXtFCfLPdsbcTK".getBytes();
        blockchain.createBlock();
        SolidityCallResult result = userContract.callFunction("addArtifact", sampleIPFSContentAddress, 1);
        blockchain.createBlock();
        BigInteger artifactId = ((BigInteger) result.getReturnValue()).subtract(BigInteger.valueOf(1));
        userContract.callFunction("verifyArtifact", artifactId);
        blockchain.createBlock();
        SolidityCallResult result3 = userContract.callFunction("verifyArtifact", artifactId);
        String actual = new String((byte[]) result3.getReturnValue(), "UTF-8");
        Assert.assertEquals("Expired!", actual);
    }

    @Test
    public void testVerifyBogusArtifactId() throws UnsupportedEncodingException {
        setUp(true);
        blockchain.createBlock();
        SolidityCallResult result = userContract.callFunction("verifyArtifact", 7);
        String actual = new String((byte[]) result.getReturnValue(), "UTF-8");
        Assert.assertEquals("Fictional Artifact!", actual);
    }

    @Test
    public void testAssignArtifactOwnerFromUnilogAccountPass() {
        setUp(true);
        blockchain.createBlock();
        SolidityCallResult result = userContract.callFunction("assignArtifactOwner", artifactOwner.getAddress());
        Assert.assertTrue((Boolean) result.getReturnValue());
    }

    @Test
    public void testAssignArtifactOwnerFromUnilogAccountWhereArtifactOwnerIsUnilog() {
        setUp(true);
        blockchain.createBlock();
        SolidityCallResult result = userContract.callFunction("assignArtifactOwner", legitimateOwner.getAddress());
        Assert.assertFalse((Boolean) result.getReturnValue());
    }

    @Test
    public void testAssignArtifactOwnerFromHackerAccount() {
        setUp(true);
        blockchain.createBlock();
        blockchain.sendEther(hackerIdentity.getAddress(), BigInteger.valueOf(initalAccountBalance));

        blockchain.setSender(hackerIdentity);
        blockchain.createBlock();
        final SolidityCallResult result = userContract.callFunction("assignArtifactOwner", hackerIdentity.getAddress());
        Assert.assertFalse((Boolean) result.getReturnValue());
        Assert.assertFalse(result.isSuccessful());
        blockchain.setSender(legitimateOwner);
    }

    @Test
    public void testDeleteArtifactThatExistsFromArtifactOwnersAccount() {
        setUp(true);
        blockchain.createBlock();
        blockchain.sendEther(artifactOwner.getAddress(), BigInteger.valueOf(initalAccountBalance));

        blockchain.createBlock();
        userContract.callFunction("assignArtifactOwner", artifactOwner.getAddress());

        byte[] sampleIPFSContentAddress = "XcF2WQi2j7c7UgJTarActp7tDNikE4B2qXtFCfLPdsbcTK".getBytes();
        blockchain.createBlock();
        SolidityCallResult result = userContract.callFunction("addArtifact", sampleIPFSContentAddress, 1000);
        BigInteger artifactId = ((BigInteger) result.getReturnValue()).subtract(BigInteger.valueOf(1));

        blockchain.setSender(artifactOwner);
        blockchain.createBlock();
        SolidityCallResult deleteResult = userContract.callFunction("deleteArtifact", artifactId);
        Assert.assertTrue((Boolean) deleteResult.getReturnValue());
        Assert.assertTrue(deleteResult.isSuccessful());
        blockchain.setSender(legitimateOwner);
    }

    @Test
    public void testDeleteArtifactThatExistsFromHackerAccount() {
        setUp(true);
        blockchain.createBlock();
        blockchain.sendEther(hackerIdentity.getAddress(), BigInteger.valueOf(initalAccountBalance));

        blockchain.createBlock();
        userContract.callFunction("assignArtifactOwner", artifactOwner.getAddress());

        byte[] sampleIPFSContentAddress = "XcF2WQi2j7c7UgJTarActp7tDNikE4B2qXtFCfLPdsbcTK".getBytes();
        blockchain.createBlock();
        SolidityCallResult result = userContract.callFunction("addArtifact", sampleIPFSContentAddress, 1000);
        BigInteger artifactId = ((BigInteger) result.getReturnValue()).subtract(BigInteger.valueOf(1));

        blockchain.setSender(hackerIdentity);
        blockchain.createBlock();
        SolidityCallResult deleteResult = userContract.callFunction("deleteArtifact", artifactId);
        Assert.assertFalse(deleteResult.isSuccessful());
        blockchain.setSender(legitimateOwner);
    }

    @Test
    public void testDeleteArtifactThatExistsFromUnilogAccount() {
        setUp(true);
        blockchain.createBlock();
        userContract.callFunction("assignArtifactOwner", artifactOwner.getAddress());

        byte[] sampleIPFSContentAddress = "XcF2WQi2j7c7UgJTarActp7tDNikE4B2qXtFCfLPdsbcTK".getBytes();
        blockchain.createBlock();
        SolidityCallResult result = userContract.callFunction("addArtifact", sampleIPFSContentAddress, 1000);
        BigInteger artifactId = ((BigInteger) result.getReturnValue()).subtract(BigInteger.valueOf(1));

        blockchain.createBlock();
        SolidityCallResult deleteResult = userContract.callFunction("deleteArtifact", artifactId);
        Assert.assertFalse((Boolean) deleteResult.getReturnValue());
    }

    @Test
    public void testDeleteArtifactThatDoesNotExist() {
        setUp(true);
        blockchain.createBlock();
        blockchain.sendEther(artifactOwner.getAddress(), BigInteger.valueOf(initalAccountBalance));

        blockchain.createBlock();
        userContract.callFunction("assignArtifactOwner", artifactOwner.getAddress());

        byte[] sampleIPFSContentAddress = "XcF2WQi2j7c7UgJTarActp7tDNikE4B2qXtFCfLPdsbcTK".getBytes();
        blockchain.createBlock();
        userContract.callFunction("addArtifact", sampleIPFSContentAddress, 1000);

        blockchain.setSender(artifactOwner);
        blockchain.createBlock();
        SolidityCallResult deleteResult = userContract.callFunction("deleteArtifact", 8);
        Assert.assertFalse((Boolean) deleteResult.getReturnValue());
        Assert.assertTrue(deleteResult.isSuccessful());
        blockchain.setSender(legitimateOwner);
    }

    @Test
    public void testDeleteArtifactWhenNoArtifactsExist() {
        setUp(true);
        blockchain.createBlock();
        blockchain.sendEther(artifactOwner.getAddress(), BigInteger.valueOf(initalAccountBalance));

        blockchain.createBlock();
        userContract.callFunction("assignArtifactOwner", artifactOwner.getAddress());

        blockchain.setSender(artifactOwner);
        blockchain.createBlock();
        SolidityCallResult deleteResult = userContract.callFunction("deleteArtifact", 0);
        Assert.assertFalse((Boolean) deleteResult.getReturnValue());
        Assert.assertTrue(deleteResult.isSuccessful());
        blockchain.setSender(legitimateOwner);
    }



    public void setUp(final boolean reset) {
        if (setUpIsDone) {
            if (reset) {
                resetBlockChain();
            }
            return;
        }
        //Low difficulty to speed up mining.
        SystemProperties.getDefault().setBlockchainConfig(new FrontierConfig(
                new FrontierConfig.FrontierConstants() {
                    @Override
                    public BigInteger getMINIMUM_DIFFICULTY() {
                        return BigInteger.ONE;
                    }
                }));
        resetBlockChain();
        setUpIsDone = true;
    }

    private void resetBlockChain() {
        blockchain = new StandaloneBlockchain().withAutoblock(true);
        legitimateOwner = blockchain.getSender();
        blockchain.createBlock();
        registryContract = blockchain.submitNewContract(getSmartContract(SMART_CONTRACT_REGISTRY));
        blockchain.createBlock();
        userContract = blockchain.submitNewContract(getSmartContract(SMART_CONTRACT_USER));
    }

    private String getSmartContract(final String contract) {
        try {
            return StringUtils.normalizeSpace(
                    IOUtils.toString(
                            this.getClass().getClassLoader().getResourceAsStream(contract)));
        }
        catch (IOException e) {
            LOGGER.error("Failed to serialize contract", e);
        }
        return null;
    }
}
