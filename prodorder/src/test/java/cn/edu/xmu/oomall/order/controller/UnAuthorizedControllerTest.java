package cn.edu.xmu.oomall.order.controller;

import cn.edu.xmu.oomall.order.OrderTestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;

@SpringBootTest(classes = OrderTestApplication.class)
@AutoConfigureMockMvc
@Transactional
class UnAuthorizedControllerTest {

    private static final String ORDER_STATES = "/orders/states";
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getOrderStates() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(ORDER_STATES)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(0)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}