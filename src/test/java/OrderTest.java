import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import praktikum.OrderApiClient;
import praktikum.UserApiClient;
import praktikum.entity.Order;
import praktikum.entity.User;
import praktikum.resources.ErrorMessageConfig;

import java.net.HttpURLConnection;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;

public class OrderTest {
    private User user;
    private OrderApiClient orderClient = new OrderApiClient();
    private UserApiClient userClient = new UserApiClient();
    private final String[] ingredients = {"691577430cc94f001a65b85f", "691577430cc94f001a65b85d", "691577430cc94f001a65b859"};

    @BeforeEach
    public void setUp() {
        user = User.randomUser();
        userClient.createUser(user);
    }

    @Test
    @DisplayName("POST /api/orders. Создание заказа с авторизацией ")
    public void createOrderAuthorizedTest() {

        ValidatableResponse createResp = orderClient.createOrder(ingredients);
        createResp
                .assertThat()
                .body("success", equalTo(true))
                .body("order.owner.name", equalTo(user.getName()))
                .body("order.owner.email", equalTo(user.getEmail()))
                .body("name", not(emptyString()))
                .body("order.price", notNullValue())
                .body("order.number", notNullValue());

    }

    @Test
    @DisplayName("POST /api/orders. Создание заказа без авторизации ")
    public void createOrderUnauthorizedTest() {
        userClient.logoutUser();
        ValidatableResponse createResp = orderClient.createOrder(ingredients);
        createResp
                .assertThat()
                .body("success", equalTo(true))
                .body("order.owner.name", nullValue())
                .body("order.owner.email", nullValue())
                .body("order.order.price", nullValue())
                .body("name", not(emptyString()))
                .body("order.number", notNullValue());

    }

    @ParameterizedTest(name = "Негативный кейс: {0}")
    @MethodSource("invalidOrders")
    @DisplayName("POST /api/orders. Негативные сценарии")
    public void createOrderWithInvalidIngredientsTest(String caseName,
                                                      String[] ingredients) {
        ValidatableResponse createResp = orderClient.createOrder(ingredients);

        if (caseName.equals("Пустой массив ингредиентов")) {
            createResp.assertThat()
                    .statusCode(HttpURLConnection.HTTP_BAD_REQUEST)
                    .body("success", equalTo(false))
                    .body("message", equalTo(ErrorMessageConfig.NO_INGREDIENTS));

        } else {
            createResp.assertThat()
                    .statusCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    @Test
    @DisplayName("GET /api/orders. Получение заказов конкретного пользователя с авторизацией")
    public void getAuthorizedOrders() {
        ValidatableResponse createResp = orderClient.getOrders();
        createResp.assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("GET /api/orders. Получение заказов конкретного пользователя без авторизации")
    public void getUnauthorizedOrders() {
        userClient.logoutUser();
        ValidatableResponse createResp = orderClient.getOrders();
        createResp.assertThat()
                .statusCode(HttpURLConnection.HTTP_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo(ErrorMessageConfig.UNAUTHORIZED));
    }


    @AfterEach
    public void tearDown() {
        userClient.deleteUser();
    }


    static Stream<Arguments> invalidOrders() {
        return Stream.of(
                Arguments.of(
                        "Пустой массив ингредиентов",
                        new String[]{}

                ),
                Arguments.of(
                        "Невалидный id ингредиента",
                        new String[]{"invalid_id_123"}
                ),
                Arguments.of(
                        "Один невалидный id ингредиента, два другие валидные",
                        new String[]{"invalid_id_123", "691577430cc94f001a65b85f", "691577430cc94f001a65b85d"}
                )
        );
    }
}
