package steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import model.OrderCreateRequest;

import static io.restassured.RestAssured.given;

public class OrderSteps {

    @Step("Создать заказ")
    public ValidatableResponse createOrder(OrderCreateRequest body) {
        return given()
                .header("Content-type", "application/json")
                .body(body)
                .post("/api/v1/orders")
                .then();
    }

    @Step("Получить список заказов")
    public ValidatableResponse getOrdersList() {
        return given()
                .get("/api/v1/orders")
                .then();
    }

    @Step("Отменить заказ")
    public void cancelOrder(Integer track) {
        given()
                .put("/api/v1/orders/cancel?track=" + track)
                .then()
                .statusCode(200);
    }
}
