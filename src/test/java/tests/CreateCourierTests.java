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

public class CreateCourierTests extends BaseTest {

    private CourierSteps courierSteps;

    private String lastCreatedCourierLogin;
    private String lastCreatedCourierPassword;
    private Integer lastCreatedCourierId;

    @Before
    public void setUp() {
        courierSteps = new CourierSteps();
    }

    @After
    public void tearDown() {
        if (lastCreatedCourierId != null) {
            courierSteps.deleteCourier(lastCreatedCourierId);
        }
    }

    // ===================== TESTS =====================

    @Test
    @DisplayName("Курьера можно создать - успешный запрос возвращает ok: true")
    public void courierCanBeCreatedTest() {
        CourierCreateRequest body = createCourierBody();

        courierSteps.createCourier(body);

        ValidatableResponse loginResponse =
                courierSteps.loginCourier(
                        new CourierLoginRequest(
                                lastCreatedCourierLogin,
                                lastCreatedCourierPassword
                        )
                );

        lastCreatedCourierId = loginResponse.extract().path("id");
        loginResponse.body("id", notNullValue());
    }

    @Test
    @DisplayName("Нельзя создать двух курьеров с одинаковым логином")
    public void cannotCreateTwoCouriersWithSameLoginTest() {
        CourierCreateRequest body = createCourierBody();

        courierSteps.createCourier(body);

        ValidatableResponse response =
                courierSteps.createCourierExpectingError(body, 409);

        response.body("message", notNullValue());
    }

    @Test
    @DisplayName("Нельзя создать курьера без логина")
    public void cannotCreateCourierWithoutLoginTest() {
        CourierCreateRequest body =
                new CourierCreateRequest(null, "test_pass", "Test");

        ValidatableResponse response =
                courierSteps.createCourierExpectingError(body, 400);

        response.body("message", notNullValue());
    }

    @Test
    @DisplayName("Нельзя создать курьера без пароля")
    public void cannotCreateCourierWithoutPasswordTest() {
        CourierCreateRequest body =
                new CourierCreateRequest("test_login", null, "Test");

        ValidatableResponse response =
                courierSteps.createCourierExpectingError(body, 400);

        response.body("message", notNullValue());
    }

    @Test
    @DisplayName("Можно создать курьера без имени")
    public void canCreateCourierWithoutFirstNameTest() {
        CourierCreateRequest body =
                new CourierCreateRequest(
                        uniqueLogin(),
                        generatePassword(),
                        null
                );

        courierSteps.createCourier(body);

        ValidatableResponse loginResponse =
                courierSteps.loginCourier(
                        new CourierLoginRequest(
                                body.getLogin(),
                                body.getPassword()
                        )
                );

        lastCreatedCourierId = loginResponse.extract().path("id");
        loginResponse.body("id", notNullValue());
    }

    // ===================== HELPERS =====================

    private CourierCreateRequest createCourierBody() {
        lastCreatedCourierLogin = uniqueLogin();
        lastCreatedCourierPassword = generatePassword();

        return new CourierCreateRequest(
                lastCreatedCourierLogin,
                lastCreatedCourierPassword,
                "Test"
        );
    }

    private String uniqueLogin() {
        return "courier_" + System.currentTimeMillis();
    }

    private String generatePassword() {
        return "pass_" + System.currentTimeMillis();
    }
}
