package tests;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;

public class GetOrdersListTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    @DisplayName("Получить список заказов — успешный ответ")
    public void getOrdersListSuccessTest() {
        ValidatableResponse response = getOrdersList();

        response.statusCode(200)
                .body("orders", notNullValue())
                .body("orders", instanceOf(List.class));
    }

    @Test
    @DisplayName("Структура заказа содержит обязательные поля")
    public void orderHasRequiredFieldsTest() {
        List<Map<String, Object>> orders = getOrdersList()
                .extract()
                .jsonPath()
                .getList("orders");

        if (!orders.isEmpty()) {
            Map<String, Object> order = orders.get(0);

            assertTrue(order.containsKey("id"));
            assertTrue(order.containsKey("firstName"));
            assertTrue(order.containsKey("lastName"));
            assertTrue(order.containsKey("address"));
            assertTrue(order.containsKey("phone"));
            assertTrue(order.containsKey("track"));
        }
    }

    // ===== STEPS =====

    @Step("Получить список заказов")
    public ValidatableResponse getOrdersList() {
        return given()
                .when()
                .get("/api/v1/orders")
                .then();
    }

}
