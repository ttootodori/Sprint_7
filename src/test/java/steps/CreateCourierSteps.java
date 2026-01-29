package steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class CreateCourierSteps {

    @Step("Сформировать тело запроса для создания курьера")
    public String courierBody(String login, String password, String firstName) {
        return String.format("{\"login\":\"%s\",\"password\":\"%s\",\"firstName\":\"%s\"}",
                login, password, firstName);
    }

    @Step("Отправить запрос на создание курьера и проверить статус {statusCode}")
    public ValidatableResponse sendCreateCourierRequest(String body, int statusCode) {
        Response response = given()
                .header("Content-type", "application/json")
                .body(body)
                .when()
                .post("/api/v1/courier");

        return response.then().statusCode(statusCode);
    }

    @Step("Проверить тело ответа при успешном создании курьера")
    public void verifySuccessResponse(ValidatableResponse response) {
        response.body("ok", org.hamcrest.Matchers.equalTo(true));
    }

    @Step("Проверить сообщение об ошибке при создании курьера")
    public void verifyErrorMessage(ValidatableResponse response, String expectedMessage) {
        response.body("message", org.hamcrest.Matchers.equalTo(expectedMessage));
    }
}