package APITests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.App;
import org.example.dto.WalletRequest;
import org.example.entity.Wallet;
import org.example.enums.OperationType;
import org.example.repository.WalletRepository;
import org.example.service.WalletService;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@AutoConfigureMockMvc
@SpringBootTest(classes = App.class)
@ActiveProfiles("test")
public abstract class BaseTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected WalletRepository walletRepository;

    @Autowired
    protected WalletService walletService;

    protected static final String BASE_URL = "/api/v1";

    @AfterEach
    protected void cleanUpBD() {
        walletRepository.deleteAll();
    }

    /**
     * Создание кошелька
     * POST /api/v1/wallets
     */
    protected ResultActions createWallet(BigDecimal balance) throws Exception {
        MockHttpServletRequestBuilder request =
                post(BASE_URL + "/wallets").contentType(MediaType.APPLICATION_JSON);
        request.param("balance", balance.toString());
        return mockMvc.perform(request);
    }

    /**
     * Проведение операции над кошельком. (DEPOSIT or WITHDRAW)
     * POST /api/v1/wallet
     */
    protected ResultActions operation(UUID walletId, OperationType operationType, BigDecimal amount) throws Exception {
        WalletRequest request = new WalletRequest(walletId, operationType, amount);
        return mockMvc.perform(post(BASE_URL + "/wallet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    /**
     * Получение баланса кошелька
     * GET /api/v1/wallets/{walletId}
     */
    protected ResultActions getBalance(UUID walletId) throws Exception {
        return mockMvc.perform(get(BASE_URL + "/wallets/{walletId}", walletId));
    }

    /**
     * Получение всех кошельков
     * GET /api/v1/wallets
     */
    protected ResultActions getAllWallets() throws Exception {
        return mockMvc.perform(get(BASE_URL + "/wallets"));
    }

    /**
     * Обновление баланса кошелька
     * PUT /api/v1/wallets/{walletId}?balance=...
     */
    protected ResultActions updateBalance(UUID walletId, BigDecimal newBalance) throws Exception {
        return mockMvc.perform(put(BASE_URL + "/wallets/{walletId}",
                walletId).param("balance", newBalance.toString()));
    }

    /**
     * Удаление кошелька
     * DELETE /api/v1/wallets/{walletId}
     */
    protected ResultActions deleteWallet(UUID walletId) throws Exception {
        return mockMvc.perform(delete(BASE_URL + "/wallets/{walletId}", walletId));
    }

    /**
     * Создание кошелька напрямую в базе данных
     */
    protected Wallet createTestWallet(BigDecimal balance) {
        return walletService.createWallet(balance);
    }
}
