package tests;

import model.CourierCreateRequest;
import model.CourierLoginRequest;
import steps.CourierSteps;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.notNullValue;

public class LoginCourierTest extends BaseTest {

    private CourierSteps courierSteps;

    private String createdCourierLogin;
    private String createdCourierPassword;
    private Integer createdCourierId;

    @Before
    public void setUp() {
        courierSteps = new CourierSteps();

        createdCourierLogin = generateUniqueLogin();
        createdCourierPassword = generatePassword();

        courierSteps.createCourier(
                new CourierCreateRequest(
                        createdCourierLogin,
                        createdCourierPassword,
                        "Test"
                )
        );
    }

    @After
    public void tearDown() {
        if (createdCourierId != null) {
            courierSteps.deleteCourier(createdCourierId);
        }
    }

    // ================ TESTS ================

    @Test
    @DisplayName("Курьер может авторизоваться - успешный логин возвращает id")
    public void courierCanLoginTest() {
        CourierLoginRequest body =
                new CourierLoginRequest(
                        createdCourierLogin,
                        createdCourierPassword
                );

        ValidatableResponse response = courierSteps.loginCourier(body);

        createdCourierId = response.extract().path("id");

        response.statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Нельзя авторизоваться без логина - возвращается ошибка 400")
    public void cannotLoginWithoutLoginTest() {
        CourierLoginRequest body =
                new CourierLoginRequest(null, createdCourierPassword);

        courierSteps.loginCourierExpectingError(body, 400)
                .body("message", notNullValue());
    }

    @Test
    @DisplayName("Нельзя авторизоваться без пароля - возвращается ошибка 400")
    public void cannotLoginWithoutPasswordTest() {
        CourierLoginRequest body =
                new CourierLoginRequest(createdCourierLogin, null);

        courierSteps.loginCourierExpectingError(body, 400)
                .body("message", notNullValue());
    }

    @Test
    @DisplayName("Ошибка при несуществующей паре логин-пароль - возвращается ошибка 404")
    public void cannotLoginWithWrongCredentialsTest() {
        CourierLoginRequest body =
                new CourierLoginRequest(
                        "non_existent_" + System.currentTimeMillis(),
                        generatePassword()
                );

        courierSteps.loginCourierExpectingError(body, 404)
                .body("message", notNullValue());
    }

    // ================ HELPERS ================

    private String generateUniqueLogin() {
        return "courier_" + System.currentTimeMillis();
    }

    private String generatePassword() {
        return "pass_" + System.currentTimeMillis();
    }
}
