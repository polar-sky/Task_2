package praktikum;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import praktikum.resources.EnvConfig;

import java.net.HttpURLConnection;
import java.util.Map;

import static praktikum.UserApiClient.bearerToken;

public class OrderApiClient {

    @Step("Создание заказа")
    public ValidatableResponse createOrder(String[] ingredients) {

        return Client.setUp()
                .header("Authorization", bearerToken)
                .body(Map.of("ingredients", ingredients))
                .when()
                .post(EnvConfig.ORDERS)
                .then()
                .log().all();
    }

    @Step("Проверка успешного создания заказа")
    public boolean isOrderCreated(ValidatableResponse createResp) {
        int statusCode = createResp.extract().statusCode();
        return statusCode == HttpURLConnection.HTTP_OK;
    }

    @Step("Получение заказов пользователя")
    public ValidatableResponse getOrders() {
        return Client.setUp()
                .header("Authorization", bearerToken)
                .when()
                .get(EnvConfig.ORDERS)
                .then()
                .log().all();
    }
}
