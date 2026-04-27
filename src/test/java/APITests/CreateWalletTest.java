package APITests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CreateWalletTest extends BaseTest {

    @Test
    @DisplayName("POST /wallets создание нового кошелька")
    void createWalletTest() throws Exception {
        ResultActions result = createWallet(new BigDecimal("250.50"));
        result.andExpect(status().isCreated());
    }
}
