package ru.yandex.praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.yandex.praktikum.api.client.CourierClient;
import ru.yandex.praktikum.model.LoginCourier;
import static org.hamcrest.CoreMatchers.equalTo;

@Epic("API Tests")
@Feature("Courier")
@RunWith(JUnitParamsRunner.class)
public class LoginCourierApiTest extends BaseTest {

    private CourierClient courierClient;

    @Test
    @DisplayName("Логин курьера. Успешный запрос")
    public void loginCourierOkTest() {
        courierClient = new CourierClient();
        courierClient.createCourier();
        Response loginResponse = courierClient.loginCourier();
        int courierID = loginResponse.then().extract().jsonPath().getInt("id");
        loginResponse.then().body("id",equalTo(courierID));
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
        courierClient = new CourierClient();
        courierClient.createCourier();
        Response loginResponse = courierClient.loginCourier(notExistingCourier);
        loginResponse.then().statusCode(404).body("message",equalTo("Учетная запись не найдена"));
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
        courierClient = new CourierClient();
        courierClient.createCourier();
        Response loginResponse = courierClient.loginCourier(invalidCourier);
        loginResponse.then().statusCode(400).body("message",equalTo("Недостаточно данных для входа"));
    }

    @After
    @Step("Удаление тестового курьера")
    public void tearDown() {
        //удаление созданного курьера
        Response deleteResponse = courierClient.deleteCourier();
        deleteResponse.then().statusCode(200);
    }
}
