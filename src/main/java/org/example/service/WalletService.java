package org.example.service;

import org.example.entity.Wallet;
import org.example.enums.OperationType;
import org.example.exception.InsufficientFundsException;
import org.example.exception.WalletNotFoundException;
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

    private Wallet getWalletWithLock(UUID id) {
        return walletRepository.findByIdWithPessimisticLock(id)
                .orElseThrow(() -> new WalletNotFoundException("Кошелек не найден: " + id));
    }

    @Transactional
    public Wallet createWallet(BigDecimal balance) {
        Wallet wallet = new Wallet(balance);
        return walletRepository.save(wallet);
    }

    @Transactional
    public Wallet getWallet(UUID id) {
        return walletRepository.findById(id).orElseThrow(() ->
                new WalletNotFoundException("Кошелек не найден: " + id));
    }

    @Transactional
    public List<Wallet> getAllWallets() {
        return walletRepository.findAll();
    }

    @Transactional
    public Wallet updateWalletBalance(UUID id, BigDecimal balance) {
        Wallet wallet = getWalletWithLock(id);
        wallet.setBalance(balance);
        return walletRepository.save(wallet);
    }

    @Transactional
    public void deleteWallet(UUID id) {
        if (!walletRepository.existsById(id))
            throw new WalletNotFoundException("Кошелек не найден: " + id);
        walletRepository.deleteById(id);
    }

    /**
     * Проведение операции над кошельком. Снятие или пополнение баланса.
     * @param id     UUID кошелька
     * @param type   Тип операции (DEPOSIT or WITHDRAW)
     * @param amount сумма денег
     * @return Кошелек с измененным балансом
     */
    @Transactional
    public Wallet operation(UUID id, OperationType type, BigDecimal amount) {
        Wallet wallet = getWalletWithLock(id);
        switch (type) {
            case DEPOSIT -> wallet.setBalance(wallet.getBalance().add(amount));
            case WITHDRAW -> {
                if (wallet.getBalance().compareTo(amount) < 0) {
                    throw new InsufficientFundsException(String.format(
                            "Недостаточно средств для снятия %s из кошелька %s", amount, id));
                }
                wallet.setBalance(wallet.getBalance().subtract(amount));
            }
        }
        return walletRepository.save(wallet);
    }
}
