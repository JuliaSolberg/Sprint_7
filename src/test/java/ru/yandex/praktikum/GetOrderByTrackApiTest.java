package ru.yandex.praktikum;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.model.Order;
import ru.yandex.praktikum.model.OrderResponse;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("API Tests")
@Feature("Orders")
public class GetOrderByTrackApiTest {
    private int orderID;
    private OrderResponse orderResponse;

    @Before
    @Step("Создание тестового заказа и получение его номера")
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";

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
    @DisplayName("Получение заказа по его номеру. Успешный запрос")
    public void getOrderByTrackOk() {
        ValidatableResponse getOrderResponse = given()
                .header("Content-type", "application/json")
                .param("t",orderID)
                .get("/api/v1/orders/track")
                .then()
                .statusCode(200)
                .body(not(empty()));

        String responseBody = getOrderResponse.extract().body().asString();
        System.out.println("Response body: " + responseBody);

        orderResponse = getOrderResponse.extract().as(OrderResponse.class);
        Order order = orderResponse.getOrder();
        System.out.println("Deserialized Order: " + order);
    }

    @Test
    @DisplayName("Получение заказа по его номеру. Запрос без номера заказа")
    public void getOrderNoTrackBadRequest() {
        ValidatableResponse getOrderResponse = given()
                .header("Content-type", "application/json")
                .get("/api/v1/orders/track")
                .then()
                .statusCode(400);

        getOrderResponse.assertThat().body("message",equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Получение заказа по его номеру. Запрос с несуществующим номером заказа")
    public void getOrderInvalidTrackNotFound() {
        ValidatableResponse getOrderResponse = given()
                .header("Content-type", "application/json")
                .param("t",777777777)
                .get("/api/v1/orders/track")
                .then()
                .statusCode(404);

        getOrderResponse.assertThat().body("message",equalTo("Заказ не найден"));
    }

    @After
    @Step("Удаление тестового заказа")
    public void tearDown() {
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
