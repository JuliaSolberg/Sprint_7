package ru.yandex.praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.yandex.praktikum.model.Courier;
import ru.yandex.praktikum.model.LoginCourier;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@Epic("API Tests")
@Feature("Courier")
@RunWith(JUnitParamsRunner.class)
public class CreateCourierApiTest {

    private LoginCourier loginCourier;
    private LoginCourier duplicateLogin;
    private Courier courier;
    private boolean hasToCleanUp;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Step("Создание тестового курьера")
    public Response returnResponseToCreateCourier() {
        courier = new Courier("AmyMyAmy","1234","Winehouse");
        loginCourier = LoginCourier.fromCourier(courier);
        System.out.println("Логин: "+loginCourier.toString());
        return given()/*.log().all()*/
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier");
    }
    @Step("Создание тестового курьера с уже существующим логином")
    public Response returnResponseToCreateCourier(Courier courier) {
        Courier duplicateCourier = new Courier(courier.getLogin(),"11111","");
        duplicateLogin = LoginCourier.fromCourier(duplicateCourier);
        System.out.println("Дупликат-логин: "+duplicateLogin.toString());
        return given()/*.log().all()*/
                .header("Content-type", "application/json")
                .body(duplicateCourier)
                .post("/api/v1/courier");
    }
    @Test
    @DisplayName("Создание курьера. Успешный запрос")
    public void createNewCourierOkCreated() {
        hasToCleanUp = true;
        // создание нового курьера
        System.out.println("Создаем тестового курьера");
        Response createResponse = returnResponseToCreateCourier();
        createResponse.then().statusCode(201).body("ok",equalTo(true));
        System.out.println("Новый курьер создан: "+createResponse.body().asString());
    }

    @Test
    @DisplayName("Создание курьера. Запрос на создание курьера с уже существующим логином")
    public void createDuplicateCourierNotCreatedConflict() {
        hasToCleanUp = true;
        // создание нового курьера
        System.out.println("Создаем тестового курьера");
        Response createResponse = returnResponseToCreateCourier();
        createResponse.then().statusCode(201).body("ok",equalTo(true));
        System.out.println("Новый курьер создан: "+createResponse.body().asString());


        Response duplicateCreateResponse = returnResponseToCreateCourier(courier);
        duplicateCreateResponse.then().statusCode(409).body("message",equalTo("Этот логин уже используется"));
        System.out.println("Пытаемся создать дупликат: "+duplicateCreateResponse.body().asString());

        //проверка что дупликат не был создан
        Response checkResponse = given()
                .header("Content-type", "application/json")
                .body(duplicateLogin)
                .post("/api/v1/courier/login");
        checkResponse.then().statusCode(404).body("message",equalTo("Учетная запись не найдена"));
        System.out.println("Проверяем, что дупликата нет " + checkResponse.body().asString());
    }

    @Step("Подготовка данных для создания тестового курьера без логина или пароля")
    public static Object[][] invalidCourierData() {
        return new Object[][] {
                {"Valusha",""},
                {"","Valusha"},
        };
    }
    @Test
    @Parameters(method = "invalidCourierData")
    @Description("Создание курьера. Запрос без логина или пароля")
    public void createNewCourierWithoutLoginOrPasswordBadRequest(String paramLogin, String paramPassword) {
        hasToCleanUp = false;
        Courier invalidCourier = new Courier(paramLogin,paramPassword,"Test_Data");
        Response response = given()
                .header("Content-type", "application/json")
                .body(invalidCourier)
                .post("/api/v1/courier");
        response.then().statusCode(400).body("message",equalTo("Недостаточно данных для создания учетной записи"));
    }

    @After
    @Step("Удаление тестового курьера")
    public void tearDown() {
        if (hasToCleanUp) {
        //авторизация только что созданного курьера для получения ID
        Response loginResponse = given()
                .header("Content-type", "application/json")
                .body(loginCourier)
                .post("/api/v1/courier/login");
        loginResponse.then().statusCode(200).body("id",notNullValue());

        //получение ID
        int courierID = loginResponse.jsonPath().getInt("id");
        System.out.println("Courier ID: " + courierID);

        //удаление созданного курьера
        Response deleteResponse = given()
                .header("Content-type", "application/json")
                .delete("/api/v1/courier/" + courierID);
        deleteResponse.then().statusCode(200).body("ok",equalTo(true));
        System.out.println("Тестовые данные успешно удалены");}
        else {
            System.out.println("Тестовый курьер не создавался, поэтому не нужно ничего удалять");
        }
    }
}
