package ru.yandex.praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.yandex.praktikum.api.client.OrdersClient;
import ru.yandex.praktikum.model.Order;
import static org.hamcrest.CoreMatchers.equalTo;

@Epic("API Tests")
@Feature("Orders")
@RunWith(JUnitParamsRunner.class)
public class CreateOrderApiTest extends BaseTest{
    private Order order;
    private int trackOrder;
    private OrdersClient orderClient;

    @Step("Подготовка данных для создания заказа")
    public static Object[][] createOrderData() {
        return new Object[][] {
                {"Coco","Chanel","Moscow, Gorky Park","Сокольники","+7 800 355 35 35",1,"2025-06-06","Beautiful minds", new String[]{"BLACK"}},
                {"Coco","Chanel","Moscow, Gorky Park","Сокольники","+7 800 355 35 35",1,"2025-06-06","Beautiful minds", new String[]{"GREY"}},
                {"Coco","Chanel","Moscow, Gorky Park","Сокольники","+7 800 355 35 35",1,"2025-06-06","Beautiful minds",new String[]{"BLACK","GREY"}},
                {"Coco","Chanel","Moscow, Gorky Park","Сокольники","+7 800 355 35 35",1,"2025-06-06","Beautiful minds",new String[]{}},
        };
    }

    @Test
    @Description("Создание заказа с разными опциями для выбора цвета самоката")
    @Parameters(method = "createOrderData")
    public void createOrderScooterColorOptionsCreated(String firstName, String lastName,
                String address, String metroStation, String phone, int rentTime,
                String deliveryDate, String comment, String[] color) {
        order = new Order(firstName,lastName,address,metroStation,phone,rentTime,deliveryDate,comment,color);

        orderClient = new OrdersClient();
        Response createOrderResponse = orderClient.createOrder(order);
        trackOrder = orderClient.getOrderTrack(createOrderResponse);
        createOrderResponse.then().body("track",equalTo(trackOrder));
    }

    @After
    @Step("Удаление тестового заказа")
    public void tearDown() {
        orderClient.cancelOrder(trackOrder);
    }
}
