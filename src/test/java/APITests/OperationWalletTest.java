package APITests;

import org.example.entity.Wallet;
import org.example.enums.OperationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OperationWalletTest extends BaseTest {

    @Test
    @DisplayName("POST /wallet - DEPOSIT")
    void depositPosTest() throws Exception {
        Wallet wallet = createTestWallet(new BigDecimal("1000.00"));
        ResultActions result = operation(wallet.getId(), OperationType.DEPOSIT, new BigDecimal("500.00"));
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1500.00));
    }

    @Test
    @DisplayName("POST /wallet - WITHDRAW")
    void withdrawPosTest() throws Exception {
        Wallet wallet = createTestWallet(new BigDecimal("1000.00"));
        ResultActions result = operation(wallet.getId(), OperationType.WITHDRAW, new BigDecimal("700.00"));
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(300.00));
    }

    @Test
    @DisplayName("POST /wallet - WITHDRAW больше чем баланс")
    void withdrawNegTest() throws Exception {
        Wallet wallet = createTestWallet(new BigDecimal("1000.00"));
        ResultActions result = operation(wallet.getId(), OperationType.WITHDRAW, new BigDecimal("1200.00"));
        result.andExpect(status().isBadRequest());
    }
}
