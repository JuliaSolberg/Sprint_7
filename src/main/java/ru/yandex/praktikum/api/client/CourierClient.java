package ru.yandex.praktikum.api.client;

import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.yandex.praktikum.model.Courier;
import ru.yandex.praktikum.model.LoginCourier;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

// class for endpoint /api/v1/courier
public class CourierClient {

    private static final Logger LOGGER = LogManager.getLogger(CourierClient.class);

    private static final String COURIER_API = "/api/v1/courier";
    private static final String LOGIN_COURIER_API = "/api/v1/courier/login";
    private static final String LOGIN_NAME = "AmyMyAmy";
    private static final String PASSWORD = "1234";
    private static final String FIRST_NAME = "Winehouse";

    private LoginCourier loginCourierData;
    private LoginCourier duplicateLogin;
    private Courier courier;

    public Response createCourierResponse(Courier courierSample) {
        LOGGER.info("Отправка запроса на создание курьера: {}", courierSample);
        return given()/*.log().all()*/
                .header("Content-type", "application/json")
                .body(courierSample)
                .post(COURIER_API);
    }

    public Response createCourier() {
        courier = new Courier(LOGIN_NAME,PASSWORD,FIRST_NAME);
        loginCourierData = LoginCourier.fromCourier(courier);
        LOGGER.info("Создан объект курьера: {}", loginCourierData);
        return createCourierResponse(courier);
    }

    public Response duplicateCourier() {
        Courier duplicateCourier = new Courier(LOGIN_NAME, "11111", "");
        duplicateLogin = LoginCourier.fromCourier(duplicateCourier);
        LOGGER.warn("Попытка создания дубликата курьера: {}", duplicateLogin);
        return createCourierResponse(duplicateCourier);
    }

    public Response checkDuplicateCourier() {
        LOGGER.info("Проверка, что дубликат не создан: {}", duplicateLogin);
        return loginCourier(duplicateLogin);
    }

    public Response loginCourier(LoginCourier loginCourierSample) {
        LOGGER.info("Попытка входа курьера: {}", loginCourierSample);
        return given()
                .header("Content-type", "application/json")
                .body(loginCourierSample)
                .post(LOGIN_COURIER_API);
    }

    public Response loginCourier() {
        LOGGER.info("Попытка входа курьера по данным: {}", loginCourierData);
        return given()
                .header("Content-type", "application/json")
                .body(loginCourierData)
                .post(LOGIN_COURIER_API);
    }

    public Response deleteCourierResponse(int courierID) {
        LOGGER.info("Удаление курьера с ID: {}", courierID);
        return given()
                .header("Content-type", "application/json")
                .delete(COURIER_API + "/" + courierID);
    }

    public Response invalidDeleteCourier() {
        LOGGER.error("Попытка удалить курьера без указания ID");
        return given()
                .header("Content-type", "application/json")
                .delete(COURIER_API + "/");
    }

    public Response deleteCourier() {
        LOGGER.info("Запрос на удаление курьера");

        //авторизация созданного курьера для получения ID
        Response loginCourierResponse = loginCourier();
        loginCourierResponse.then().statusCode(200).body("id",notNullValue());

        //получение ID
        int courierID = loginCourierResponse.jsonPath().getInt("id");
        System.out.println("Courier ID: " + courierID);

        return deleteCourierResponse(courierID);
    }
}
