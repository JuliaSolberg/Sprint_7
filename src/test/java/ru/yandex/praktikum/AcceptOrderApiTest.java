package ru.yandex.praktikum;

import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.model.Courier;
import ru.yandex.praktikum.model.LoginCourier;
import ru.yandex.praktikum.model.Order;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@Epic("API Tests")
@Feature("Orders")
public class AcceptOrderApiTest {

    private Courier courier;
    private LoginCourier loginCourier;
    private int courierID;
    private int orderID;

    @Before
    @Step("Создание тестового курьера и тестового заказа")
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";

        // создание тестового курьера и получение его id
        courier = new Courier("AmyMyAmy","1234","Winehouse");
        given()/*.log().all()*/
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier");
        loginCourier = LoginCourier.fromCourier(courier);

        ValidatableResponse loginResponse = given()
                .header("Content-type", "application/json")
                .body(loginCourier)
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200);
        courierID = loginResponse.extract().jsonPath().getInt("id");
        System.out.println("Courier ID: " + courierID);

        // создание тестового заказа и получение его id
        Order order = new Order("Coco","Chanel","Moscow, Gorky Park","Сокольники","+7 800 355 35 35",1,"2025-06-06","Beautiful minds",new String[]{});
        ValidatableResponse orderResponse = given()
                .header("Content-type", "application/json")
                .body(order)
                .post("/api/v1/orders")
                .then()
                .statusCode(201);
        orderID = orderResponse.extract().jsonPath().getInt("track");
        System.out.println("Order ID: " + orderID);
    }

    @Test
    @DisplayName("Принять заказ. Успешный запрос")
    public void acceptOrderOk() {
        ValidatableResponse acceptOrderResponse = given().log().all()
                .header("Content-type", "application/json")
                .param("courierId",courierID)
                .put("/api/v1/orders/accept/"+orderID)
                .then().log().all()
                .statusCode(200);
        acceptOrderResponse.assertThat().body("ok",equalTo(true));
    }

    @Test
    @DisplayName("Принять заказ. Запрос без ID курьера")
    public void acceptOrderWithoutCourierIdConflict() {
        ValidatableResponse acceptOrderResponse = given().log().all()
                .header("Content-type", "application/json")
                .put("/api/v1/orders/accept/"+orderID)
                .then().log().all()
                .statusCode(400);
        acceptOrderResponse.assertThat().body("message",equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Принять заказ. Запрос без номера заказа")
    public void acceptOrderWithoutOrderIdBadRequest() {
        ValidatableResponse acceptOrderResponse = given().log().all()
                .header("Content-type", "application/json")
                .param("courierId",courierID)
                .put("/api/v1/orders/accept/")
                .then().log().all()
                .statusCode(400);
        acceptOrderResponse.assertThat().body("message",equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Принять заказ. Запрос с несуществующим ID курьера")
    public void acceptOrderInvalidCourierNotFound() {
        ValidatableResponse acceptOrderResponse = given().log().all()
                .header("Content-type", "application/json")
                .param("courierId",8888888)
                .put("/api/v1/orders/accept/"+orderID)
                .then().log().all()
                .statusCode(404);
        acceptOrderResponse.assertThat().body("message",equalTo("Курьера с таким id не существует"));
    }

    @Test
    @DisplayName("Принять заказ. Запрос с несуществующим номером заказа")
    public void acceptOrderInvalidOrderNotFound() {
        ValidatableResponse acceptOrderResponse = given().log().all()
                .header("Content-type", "application/json")
                .param("courierId",courierID)
                .put("/api/v1/orders/accept/"+8888888)
                .then().log().all()
                .statusCode(404);
        acceptOrderResponse.assertThat().body("message",equalTo("Заказа с таким id не существует"));
    }

    @After
    @Step("Удаление тестового курьера и тестового заказа")
    public void tearDown() {
        // удаление курьера
        given()
                .header("Content-type", "application/json")
                .delete("/api/v1/courier/" + courierID)
                .then().statusCode(200);
        System.out.println("Тестовый курьер удален");
        //удаление заказа
        given()
                .header("Content-type", "application/json")
                .param("track",orderID)
                .put("/api/v1/orders/cancel")
                .then()
                .statusCode(200);
        System.out.println("Тестовый заказ удален");
    }
}
