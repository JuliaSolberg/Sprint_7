package ru.yandex.praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.yandex.praktikum.model.Courier;
import ru.yandex.praktikum.model.LoginCourier;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@Epic("API Tests")
@Feature("Courier")
@RunWith(JUnitParamsRunner.class)
public class LoginCourierApiTest {

    private Courier courier;
    private LoginCourier loginCourier;
    private ValidatableResponse response;
    private int courierID;

    @Before
    @Step("Создание тестового курьера")
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        courier = new Courier("AmyMyAmy","1234","Winehouse");
        loginCourier = LoginCourier.fromCourier(courier);
        response = given()/*.log().all()*/
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier")
                .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("Логин курьера. Успешный запрос")
    public void loginCourierOkTest() {
        ValidatableResponse loginResponse = given()
                .header("Content-type", "application/json")
                .body(loginCourier)
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200);
        courierID = loginResponse.extract().jsonPath().getInt("id");
        loginResponse.assertThat().body("id",equalTo(courierID));
    }

    @Step("Подготовка данных: несущесвующая пара логин/пароль")
    public static Object[][] notExistingCourierData() {
        return new Object[][] {
                {"AmyMyAmy", "AmyMyAmy"},
                {"1234","1234"},
        };
    }
    @Test
    @Parameters(method = "notExistingCourierData")
    @Description("Логин курьера. Запрос с несуществующей парой логин/пароль")
    public void loginNotExistingCourierNotFound(String login, String password) {
        LoginCourier notExistingCourier = new LoginCourier(login,password);
        ValidatableResponse loginResponse = given()
                .header("Content-type","application/json")
                .body(notExistingCourier)
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404);
        loginResponse.assertThat().body("message",equalTo("Учетная запись не найдена"));
    }

    @Step("Подготовка данных: отсутствующий логин или пароль")
    public static Object[][] invalidLoginCourierData() {
        return new Object[][] {
                {"AmyMyAmy",""},
                {"","1234"},
        };
    }

    @Test
    @Parameters(method = "invalidLoginCourierData")
    @Description("Логин курьера. Запрос без логина или пароля")
    public void loginInvalidCourierBadRequest(String login, String password) {
        LoginCourier invalidCourier = new LoginCourier(login,password);
        ValidatableResponse loginResponse = given()
                .header("Content-type","application/json")
                .body(invalidCourier)
                .post("/api/v1/courier/login")
                .then()
                .statusCode(400);
        loginResponse.assertThat().body("message",equalTo("Недостаточно данных для входа"));
    }

    @After
    @Step("Удаление тестового курьера")
    public void tearDown() {
        ValidatableResponse loginResponse = given()
                .header("Content-type", "application/json")
                .body(loginCourier)
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200);

        courierID = loginResponse.extract().jsonPath().getInt("id");
        loginResponse.assertThat().body("id",equalTo(courierID));

        ValidatableResponse deleteResponse = given()
                .header("Content-type", "application/json")
                .delete("/api/v1/courier/" + courierID)
                .then()
                .statusCode(200);
        System.out.println("Тестовые данные успешно удалены");
    }
}
