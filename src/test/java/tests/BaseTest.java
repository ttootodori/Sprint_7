package tests;

import io.restassured.RestAssured;
import org.junit.BeforeClass;

public abstract class BaseTest {

    @BeforeClass
    public static void setUpBase() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }
}
