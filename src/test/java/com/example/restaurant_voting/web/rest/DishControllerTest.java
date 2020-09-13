package com.example.restaurant_voting.web.rest;

import com.example.restaurant_voting.util.DateUtil;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static com.example.restaurant_voting.TestUtil.userHttpBasic;
import static com.example.restaurant_voting.UserTestData.ADMIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DishControllerTest extends AbstractControllerTest {
    private static final String REST_URL = DishController.REST_URL + '/';
    private static final String REST_URL_BY_DATE = REST_URL + "byDate?date=2020-08-01";
    private static final String REST_URL_BY_NAME = REST_URL + "byName?name=роЛ";
    private static final String PACKAGE_JSON = "dish/";

    private static final Integer DISH_ID = 114;

    /**
     * Warning. Remember, delete or update or create only if DATE menu is FUTURE.
     */
    private final static LocalDate TOMORROW_DATE = LocalDate.parse("2020-08-03");
    private final static LocalDate CURRENT_DATE = LocalDate.parse("2020-08-02");
    private final static LocalDate YESTERDAY_DATE = LocalDate.parse("2020-08-01");

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void getByDate() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_BY_DATE)
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void getByName() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_BY_NAME)
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void getById() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + DISH_ID)
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    //    https://stackoverflow.com/questions/52830441/junit5-mock-a-static-method
    //    https://javadoc.io/static/org.mockito/mockito-core/3.4.6/org/mockito/Mockito.html#static_mocks
    @Test
    @SuppressWarnings("unchecked")
    void deleteDishWithTomorrowDateMenu() throws Exception {
        assertNotEquals(YESTERDAY_DATE, DateUtil.getDate());
        try (MockedStatic mocked = mockStatic(DateUtil.class)) {
            mocked.when(DateUtil::getDate).thenReturn(YESTERDAY_DATE);
            mocked.when(DateUtil::getTomorrow).thenReturn(CURRENT_DATE);
            assertEquals(YESTERDAY_DATE, DateUtil.getDate());

            perform(MockMvcRequestBuilders.delete(REST_URL + DISH_ID)
                    .with(userHttpBasic(ADMIN)))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }
        assertNotEquals(YESTERDAY_DATE, DateUtil.getDate());
    }

    @Test
    @SuppressWarnings("unchecked")
    void updateDishWithTomorrowDateMenu() throws Exception {
        assertNotEquals(YESTERDAY_DATE, DateUtil.getDate());
        try (MockedStatic mocked = mockStatic(DateUtil.class)) {
            mocked.when(DateUtil::getDate).thenReturn(YESTERDAY_DATE);
            mocked.when(DateUtil::getTomorrow).thenReturn(CURRENT_DATE);
            assertEquals(YESTERDAY_DATE, DateUtil.getDate());

            perform(MockMvcRequestBuilders.put(REST_URL + DISH_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(readFile(PACKAGE_JSON + "dish_create_update.json"))
                    .with(userHttpBasic(ADMIN)))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }
        assertNotEquals(YESTERDAY_DATE, DateUtil.getDate());
    }

    @Test
    @SuppressWarnings("unchecked")
    void createDishWithTomorrowDateMenu() throws Exception {
        assertNotEquals(YESTERDAY_DATE, DateUtil.getDate());
        try (MockedStatic mocked = mockStatic(DateUtil.class)) {
            mocked.when(DateUtil::getDate).thenReturn(YESTERDAY_DATE);
            mocked.when(DateUtil::getTomorrow).thenReturn(CURRENT_DATE);
            assertEquals(YESTERDAY_DATE, DateUtil.getDate());

            perform(MockMvcRequestBuilders.post(MenuControllerTest.REST_URL + MenuControllerTest.MENU_ID + "/dishes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(readFile(PACKAGE_JSON + "dish_create_update.json"))
                    .with(userHttpBasic(ADMIN)))
                    .andDo(print())
                    .andExpect(status().isCreated());
        }
        assertNotEquals(YESTERDAY_DATE, DateUtil.getDate());
    }
}