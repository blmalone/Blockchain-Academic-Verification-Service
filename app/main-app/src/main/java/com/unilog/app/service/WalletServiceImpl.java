package com.unilog.app.service;

import com.unilog.app.entity.Wallet;
import org.ethereum.crypto.ECKey;
import org.springframework.stereotype.Service;

import static org.bouncycastle.util.encoders.Hex.toHexString;

@Service
public class WalletServiceImpl implements WalletService {

    @Override
    public Wallet generateNewWallet() {
        final ECKey key = new ECKey();
        byte[] address = key.getAddress();
        byte[] privateKey = key.getPrivKeyBytes();
        byte[] publicKey = key.getPubKey();

        Wallet wallet = new Wallet();
        //Look at encrypting this if I intend to store it on database. Possibly RSA encryption.
        wallet.setPrivateKey(toHexString(privateKey));
        wallet.setPublicKey(toHexString(publicKey));
        wallet.setWalletAddress(toHexString(address));
        return wallet;
    }
}
