package ru.yandex.praktikum;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import ru.yandex.praktikum.api.client.CourierClient;
import static org.hamcrest.CoreMatchers.equalTo;


@Epic("API Tests")
@Feature("Courier")
public class DeleteCourierApiTest extends BaseTest {

    private CourierClient courierClient;

    @Test
    @DisplayName("Удаление курьера. Запрос без ID курьера")
    public void deleteCourierWithoutIdBadRequest() {
        courierClient = new CourierClient();
        Response deleteResponse = courierClient.invalidDeleteCourier();
        deleteResponse.then().statusCode(400).body("message",equalTo("Недостаточно данных для удаления курьера"));
    }

    @Test
    @DisplayName("Удаление курьера. Запрос с несуществующим ID курьера")
    public void deleteCourierWrongIdNotFound() {
        courierClient = new CourierClient();
        Response deleteResponse = courierClient.deleteCourierResponse(8888);
        deleteResponse.then().statusCode(404).body("message",equalTo("Курьера с таким id нет"));
    }

    @Test
    @DisplayName("Удаление курьера. Успешный запрос")
    public void deleteCourierOk() {
        courierClient = new CourierClient();
        courierClient.createCourier();
        Response deleteResponse = courierClient.deleteCourier();
        deleteResponse.then().statusCode(200).body("ok",equalTo(true));
    }
}
