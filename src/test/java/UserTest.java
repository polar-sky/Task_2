import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import praktikum.UserApiClient;
import praktikum.entity.User;
import praktikum.resources.ErrorMessageConfig;

import java.net.HttpURLConnection;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;

public class UserTest {
    private User user;
    private UserApiClient client = new UserApiClient();
    private boolean isUserCreated = false;

    @BeforeEach
    public void setUp() {
        user = User.randomUser();
    }

    @Test
    @DisplayName("POST /api/auth/register. Проверка создания уникального пользователя")
    public void createUser() {
        ValidatableResponse createResp = client.createUser(user);
        createResp.assertThat().statusCode(HttpURLConnection.HTTP_OK);
        isUserCreated = client.isUserCreated(createResp);
    }

    @Test
    @DisplayName("POST /api/auth/register. Проверка создания уже существующего пользователя")
    public void createExistingUser() {
        ValidatableResponse createResp = client.createUser(user);
        ValidatableResponse createResp2 = client.createUser(user);
        createResp2.assertThat().statusCode(HttpURLConnection.HTTP_FORBIDDEN);
    }

    @ParameterizedTest
    @MethodSource("invalidUsers")
    @DisplayName("POST /api/auth/register. Проверка обязательности полей")
    public void createUserWithoutRequiredField(User user, String missingField) {
        ValidatableResponse response = client.createUser(user);
        response.assertThat()
                .statusCode(HttpURLConnection.HTTP_FORBIDDEN)
                .body("success", equalTo(false));
    }

    @ParameterizedTest
    @MethodSource("userUpdates")
    @DisplayName("PATCH /api/auth/user. Изменение данных с авторизацией ")
    public void changeAuthorizedUserInfoTest() {
        client.createUser(user);
        ValidatableResponse createResp = client.updateUser(user);
        createResp
                .assertThat()
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()));
    }

    @ParameterizedTest
    @MethodSource("userUpdates")
    @DisplayName("PATCH /api/auth/user. Изменение данных без авторизации ")
    public void changeUnauthorizedUserInfoTest() {
        client.createUser(user);
        client.logoutUser();
        ValidatableResponse createResp = client.updateUser(user);
        createResp
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo(ErrorMessageConfig.UNAUTHORIZED))
                .statusCode(HttpURLConnection.HTTP_UNAUTHORIZED);
    }

    @AfterEach
    public void tearDown() {
        if (isUserCreated) {
            client.deleteUser();
        }
    }

    static Stream<Arguments> invalidUsers() {
        return Stream.of(
                Arguments.of(
                        new User(null, "password123", "Акакий"),
                        "email"
                ),
                Arguments.of(
                        new User("akaki@yandex.ru", null, "Акакий"),
                        "password"
                ),
                Arguments.of(
                        new User("akaki@yandex.ru", "password123", null),
                        "name"
                ),
                Arguments.of(
                        new User(null, null, null),
                        "all fields null"
                )
        );
    }

    static Stream<Arguments> userUpdates() {
        return Stream.of(
                Arguments.of(
                        "Изменение email",
                        new User("new_email" + System.currentTimeMillis() + "@yandex.ru", null, null)
                ),
                Arguments.of(
                        "Изменение password",
                        new User(null, "newPassword123", null)
                ),
                Arguments.of(
                        "Изменение name",
                        new User(null, null, "НовоеИмя")
                ),
                Arguments.of(
                        "Изменение email и name",
                        new User("both_" + System.currentTimeMillis() + "@yandex.ru", null, "НовоеИмя")
                ),
                Arguments.of(
                        "Изменение email, name, password",
                        new User("new_email" + System.currentTimeMillis() + "@yandex.ru", "newPassword123", "НовоеИмя")
                )
        );
    }

}
