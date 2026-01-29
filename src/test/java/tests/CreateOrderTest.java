package tests;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest {

    private Integer createdOrderTrack;
    private final List<String> colors;

    public CreateOrderTest(List<String> colors) {
        this.colors = colors;
    }

    @Parameterized.Parameters(name = "Colors: {0}")
    public static Object[][] getTestData() {
        return new Object[][] {
                {List.of("BLACK")},           // Один цвет BLACK
                {List.of("GREY")},            // Один цвет GREY
                {Arrays.asList("BLACK", "GREY")},   // Оба цвета
                {null}                              // Без цвета
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @After
    public void tearDown() {
        if (createdOrderTrack != null) {
            cancelOrder(createdOrderTrack);
        }
    }

    @Test
    @DisplayName("Создание заказа с разными цветами")
    public void createOrderWithDifferentColorsTest() {
        String body = createOrderBody(colors);

        ValidatableResponse response = createOrder(body);
        createdOrderTrack = response.extract().path("track");

        response.statusCode(201)
                .body("track", notNullValue());
    }

    // ================ STEPS ================

    @Step("Сформировать тело запроса с цветами: {colors}")
    private String createOrderBody(List<String> colors) {
        StringBuilder body = new StringBuilder();
        String deliveryDate = LocalDate.now().plusDays(3).toString();

        body.append("{")
                .append("\"firstName\":\"Тест\",")
                .append("\"lastName\":\"Тестов\",")
                .append("\"address\":\"ул. Тестовая, 1\",")
                .append("\"metroStation\":\"1\",")
                .append("\"phone\":\"+79999999999\",")
                .append("\"rentTime\":3,")
                .append("\"deliveryDate\":\"").append(deliveryDate).append("\",")
                .append("\"comment\":\"Тестовый заказ\"");

        if (colors != null && !colors.isEmpty()) {
            body.append(", \"color\": [");
            for (int i = 0; i < colors.size(); i++) {
                body.append("\"").append(colors.get(i)).append("\"");
                if (i < colors.size() - 1) {
                    body.append(", ");
                }
            }
            body.append("]");
        }

        body.append("}");
        return body.toString();
    }

    @Step("Создать заказ")
    public ValidatableResponse createOrder(String body) {
        return given()
                .header("Content-type", "application/json")
                .body(body)
                .post("/api/v1/orders")
                .then();
    }

    @Step("Отменить заказ с track: {track}")
    private void cancelOrder(Integer track) {
        try {
            given()
                    .put("/api/v1/orders/cancel?track=" + track)
                    .then()
                    .statusCode(200);
        } catch (Exception e) {
            // Игнорируем ошибки (заказ может быть уже отменен)
        }
    }
}