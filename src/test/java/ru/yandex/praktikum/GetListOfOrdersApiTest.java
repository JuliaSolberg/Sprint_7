package ru.yandex.praktikum;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import ru.yandex.praktikum.model.ListOfOrders;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;

@Epic("API Tests")
@Feature("Orders")
public class GetListOfOrdersApiTest extends BaseTest {

    @Test
    @DisplayName("Получение списка заказов. Запрос на 10 доступных заказов возле метро Калужская")
    public void getListOfOrdersOk() {
        // формирование коллекции параметров для передачи в запрос
        Map<String,Object> params = new HashMap<>();
        params.put("climit",10);
        params.put("page",0);
        params.put("nearestStation",new String[]{"110"});

        ValidatableResponse listOfOrdersResponse = given()
                .header("Content-type", "application/json")
                .params(params)
                .get("/api/v1/orders")
                .then()
                .statusCode(200)
                .body("orders", not(empty()));

        ListOfOrders listOfOrders = listOfOrdersResponse.extract().as(ListOfOrders.class);

        System.out.println("Первый заказ из списка: " + listOfOrders.getOrders().get(0));
    }

}
