package ru.yandex.praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.yandex.praktikum.model.Order;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@Epic("API Tests")
@Feature("Orders")
@RunWith(JUnitParamsRunner.class)
public class CreateOrderApiTest {
    private Order order;
    private int trackOrder;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

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
        ValidatableResponse createOrderResponse = given().log().all()
                .header("Content-type", "application/json")
                .body(order)
                .post("/api/v1/orders")
                .then()
                .statusCode(201);
        trackOrder = createOrderResponse.extract().jsonPath().getInt("track");
        System.out.println("Номер заказа: "+trackOrder);
        createOrderResponse.assertThat().body("track",equalTo(trackOrder));
    }

    @After
    @Step("Удаление тестового заказа")
    public void tearDown() {
        ValidatableResponse deleteOrderResponse = given()
                .header("Content-type", "application/json")
                .param("track",trackOrder)
                .put("/api/v1/orders/cancel")
                .then()
                .statusCode(200);
    }
}
