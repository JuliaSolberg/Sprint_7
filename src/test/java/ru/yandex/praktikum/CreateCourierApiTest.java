package ru.yandex.praktikum;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.yandex.praktikum.api.client.CourierClient;
import ru.yandex.praktikum.model.Courier;
import static org.hamcrest.Matchers.*;

@Epic("API Tests")
@Feature("Courier")
@RunWith(JUnitParamsRunner.class)
public class CreateCourierApiTest extends BaseTest {

    private static final Logger LOGGER = LogManager.getLogger(CreateCourierApiTest.class);
    private CourierClient courierClient;
    private boolean hasToCleanUp;

    @Test
  //  @DisplayName("Создание курьера. Успешный запрос")
    public void createNewCourierOkCreated() {
        hasToCleanUp = true;

        // создание нового курьера
        LOGGER.info("Создание нового курьера...");
        courierClient = new CourierClient();
        Response createResponse = courierClient.createCourier();
        createResponse.then().statusCode(201).body("ok",equalTo(true));
        LOGGER.info("Новый курьер успешно создан: {}", createResponse.body().asString());
    }

    @Test
  //  @DisplayName("Создание курьера. Запрос на создание курьера с уже существующим логином")
    public void createDuplicateCourierNotCreatedConflict() {
        hasToCleanUp = true;

        // создание нового курьера
        LOGGER.info("Создание нового курьера...");
        courierClient = new CourierClient();
        Response createResponse = courierClient.createCourier();
        createResponse.then().statusCode(201).body("ok",equalTo(true));
        LOGGER.info("Новый курьер создан: {}", createResponse.body().asString());


        Response duplicateCreateResponse = courierClient.duplicateCourier();
        duplicateCreateResponse.then().statusCode(409).body("message",equalTo("Этот логин уже используется"));
        LOGGER.warn("Попытка создать дубликат: {}", duplicateCreateResponse.body().asString());

        //проверка что дупликат не был создан
        Response checkResponse = courierClient.checkDuplicateCourier();
        checkResponse.then().statusCode(404).body("message",equalTo("Учетная запись не найдена"));
        LOGGER.info("Проверка, что дубликат не создан: {}", checkResponse.body().asString());
    }

  //  @Step("Подготовка данных для создания тестового курьера без логина или пароля")
    public static Object[][] invalidCourierData() {
        return new Object[][] {
                {"Valusha",""},
                {"","Valusha"},
        };
    }
    @Test
    @Parameters(method = "invalidCourierData")
   // @Description("Создание курьера. Запрос без логина или пароля")
    public void createNewCourierWithoutLoginOrPasswordBadRequest(String paramLogin, String paramPassword) {
        hasToCleanUp = false;

        LOGGER.info("Проверка создания курьера без логина или пароля: login='{}', password='{}'", paramLogin, paramPassword);
        courierClient = new CourierClient();
        Courier invalidCourier = new Courier(paramLogin,paramPassword,"Test_Data");
        Response response = courierClient.createCourierResponse(invalidCourier);
        response.then().statusCode(400).body("message",equalTo("Недостаточно данных для создания учетной записи"));
        LOGGER.warn("Попытка создать курьера с недостаточными данными: {}", response.body().asString());
    }

    @After
   // @Step("Удаление тестового курьера")
    public void tearDown() {
        if (hasToCleanUp) {
            //удаление созданного курьера
            LOGGER.info("Удаление тестового курьера...");
            Response deleteResponse = courierClient.deleteCourier();
            deleteResponse.then().statusCode(200);
            LOGGER.info("Тестовые данные успешно удалены.");}
        else {
            LOGGER.info("Тестовый курьер не создавался, удаление не требуется.");}
    }
}
