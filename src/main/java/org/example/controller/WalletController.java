package org.example.controller;

import org.example.dto.WalletRequest;
import org.example.dto.WalletResponse;
import org.example.entity.Wallet;
import org.example.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class WalletController {

    private final WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/wallets")
    public ResponseEntity<WalletResponse> createWallet(@RequestParam BigDecimal balance) {
        Wallet createdWallet = walletService.createWallet(balance);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new WalletResponse(createdWallet.getId(), createdWallet.getBalance()));
    }

    @PostMapping("/wallet")
    public ResponseEntity<WalletResponse> operation(@RequestBody WalletRequest request) {
        Wallet updatedWallet = walletService.operation(request.walletId(), request.operationType(), request.amount());
        return ResponseEntity.ok(new WalletResponse(updatedWallet.getId(), updatedWallet.getBalance()));
    }

    @GetMapping("/wallets/{walletId}")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable UUID walletId) {
        BigDecimal balance = walletService.getWallet(walletId).getBalance();
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/wallets")
    public ResponseEntity<List<WalletResponse>> getAllWallets() {
        List<WalletResponse> wallets = walletService.getAllWallets().stream()
                .map(w -> new WalletResponse(w.getId(), w.getBalance()))
                .toList();
        return ResponseEntity.ok(wallets);
    }

    @PutMapping("/wallets/{walletId}")
    public ResponseEntity<WalletResponse> updateBalance(@PathVariable UUID walletId,
                                                        @RequestParam BigDecimal balance) {
        Wallet updated = walletService.updateWalletBalance(walletId, balance);
        return ResponseEntity.ok(new WalletResponse(updated.getId(), updated.getBalance()));
    }

    @DeleteMapping("/wallets/{walletId}")
    public ResponseEntity<Void> deleteWallet(@PathVariable UUID walletId) {
        walletService.deleteWallet(walletId);
        return ResponseEntity.noContent().build();
    }
}
