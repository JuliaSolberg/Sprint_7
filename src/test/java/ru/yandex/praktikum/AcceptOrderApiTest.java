package ru.yandex.praktikum;

import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.api.client.CourierClient;
import ru.yandex.praktikum.api.client.OrdersClient;
import ru.yandex.praktikum.model.Courier;
import ru.yandex.praktikum.model.LoginCourier;

import static org.hamcrest.CoreMatchers.equalTo;

@Epic("API Tests")
@Feature("Orders")
public class AcceptOrderApiTest extends BaseTest {

    private CourierClient courierClient;
    private OrdersClient ordersClient;

    private Courier courier;
    private LoginCourier loginCourier;
    private int courierID;
    private int orderID;

    @Before
    @Step("Создание тестового курьера и тестового заказа")
    public void prepare() {
        ordersClient = new OrdersClient();
        Response createOrderResponse = ordersClient.createOrder();
        orderID = ordersClient.getOrderID(createOrderResponse);

        courierClient = new CourierClient();
        courierClient.createCourier();
        Response loginCourierResponse = courierClient.loginCourier();
        courierID = loginCourierResponse.jsonPath().getInt("id");
    }

    @Test
    @DisplayName("Принять заказ. Успешный запрос")
    public void acceptOrderOk() {
        Response acceptOrderResponse = ordersClient.acceptOrder(courierID,orderID);
        acceptOrderResponse.then().statusCode(200).body("ok",equalTo(true));
    }

    @Test
    @DisplayName("Принять заказ. Запрос без ID курьера")
    public void acceptOrderWithoutCourierIdConflict() {
        Response acceptOrderResponse = ordersClient.acceptOrder("orderID",orderID);
        acceptOrderResponse.then().statusCode(400).body("message",equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Принять заказ. Запрос без номера заказа")
    public void acceptOrderWithoutOrderIdBadRequest() {
        Response acceptOrderResponse = ordersClient.acceptOrder("courierID",courierID);
        acceptOrderResponse.then().statusCode(400).body("message",equalTo("Недостаточно данных для поиска"));
       }

    @Test
    @DisplayName("Принять заказ. Запрос с несуществующим ID курьера")
    public void acceptOrderInvalidCourierNotFound() {
        Response acceptOrderResponse = ordersClient.acceptOrder(8888888,orderID);
        acceptOrderResponse.then().statusCode(404).body("message",equalTo("Курьера с таким id не существует"));
    }

    @Test
    @DisplayName("Принять заказ. Запрос с несуществующим номером заказа")
    public void acceptOrderInvalidOrderNotFound() {
        Response acceptOrderResponse = ordersClient.acceptOrder(courierID,8888888);
        acceptOrderResponse.then().statusCode(404).body("message",equalTo("Заказа с таким id не существует"));
    }

    @After
    @Step("Удаление тестового курьера")
    public void tearDown() {
        //удаление созданного курьера
        Response deleteResponse = courierClient.deleteCourier();
        deleteResponse.then().statusCode(200);
    }
}
