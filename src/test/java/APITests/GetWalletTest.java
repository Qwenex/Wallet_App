package APITests;

import org.example.entity.Wallet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class GetWalletTest extends BaseTest {

    @Test
    @DisplayName("GET /wallets/{id} - получение баланса")
    void getBalancePosTest() throws Exception {
        Wallet wallet = createTestWallet(new BigDecimal("1500.00"));
        ResultActions result = getBalance(wallet.getId());
        result.andExpect(status().isOk())
                .andExpect(content().string("1500.00"));
    }

    @Test
    @DisplayName("GET /wallets/{id} - попытка получения баланса у несущ. кошелька")
    void getBalanceNegTest() throws Exception {
        UUID id = UUID.randomUUID();
        getBalance(id).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /wallets - получение всех кошельков")
    void getAllWalletsTest() throws Exception {
        createTestWallet(new BigDecimal("100.00"));
        createTestWallet(new BigDecimal("200.00"));
        ResultActions result = getAllWallets();

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].balance").value(100.00))
                .andExpect(jsonPath("$[1].balance").value(200.00));
    }
}
