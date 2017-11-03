package com.unilog.app.service;

import com.unilog.app.entity.Wallet;
import org.springframework.stereotype.Service;

@Service
public interface WalletService {

    Wallet generateNewWallet();
}
