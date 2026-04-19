package APITests;

import org.example.entity.Wallet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UpdateWalletTest extends BaseTest {

    @Test
    @DisplayName("POST /wallets - обновление баланса кошелька напрямую")
    void updateWalletPosTest() throws Exception {
        Wallet wallet = createTestWallet(new BigDecimal("250.50"));
        ResultActions result = updateBalance(wallet.getId(),new BigDecimal("500.00"));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(500.00));
    }

    @Test
    @DisplayName("POST /wallets - попытка обновления баланса у несущ. кошелька")
    void updateWalletNegTest() throws Exception {
        UUID id = UUID.randomUUID();
        updateBalance(id ,new BigDecimal("123.00")).andExpect(status().isNotFound());
    }
}
