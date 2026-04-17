package APITests;

import io.restassured.RestAssured;
import org.example.App;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
public abstract class BaseTest {

    @LocalServerPort
    private int port;

    protected UUID testWalletId;

    @BeforeEach
    void setUp() {
        RestAssured.port = this.port;
        RestAssured.basePath = "/api/v1";
    }

    @BeforeEach
    public void createWallet() {
        String id = given()
                .queryParam("balance", "1000")
                .post("/wallets")
                .then()
                .extract()
                .path("walletId");
        testWalletId = UUID.fromString(id);
    }

    @AfterEach
    public void cleanup() {
        if (testWalletId != null) {
            given()
                    .pathParam("walletId", testWalletId)
                    .when()
                    .delete("/wallets/{walletId}")
                    .then()
                    .statusCode(anyOf(is(204), is(404)));
        }
    }
}
