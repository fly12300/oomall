package cn.edu.xmu.oomall.order.controller;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.javaee.core.util.JwtHelper;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.order.OrderTestApplication;
import cn.edu.xmu.oomall.order.controller.vo.OrderMessageVo;
import cn.edu.xmu.oomall.order.dao.openfeign.CustomerDao;
import cn.edu.xmu.oomall.order.dao.openfeign.ExpressDao;
import cn.edu.xmu.oomall.order.dao.openfeign.GoodsDao;
import cn.edu.xmu.oomall.order.dao.openfeign.ShopDao;
import cn.edu.xmu.oomall.order.dao.openfeign.bo.*;
import cn.edu.xmu.oomall.order.service.dto.IdNameDto;
import cn.edu.xmu.oomall.order.service.dto.IdNameTypeDto;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.CoreMatchers.is;

@SpringBootTest(classes = OrderTestApplication.class)
@AutoConfigureMockMvc
@Transactional
class ShopControllerTest {
    private static final String ORDERS = "/shops/{shopId}/orders";
    private static final String ORDERS_ID = "/shops/{shopId}/orders/{id}";
    private static final String ORDERS_CONFIRM = "/shops/{shopId}/orders/{id}/confirm";
    private static String shop1Token;
    private static String shop2Token;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CustomerDao customerDao;
    @MockBean
    private ExpressDao expressDao;
    @MockBean
    private GoodsDao goodsDao;
    @MockBean
    private ShopDao shopDao;
    @MockBean
    private RedisUtil redisUtil;
    @MockBean
    private RocketMQTemplate rocketMQTemplate;

    @BeforeAll
    public static void start() {
        JwtHelper jwtHelper = new JwtHelper();
        shop1Token = jwtHelper.createToken(9L, "shop1", 1L, 2, 3600);
        shop2Token = jwtHelper.createToken(2L, "shop2", 2L, 2, 3600);
    }

    @Test
    void getOrdersByShopId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(ORDERS, 1)
                        .header("authorization", shop1Token)
                        .param("page", "1")
                        .param("pageSize", "30")
                        .param("customerId", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void updateOrderMessage() throws Exception {
        OrderMessageVo vo = new OrderMessageVo("hello");

        mockMvc.perform(MockMvcRequestBuilders.put(ORDERS_ID, 1, 1)
                        .header("authorization", shop1Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void getOrderById() throws Exception {
        Customer customer = Customer.builder().id(1L).name("赵某").build();
        Shop shop = Shop.builder().id(1L).name("好店").type((byte) 0).build();
        Express express = Express.builder().id(1L).billCode("JT1234123412341").build();
        IdNameTypeDto activity = IdNameTypeDto.builder().id(1L).name("优惠").type((byte) 0).build();
        List<IdNameTypeDto> actList = new ArrayList<>();
        actList.add(activity);
        Onsale onsale = Onsale.builder().id(1L).actList(actList).price(100L).product(new IdNameDto(1L, "testProduct")).build();

        Mockito.when(customerDao.getCustomerById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(new InternalReturnObject<>(customer));
        Mockito.when(shopDao.getShopById(Mockito.anyLong())).thenReturn(new InternalReturnObject<>(shop));
        Mockito.when(expressDao.getExpressById(Mockito.anyLong())).thenReturn(new InternalReturnObject<>(express));
        Mockito.when(goodsDao.getOnsaleById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(new InternalReturnObject<>(onsale));
        Mockito.when(goodsDao.getCouponById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(new InternalReturnObject<>(new Coupon(1L, "testCoupon", new IdNameTypeDto(1L, "testShop", (byte) 0), 10, (byte) 0, 1, LocalDateTime.MAX, "testStrategy")));
        Mockito.when(goodsDao.getProductById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(new InternalReturnObject<>(new Product(1L, "testProduct", new IdNameDto(1L, "testTemplate"), 100)));

        mockMvc.perform(MockMvcRequestBuilders.get(ORDERS_ID, 1, 1)
                        .header("authorization", shop1Token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderSn", is("2016102361242")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void cancelOrderById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(ORDERS_ID, 1, 1)
                        .header("authorization", shop1Token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.STATENOTALLOW.getErrNo())));
    }

    @Test
    void confirmOrder() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(ORDERS_CONFIRM, 1, 754)
                        .header("authorization", shop1Token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}