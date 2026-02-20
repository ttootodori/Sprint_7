package tests;

import model.OrderCreateRequest;
import steps.OrderSteps;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest extends BaseTest {

    private OrderSteps orderSteps;

    private Integer createdOrderTrack;
    private final List<String> colors;

    public CreateOrderTest(List<String> colors) {
        this.colors = colors;
    }

    @Parameterized.Parameters(name = "Colors: {0}")
    public static Object[][] getTestData() {
        return new Object[][]{
                {List.of("BLACK")},
                {List.of("GREY")},
                {Arrays.asList("BLACK", "GREY")},
                {null}
        };
    }

    @Before
    public void setUp() {
        orderSteps = new OrderSteps();
    }

    @After
    public void tearDown() {
        if (createdOrderTrack != null) {
            orderSteps.cancelOrder(createdOrderTrack);
            createdOrderTrack = null;
        }
    }

    @Test
    @DisplayName("Создание заказа с указанием разных значений цвета")
    public void createOrderWithDifferentColorsTest() {
        OrderCreateRequest body = createOrderBody(colors);

        ValidatableResponse response = orderSteps.createOrder(body);
        createdOrderTrack = response.extract().path("track");

        response.statusCode(201)
                .body("track", notNullValue());
    }

    // ================ HELPERS ================

    private OrderCreateRequest createOrderBody(List<String> colors) {
        String deliveryDate = LocalDate.now().plusDays(3).toString();

        return new OrderCreateRequest(
                "Тест",
                "Тестов",
                "ул. Тестовая, 1",
                "1",
                "+79999999999",
                3,
                deliveryDate,
                "Тестовый заказ",
                colors
        );
    }
}
