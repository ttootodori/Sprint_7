package tests;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class LoginCourierTest {

    private String createdCourierLogin;
    private String createdCourierPassword;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @After
    public void tearDown() {
        if (createdCourierLogin != null && createdCourierPassword != null) {
            deleteCourier(createdCourierLogin, createdCourierPassword);
        }
    }

    // ================ TESTS ================

    @Test
    @DisplayName("Курьер может авторизоваться - успешный логин возвращает id")
    public void courierCanLoginTest() {
        String login = generateUniqueLogin();
        String password = generatePassword();

        createdCourierLogin = login;
        createdCourierPassword = password;

        createCourier(login, password);

        ValidatableResponse response = loginCourier(login, password);
        response.statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Нельзя авторизоваться без логина - возвращается ошибка 400")
    public void cannotLoginWithoutLoginTest() {
        ValidatableResponse response = loginCourier("", generatePassword());

        response.statusCode(400)
                .body("message", notNullValue());
    }

    @Test
    @DisplayName("Нельзя авторизоваться без пароля - возвращается ошибка 400")
    public void cannotLoginWithoutPasswordTest() {
        String login = generateUniqueLogin();
        String password = generatePassword();

        createdCourierLogin = login;
        createdCourierPassword = password;

        createCourier(login, password);

        ValidatableResponse response = loginCourier(login, "");
        response.statusCode(400)
                .body("message", notNullValue());
    }

    @Test
    @DisplayName("Ошибка при несуществующей паре логин-пароль - возвращается ошибка 404")
    public void cannotLoginWithWrongCredentialsTest() {
        ValidatableResponse response = loginCourier(
                "non_existent_" + System.currentTimeMillis(),
                generatePassword()
        );

        response.statusCode(404)
                .body("message", notNullValue());
    }

    // ================ STEPS ================

    @Step("Сгенерировать уникальный логин")
    public String generateUniqueLogin() {
        return "courier_" + System.currentTimeMillis();
    }

    @Step("Сгенерировать пароль")
    public String generatePassword() {
        return "pass_" + System.currentTimeMillis();
    }

    @Step("Создание курьера с логином: {login}")
    public void createCourier(String login, String password) {
        String body = String.format(
                "{\"login\":\"%s\",\"password\":\"%s\",\"firstName\":\"Test\"}",
                login, password
        );

        given()
                .header("Content-type", "application/json")
                .body(body)
                .post("/api/v1/courier")
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Step("Авторизация курьера с логином: {login}")
    public ValidatableResponse loginCourier(String login, String password) {
        String body = String.format(
                "{\"login\":\"%s\",\"password\":\"%s\"}",
                login, password
        );

        return given()
                .header("Content-type", "application/json")
                .body(body)
                .post("/api/v1/courier/login")
                .then();
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
