import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import praktikum.UserApiClient;
import praktikum.entity.User;
import praktikum.resources.ErrorMessageConfig;

import java.net.HttpURLConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginTest {
    private User user;
    private UserApiClient client = new UserApiClient();
    private boolean isUserLoggedIn = false;

    @BeforeEach
    public void setUp() {
        user = User.randomUser();
        client.createUser(user);
    }

    @Test
    @DisplayName("POST /api/auth/login. Проверка успешной авторизации пользователя")
    public void loginCourierSuccessTest() {
        ValidatableResponse createResp = client.loginUser(user);
        createResp.assertThat().statusCode(HttpURLConnection.HTTP_OK);
        isUserLoggedIn = client.isUserLoggedIn(createResp);
    }

    @ParameterizedTest(name = "Проверка ошибки при авторизации пользователя без обязательного поля {0}")
    @ValueSource(strings = {"email", "password"})
    @DisplayName("POST /api/auth/login. Проверка ошибки при авторизации пользователя без обязательных полей")
    public void loginUserWithoutRequiredFieldsErrorTest(String field) {
        if ("email".equals(field)) {
            user.setEmail(null);
        } else if ("password".equals(field)) {
            user.setPassword(null);
        }
        ValidatableResponse createResp = client.loginUser(user);
        createResp
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_UNAUTHORIZED);
        assertEquals(ErrorMessageConfig.LOGIN_BAD_REQUEST, createResp.extract().path("message"));
    }

    @ParameterizedTest(name = "Проверка ошибки при авторизации курьера c {0} = {1}")
    @CsvSource({
            "email, 1",
            "password, 2"
    })
    @DisplayName("POST /api/auth/login. Проверка ошибки при авторизации c несуществующей парой логин-пароль")
    public void loginNonExistingUserTest(String field, String param) {
        if ("email".equals(field)) {
            user.setEmail(param);
        } else if ("password".equals(field)) {
            user.setPassword(param);
        }
        ValidatableResponse createResp = client.loginUser(user);
        createResp
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_UNAUTHORIZED);
        assertEquals(ErrorMessageConfig.LOGIN_BAD_REQUEST, createResp.extract().path("message"));
    }

    @AfterEach
    public void tearDown() {
        if (isUserLoggedIn) {
            client.logoutUser();
            client.deleteUser();
        }
    }
}
