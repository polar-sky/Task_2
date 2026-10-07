package praktikum;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import praktikum.entity.User;
import praktikum.resources.EnvConfig;

import java.net.HttpURLConnection;
import java.util.Map;

public class UserApiClient {
    static String bearerToken;
    private static String refreshToken;


    @Step("Создание пользователя")
    public ValidatableResponse createUser(User user) {
        ValidatableResponse response = Client.setUp()
                .body(user)
                .when()
                .post(EnvConfig.CREATE_USER)
                .then()
                .log().all();
        bearerToken = response.extract().path("accessToken");
        refreshToken = response.extract().path("refreshToken");

        return response;
    }

    @Step("Проверка успешного создания пользователя")
    public boolean isUserCreated(ValidatableResponse createResp) {
        int statusCode = createResp.extract().statusCode();
        return statusCode == HttpURLConnection.HTTP_OK;
    }

    @Step("Логин пользователя в системе")
    public ValidatableResponse loginUser(User user) {
        return Client.setUp()
                .body(user)
                .when()
                .post(EnvConfig.LOGIN)
                .then()
                .log().all();
    }

    @Step("Проверка успешного логина пользователя")
    public boolean isUserLoggedIn(ValidatableResponse createResp) {
        int statusCode = createResp.extract().statusCode();
        return statusCode == 200;
    }

    @Step("Удаление пользователя")
    public void deleteUser() {
        Client.setUp()
                .header("Authorization", bearerToken)
                .when()
                .delete(EnvConfig.USER)
                .then()
                .log().all();
    }

    @Step("Выход пользователя")
    public void logoutUser() {
        Client.setUp()
                .header("Authorization", bearerToken)
                .body(Map.of("token", refreshToken))
                .when()
                .post(EnvConfig.LOGOUT)
                .then()
                .log().all();
        bearerToken = "";
        refreshToken = "";
    }

    @Step("Изменение данных пользователя")
    public ValidatableResponse updateUser(User user) {
        return Client.setUp()
                .header("Authorization", bearerToken)
                .body(user)
                .when()
                .patch(EnvConfig.USER)
                .then()
                .log().all();
    }

}
