package APITests;

import io.restassured.http.ContentType;
import org.example.dto.WalletRequest;
import org.example.enums.OperationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class WalletErrorTest extends BaseTest {

    @Test
    @DisplayName("GET /wallets/{id} - несуществующий ID → 404")
    public void getNonExistentWallet() {
        UUID randomId = UUID.randomUUID();

        given()
                .pathParam("walletId", randomId)
                .when()
                .get("/wallets/{walletId}")
                .then()
                .statusCode(404)
                .body("status", equalTo(404))
                .body("error", equalTo("Not Found"))
                .body("message", containsString(randomId.toString()))
                .body("path", containsString("/api/v1/wallets/" + randomId));
    }

    @Test
    @DisplayName("POST /wallet - WITHDRAW на сумму больше баланса → 400")
    public void withdrawMoreThanBalance() {
        WalletRequest request = new WalletRequest(testWalletId, OperationType.WITHDRAW, new BigDecimal("2000.00"));

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/wallet")
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"))
                .body("message", containsString(
                        "Недостаточно средств для снятия 2000.00 из кошелька " + testWalletId));
    }

    @Test
    @DisplayName("POST /wallet - невалидный UUID → 400")
    public void invalidUuidFormat() {
        String badJson = """
                {
                    "walletId": "001",
                    "operationType": "DEPOSIT",
                    "amount": 100
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(badJson)
                .when()
                .post("/wallet")
                .then()
                .statusCode(400)
                .body("message", containsString(
                        "Неправильный запрос JSON или неверный формат данных"));
    }
}
