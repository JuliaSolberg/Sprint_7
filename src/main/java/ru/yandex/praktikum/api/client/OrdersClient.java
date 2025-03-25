package ru.yandex.praktikum.api.client;

import com.google.gson.Gson;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.yandex.praktikum.model.Order;
import ru.yandex.praktikum.model.OrderResponse;

import static io.restassured.RestAssured.given;

// class for endpoint /api/vi/orders
public class OrdersClient {

    private static final Logger LOGGER = LogManager.getLogger(OrdersClient.class);

    private static final String ORDERS_API = "/api/v1/orders";
    private static final String FIRST_NAME = "Coco";
    private static final String LAST_NAME = "Chanel";
    private static final String ADDRESS = "Moscow, Gorky Park";
    private static final String METRO_STATION = "Сокольники";
    private static final String PHONE = "+7 800 355 35 35";
    private static final int RENT_TIME = 1;
    private static final String DELIVERY_DATE = "2025-06-06";
    private static final String COMMENT = "Beautiful minds";


    private Order order;
    private int orderID;
    private int orderTrack;

    public void prepareOrderData() {
        order = new Order(FIRST_NAME,LAST_NAME,ADDRESS,METRO_STATION,PHONE,RENT_TIME,DELIVERY_DATE,COMMENT,new String[]{});
        LOGGER.info("Подготовлены данные для заказа: {}", order);
    }

    public Response createOrder() {
        prepareOrderData();
        LOGGER.info("Отправка запроса на создание заказа...");
        Response response = given()
                .header("Content-type", "application/json")
                .body(order)
                .post(ORDERS_API);
        LOGGER.info("Ответ API при создании заказа: {}", response.body().asString());
        return response;
    }

    public Response createOrder(Order orderAsParam) {
        LOGGER.info("Отправка запроса на создание заказа с параметрами: {}", orderAsParam);
        Response response = given()
                .header("Content-type", "application/json")
                .body(orderAsParam)
                .post(ORDERS_API);
        LOGGER.info("Ответ API при создании заказа: {}", response.body().asString());
        return response;
    }

    public int getOrderID(Response orderResponse) {
        orderTrack = orderResponse.jsonPath().getInt("track");
        LOGGER.info("Получен трек-номер заказа: {}", orderTrack);
        Response getOrderResponse =  given()
                .header("Content-type", "application/json")
                .param("t",orderTrack)
                .get(ORDERS_API+"/track");
        getOrderResponse.then().log().all();
        LOGGER.info("Ответ API при получении заказа: {}", getOrderResponse.body().asString());

        Gson gson = new Gson();
        OrderResponse orderResponseAsObject = gson.fromJson(getOrderResponse.body().asString(), OrderResponse.class);
        Order orderFromJson = orderResponseAsObject.getOrder();
        LOGGER.info("Извлеченный объект заказа: {}", orderFromJson);

        return orderFromJson.getId();
    }

    public int getOrderTrack(Response orderResponse) {
        orderTrack = orderResponse.jsonPath().getInt("track");
        LOGGER.info("Получен трек-номер заказа: {}", orderTrack);
        return orderTrack;
    }

    public Response acceptOrder(int courierID, int orderID) {
        LOGGER.info("Попытка принять заказ {} курьером {}", orderID, courierID);
        Response response = given().log().all()
                .header("Content-type", "application/json")
                .param("courierId", courierID)
                .put(ORDERS_API + "/accept/" + orderID);
        LOGGER.info("Ответ API при принятии заказа: {}", response.body().asString());
        return response;
    }

    public Response acceptOrder(String idString, int idNumber) {
        LOGGER.info("Попытка принять заказ, параметр: {}, значение: {}", idString, idNumber);
        Response response;

        if (idString.equals("orderID")) {
            response = given().log().all()
                    .header("Content-type", "application/json")
                    .put(ORDERS_API + "/accept/" + idNumber);
        } else {
            response = given().log().all()
                    .header("Content-type", "application/json")
                    .param("courierId",idNumber)
                    .put(ORDERS_API + "/accept/");
        }
        LOGGER.info("Ответ API при принятии заказа: {}", response.body().asString());
        return response;
    }

    public Response getOrderByTrack(int track) {
        LOGGER.info("Получение информации о заказе по трек-номеру: {}", track);
        Response response = given()
                .header("Content-type", "application/json")
                .param("t",track)
                .get(ORDERS_API+"/track");
        LOGGER.info("Ответ API при запросе заказа: {}", response.body().asString());
        return response;
    }

    public void cancelOrder (int orderTrack) {
        LOGGER.info("Попытка отмены заказа с трек-номером: {}", orderTrack);
        given().log().all()
                .header("Content-type", "application/json")
                .param("track",orderTrack)
                .put(ORDERS_API+"/cancel")
                .then().log().all()
                .statusCode(200);
        LOGGER.info("Заказ успешно отменен.");
    }
}
