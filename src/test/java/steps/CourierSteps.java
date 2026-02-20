package steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import model.CourierCreateRequest;
import model.CourierLoginRequest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CourierSteps {

    @Step("Создать курьера")
    public void createCourier(CourierCreateRequest body) {
        given()
                .header("Content-type", "application/json")
                .body(body)
                .post("/api/v1/courier")
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Step("Авторизовать курьера")
    public ValidatableResponse loginCourier(CourierLoginRequest body) {
        return given()
                .header("Content-type", "application/json")
                .body(body)
                .post("/api/v1/courier/login")
                .then();
    }

    @Step("Удалить курьера по id")
    public void deleteCourier(Integer courierId) {
        given()
                .delete("/api/v1/courier/" + courierId)
                .then()
                .statusCode(200);
    }

    @Step("Создать курьера с ожидаемой ошибкой {statusCode}")
    public ValidatableResponse createCourierExpectingError(
            CourierCreateRequest body,
            int statusCode
    ) {
        return given()
                .header("Content-type", "application/json")
                .body(body)
                .post("/api/v1/courier")
                .then()
                .statusCode(statusCode);
    }
    @Step("Авторизация курьера с ошибкой {statusCode}")
    public ValidatableResponse loginCourierExpectingError(
            CourierLoginRequest body,
            int statusCode
    ) {
        return given()
                .header("Content-type", "application/json")
                .body(body)
                .post("/api/v1/courier/login")
                .then()
                .statusCode(statusCode);
    }
}
