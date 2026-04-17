package APITests;

import io.restassured.http.ContentType;
import org.example.dto.WalletRequest;
import org.example.enums.OperationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class WalletOperationsTest extends BaseTest {

    @Test
    @DisplayName("POST /api/v1/wallet - DEPOSIT")
    public void depositBalance() {
        WalletRequest request = new WalletRequest(testWalletId, OperationType.DEPOSIT, new BigDecimal("500.00"));

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/wallet")
                .then()
                .statusCode(200)
                .body("walletId", equalTo(testWalletId.toString()))
                .body("balance", equalTo(1500.00f));
    }

    @Test
    @DisplayName("POST /api/v1/wallet - WITHDRAW")
    public void withdrawBalance() {
        WalletRequest request = new WalletRequest(testWalletId, OperationType.WITHDRAW, new BigDecimal("300.00"));

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/wallet")
                .then()
                .statusCode(200)
                .body("balance", equalTo(700.00f));
    }

    @Test
    @DisplayName("GET /api/v1/wallets/{id} - Get balance")
    public void getBalance() {
        given()
                .pathParam("walletId", testWalletId)
                .when()
                .get("/wallets/{walletId}")
                .then()
                .statusCode(200)
                .body(equalTo("1000.00"));
    }
}
