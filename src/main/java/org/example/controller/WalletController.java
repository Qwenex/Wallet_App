package org.example.controller;

import org.example.dto.Request;
import org.example.dto.Response;
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
    public ResponseEntity<Response> createWallet(@RequestParam BigDecimal balance) {
        Wallet createdWallet = walletService.createWallet(balance);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Response(createdWallet.getId(), createdWallet.getBalance()));
    }

    @PostMapping("/wallet")
    public ResponseEntity<Response> operation(@RequestBody Request request) {
        Wallet updatedWallet = walletService.operation(request.id(), request.type(), request.amount());
        return ResponseEntity.ok(new Response(updatedWallet.getId(), updatedWallet.getBalance()));
    }

    @GetMapping("/wallets/{walletId}")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable UUID id) {
        BigDecimal balance = walletService.getWallet(id).getBalance();
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/wallets")
    public ResponseEntity<List<Response>> getAllWallets() {
        List<Response> wallets = walletService.getAllWallets().stream()
                .map(w -> new Response(w.getId(), w.getBalance()))
                .toList();
        return ResponseEntity.ok(wallets);
    }

    @PutMapping("/wallets/{walletId}")
    public ResponseEntity<Response> updateBalance(@PathVariable UUID id,
                                                  @RequestParam BigDecimal balance) {
        Wallet updated = walletService.updateWalletBalance(id, balance);
        return ResponseEntity.ok(new Response(updated.getId(), updated.getBalance()));
    }

    @DeleteMapping("/wallets/{walletId}")
    public ResponseEntity<Void> deleteWallet(@PathVariable UUID id) {
        walletService.deleteWallet(id);
        return ResponseEntity.noContent().build();
    }
}
