package APITests;

import org.example.entity.Wallet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class DeleteWalletTest extends BaseTest{

    @Test
    @DisplayName("DELETE /wallets/{id} - успешное удаление кошелька")
    void deleteWalletPosTest() throws Exception {
        Wallet wallet = createTestWallet(BigDecimal.ZERO);
        deleteWallet(wallet.getId()).andExpect(status().isNoContent());
        getBalance(wallet.getId()).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /wallets/{id} - попытка удаления несуществующего кошелька")
    void deleteWalletNegTest() throws Exception {
        UUID id = UUID.randomUUID();
        deleteWallet(id).andExpect(status().isNotFound());
    }
}
