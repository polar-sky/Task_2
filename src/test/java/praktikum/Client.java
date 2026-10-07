package praktikum;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import praktikum.resources.EnvConfig;

import static io.restassured.RestAssured.given;
import static io.restassured.config.HttpClientConfig.httpClientConfig;

public class Client {
    public static final String BASE_PATH = "/api";

    public static RequestSpecification setUp() {
        return given()
                .config(RestAssuredConfig.config()
                        .httpClient(httpClientConfig()
                                .setParam("http.connection.timeout", 5000)   // 5 секунд на соединение
                                .setParam("http.socket.timeout", 5000)))
                .log().all()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .baseUri(EnvConfig.BASE_URI)
                .basePath(BASE_PATH);
    }
}
