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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    private final HashMap<UUID, ReentrantReadWriteLock> lockHashMap = new HashMap<>();

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
        ReentrantReadWriteLock.ReadLock readLock = lockHashMap.putIfAbsent(id, new ReentrantReadWriteLock()).readLock();
        try {
            readLock.lock();
            return walletRepository.findById(id).orElseThrow(() ->
                    new WalletNotFoundException("Кошелек не найден: " + id));
        } finally {
            readLock.unlock();
        }
    }

    @Transactional
    public List<Wallet> getAllWallets() {
        Collection<ReentrantReadWriteLock> values = lockHashMap.values();
        try {
            values.forEach(x -> x.readLock().lock());
            return walletRepository.findAll();
        } finally {
            values.forEach(x -> x.readLock().unlock());
        }
    }

    @Transactional
    public Wallet updateWalletBalance(UUID id, BigDecimal balance) {
        ReentrantReadWriteLock.WriteLock writeLock = lockHashMap.putIfAbsent(id, new ReentrantReadWriteLock()).writeLock();
        try {
            writeLock.lock();
            Wallet wallet = getWallet(id);
            wallet.setBalance(balance);
            return walletRepository.save(wallet);
        } finally {
            writeLock.unlock();
        }
    }

    @Transactional
    public void deleteWallet(UUID id) {
        ReentrantReadWriteLock.WriteLock writeLock = lockHashMap.putIfAbsent(id, new ReentrantReadWriteLock()).writeLock();
        try {
            writeLock.lock();
            if (!walletRepository.existsById(id))
                throw new WalletNotFoundException("Кошелек не найден: " + id);
            walletRepository.deleteById(id);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Проведение операции над кошельком. Снятие или пополнение баланса.
     * @param id UUID кошелька
     * @param type Тип операции (DEPOSIT or WITHDRAW)
     * @param amount сумма денег
     * @return Кошелек с измененным балансом
     */
    @Transactional
    public Wallet operation(UUID id, OperationType type, BigDecimal amount) {
        ReentrantReadWriteLock.WriteLock writeLock = lockHashMap.putIfAbsent(id, new ReentrantReadWriteLock()).writeLock();
        try {
            writeLock.lock();
            Wallet wallet = getWallet(id);
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
        } finally {
            writeLock.unlock();
        }
    }
}
