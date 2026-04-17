package org.example.service;

import org.example.entity.Wallet;
import org.example.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    @Autowired
    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Transactional
    public Wallet createWallet(BigDecimal balance) {
        Wallet wallet = new Wallet(balance);
        return walletRepository.save(wallet);
    }

    @Transactional
    public Wallet getWallet(UUID id) {
        return walletRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Wallet not found: " + id));
    }

    @Transactional
    public List<Wallet> getAllWallets() {
        return walletRepository.findAll();
    }

    @Transactional
    public Wallet updateWalletBalance(UUID id, BigDecimal balance) {
        Wallet wallet = getWallet(id);
        wallet.setBalance(balance);
        return walletRepository.save(wallet);
    }

    @Transactional
    public void deleteWallet(UUID id) {
        if (!walletRepository.existsById(id))
            throw new RuntimeException("Wallet not found: " + id);
        walletRepository.deleteById(id);
    }

    @Transactional
    public Wallet operation(UUID id, OperationType type, BigDecimal amount) {
        Wallet wallet = getWallet(id);
        switch (type) {
            case DEPOSIT -> wallet.setBalance(wallet.getBalance().add(amount));
            case WITHDRAW -> {
                if (wallet.getBalance().compareTo(amount) < 0) {
                    throw new RuntimeException(String.format(
                            "Недостаточно средств для снятия %s в кошельке %s", amount, id));
                }
                wallet.setBalance(wallet.getBalance().subtract(amount));
            }
        }
        return walletRepository.save(wallet);
    }
}
