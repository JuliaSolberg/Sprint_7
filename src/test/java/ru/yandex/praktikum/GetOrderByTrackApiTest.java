package ru.yandex.praktikum;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.api.client.CourierClient;
import ru.yandex.praktikum.api.client.OrdersClient;
import ru.yandex.praktikum.model.Order;
import ru.yandex.praktikum.model.OrderResponse;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("API Tests")
@Feature("Orders")
public class GetOrderByTrackApiTest extends BaseTest {

    private OrdersClient ordersClient;
    private int orderTrack;
    private OrderResponse orderResponse;

    @Before
    @Step("Создание тестового заказа и получение его номера")
    public void prepare() {
        // создание тестового заказа и получение его track
        ordersClient = new OrdersClient();
        Response createOrderResponse = ordersClient.createOrder();
        orderTrack = ordersClient.getOrderTrack(createOrderResponse);
    }

    @Test
    @DisplayName("Получение заказа по его номеру. Успешный запрос")
    public void getOrderByTrackOk() {
        Response getOrderResponse = ordersClient.getOrderByTrack(orderTrack);
        getOrderResponse.then().statusCode(200).body(not(empty()));

        String responseBody = getOrderResponse.body().asString();
        System.out.println("Response body: " + responseBody);

        orderResponse = getOrderResponse.as(OrderResponse.class);
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
        Response getOrderResponse = ordersClient.getOrderByTrack(777777777);
        getOrderResponse.then().statusCode(404).body("message",equalTo("Заказ не найден"));
    }

    @After
    @Step("Удаление тестового заказа")
    public void tearDown() {
        //отмена заказа
        ordersClient.cancelOrder(orderTrack);
        System.out.println("Тестовый заказ удален");
    }
}
