package tests;

import steps.OrderSteps;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import org.assertj.core.api.SoftAssertions;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assume.assumeThat;

public class GetOrdersListTest extends BaseTest {

    private OrderSteps orderSteps;

    @Before
    public void setUp() {
        orderSteps = new OrderSteps();
    }

    @Test
    @DisplayName("Получить список заказов — успешный ответ")
    public void getOrdersListSuccessTest() {
        ValidatableResponse response = orderSteps.getOrdersList();

        response.statusCode(200)
                .body("orders", notNullValue())
                .body("orders", instanceOf(List.class));
    }

    @Test
    @DisplayName("Структура заказа содержит обязательные поля")
    public void orderHasRequiredFieldsTest() {
        List<Map<String, Object>> orders = orderSteps.getOrdersList()
                .extract()
                .jsonPath()
                .getList("orders");

        assumeThat("Список заказов не пустой", orders, not(empty()));

        Map<String, Object> order = orders.get(0); // ← ВОТ ЭТОГО НЕ ХВАТАЛО

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(order.keySet())
                .as("Проверка обязательных полей заказа")
                .contains(
                        "id",
                        "firstName",
                        "lastName",
                        "address",
                        "phone",
                        "track"
                );

        softly.assertAll();
    }

}