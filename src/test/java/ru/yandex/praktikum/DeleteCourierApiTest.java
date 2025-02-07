package ru.yandex.praktikum;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.model.Courier;
import ru.yandex.praktikum.model.LoginCourier;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;


@Epic("API Tests")
@Feature("Courier")
public class DeleteCourierApiTest {
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    @DisplayName("Удаление курьера. Запрос без ID курьера")
    public void deleteCourierWithoutIdBadRequest() {
        ValidatableResponse deleteResponse = given()
                .header("Content-type", "application/json")
                .delete("/api/v1/courier/" )
                .then()
                .statusCode(400);
        deleteResponse.assertThat().body("message",equalTo("Недостаточно данных для удаления курьера"));
    }

    @Test
    @DisplayName("Удаление курьера. Запрос с несуществующим ID курьера")
    public void deleteCourierWrongIdNotFound() {
        ValidatableResponse deleteResponse = given()
                .header("Content-type", "application/json")
                .delete("/api/v1/courier/" + 8888)
                .then()
                .statusCode(404);
        deleteResponse.assertThat().body("message",equalTo("Курьера с таким id нет"));
    }

    @Test
    @DisplayName("Удаление курьера. Успешный запрос")
    public void deleteCourierOk() {
        Courier courier = new Courier("AmyMyAmy","1234","Winehouse");
        given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier");

        LoginCourier loginCourier = LoginCourier.fromCourier(courier);
        ValidatableResponse loginResponse = given()
                .header("Content-type", "application/json")
                .body(loginCourier)
                .post("/api/v1/courier/login")
                .then().statusCode(200);

        int courierId = loginResponse.extract().jsonPath().getInt("id");
        System.out.println("Courier ID: " + courierId);

        ValidatableResponse deleteResponse = given()
                .header("Content-type", "application/json")
                .delete("/api/v1/courier/" + courierId)
                .then()
                .statusCode(200);
        deleteResponse.assertThat().body("ok",equalTo(true));
    }
}
