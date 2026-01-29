package tests;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class CreateCourierTests {

    private String lastCreatedCourierLogin;
    private String lastCreatedCourierPassword;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @After
    public void tearDown() {
        if (lastCreatedCourierLogin != null && lastCreatedCourierPassword != null) {
            deleteCourier(lastCreatedCourierLogin, lastCreatedCourierPassword);
        }
    }

    // ===================== TESTS =====================

    @Test
    @DisplayName("Курьера можно создать - успешный запрос возвращает ok: true")
    public void courierCanBeCreatedTest() {
        String login = uniqueLogin();
        String password = generatePassword();

        lastCreatedCourierLogin = login;
        lastCreatedCourierPassword = password;

        String body = courierBody(login, password, "Test");

        ValidatableResponse response = sendCreateCourierRequest(body, 201);
        response.body("ok", notNullValue());
    }

    @Test
    @DisplayName("Нельзя создать двух курьеров с одинаковым логином")
    public void cannotCreateTwoCouriersWithSameLoginTest() {
        String login = uniqueLogin();
        String password = generatePassword();

        lastCreatedCourierLogin = login;
        lastCreatedCourierPassword = password;

        String body = courierBody(login, password, "Test");

        sendCreateCourierRequest(body, 201);
        ValidatableResponse response = sendCreateCourierRequest(body, 409);
        response.body("message", notNullValue());
    }

    @Test
    @DisplayName("Нельзя создать курьера без логина")
    public void cannotCreateCourierWithoutLoginTest() {
        String body = "{ \"password\": \"test_pass\", \"firstName\": \"Test\" }";

        ValidatableResponse response = sendCreateCourierRequest(body, 400);
        response.body("message", notNullValue());
    }

    @Test
    @DisplayName("Нельзя создать курьера без пароля")
    public void cannotCreateCourierWithoutPasswordTest() {
        String body = "{ \"login\": \"test_login\", \"firstName\": \"Test\" }";

        ValidatableResponse response = sendCreateCourierRequest(body, 400);
        response.body("message", notNullValue());
    }

    @Test
    @DisplayName("Можно создать курьера без имени")
    public void canCreateCourierWithoutFirstNameTest() {
        String login = uniqueLogin();
        String password = generatePassword();

        lastCreatedCourierLogin = login;
        lastCreatedCourierPassword = password;

        String body = String.format(
                "{ \"login\": \"%s\", \"password\": \"%s\" }",
                login, password
        );

        ValidatableResponse response = sendCreateCourierRequest(body, 201);
        response.body("ok", notNullValue());
    }

    // ===================== STEPS =====================

    @Step("Сформировать тело запроса для создания курьера")
    public String courierBody(String login, String password, String firstName) {
        return String.format(
                "{\"login\":\"%s\",\"password\":\"%s\",\"firstName\":\"%s\"}",
                login, password, firstName
        );
    }

    @Step("Отправить POST /api/v1/courier и проверить статус {statusCode}")
    public ValidatableResponse sendCreateCourierRequest(String body, int statusCode) {
        return given()
                .header("Content-type", "application/json")
                .body(body)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(statusCode);
    }

    @Step("Сгенерировать уникальный логин курьера")
    public String uniqueLogin() {
        return "courier_" + System.currentTimeMillis();
    }

    @Step("Сгенерировать пароль курьера")
    public String generatePassword() {
        return "pass_" + System.currentTimeMillis();
    }

    @Step("Удалить курьера: {login}")
    private void deleteCourier(String login, String password) {
        try {
            String loginBody = String.format(
                    "{\"login\":\"%s\",\"password\":\"%s\"}",
                    login, password
            );

            Integer courierId = given()
                    .header("Content-type", "application/json")
                    .body(loginBody)
                    .post("/api/v1/courier/login")
                    .then()
                    .extract()
                    .path("id");

            if (courierId != null) {
                given()
                        .delete("/api/v1/courier/" + courierId)
                        .then()
                        .statusCode(200);
            }
        } catch (Exception ignored) {
        }
    }
}
