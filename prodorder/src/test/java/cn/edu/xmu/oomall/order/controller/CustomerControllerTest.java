package cn.edu.xmu.oomall.order.controller;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.javaee.core.util.JwtHelper;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.order.OrderTestApplication;
import cn.edu.xmu.oomall.order.controller.vo.ConsigneeVo;
import cn.edu.xmu.oomall.order.controller.vo.OrderItemVo;
import cn.edu.xmu.oomall.order.controller.vo.OrderVo;
import cn.edu.xmu.oomall.order.controller.vo.PayInfoVo;
import cn.edu.xmu.oomall.order.dao.openfeign.CustomerDao;
import cn.edu.xmu.oomall.order.dao.openfeign.ExpressDao;
import cn.edu.xmu.oomall.order.dao.openfeign.GoodsDao;
import cn.edu.xmu.oomall.order.dao.openfeign.ShopDao;
import cn.edu.xmu.oomall.order.dao.openfeign.bo.*;
import cn.edu.xmu.oomall.order.service.dto.IdNameDto;
import cn.edu.xmu.oomall.order.service.dto.IdNameTypeDto;
import cn.edu.xmu.oomall.order.service.openfeign.PaymentService;
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
class CustomerControllerTest {
    private static final String ORDERS = "/orders";
    private static final String ORDERS_PAY = "/orders/{id}/pay";
    private static final String ORDERS_ID = "/orders/{id}";
    private static String customer1Token;
    private static String customer2Token;
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
    private PaymentService paymentService;
    @MockBean
    private RedisUtil redisUtil;
    @MockBean
    private RocketMQTemplate rocketMQTemplate;

    @BeforeAll
    public static void start() {
        JwtHelper jwtHelper = new JwtHelper();
        customer1Token = jwtHelper.createToken(1L, "customer2", -1L, 2, 3600);
        customer2Token = jwtHelper.createToken(2L, "customer2", -1L, 2, 3600);
    }

    @Test
    void getOrders1() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(ORDERS)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("authorization", customer1Token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void getOrders2() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(ORDERS)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("authorization", customer1Token)
                        .param("status", "200"))
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

        mockMvc.perform(MockMvcRequestBuilders.get(ORDERS_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("authorization", customer1Token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void updateOrderById() throws Exception {
        ConsigneeVo vo = new ConsigneeVo("张三", "张三宅", 2418L, "112341");

        mockMvc.perform(MockMvcRequestBuilders.put(ORDERS_ID, 2)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("authorization", customer2Token)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        mockMvc.perform(MockMvcRequestBuilders.put(ORDERS_ID, 2)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("authorization", customer1Token)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo))))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.AUTH_NO_RIGHT.getErrNo())))
                .andDo(MockMvcResultHandlers.print());

        mockMvc.perform(MockMvcRequestBuilders.put(ORDERS_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("authorization", customer1Token)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.STATENOTALLOW.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void cancelOrderById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(ORDERS_ID, 2)
                        .header("authorization", customer2Token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void createOrder() throws Exception {
        OrderItemVo orderItemVo = new OrderItemVo(1L, 20, 1L);
        ConsigneeVo consigneeVo = new ConsigneeVo("张三", "张三宅", 2418L, "112341");
        List<OrderItemVo> voList = new ArrayList<>();
        voList.add(orderItemVo);
        OrderVo orderVo = new OrderVo(voList, consigneeVo, "new Order");

        IdNameTypeDto activity = IdNameTypeDto.builder().id(1L).name("优惠").type((byte) 0).build();
        List<IdNameTypeDto> actList = new ArrayList<>();
        actList.add(activity);
        Onsale onsale = Onsale.builder().id(1L).actList(actList).product(new IdNameDto(1L, "testProduct")).quantity(100).maxQuantity(40)
                .shop(new IdNameTypeDto(1L, "testShop", (byte) 0)).price(100L).build();

        Mockito.when(goodsDao.getOnsaleById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(new InternalReturnObject<>(onsale));
        mockMvc.perform(MockMvcRequestBuilders.post(ORDERS)
                        .header("authorization", customer1Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(orderVo))))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void getOrdersByCustomerId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(ORDERS)
                        .header("authorization", customer1Token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void payOrder() throws Exception {
        PayInfoVo vo = new PayInfoVo(100L, 1L, null);
        mockMvc.perform(MockMvcRequestBuilders.post(ORDERS_PAY, 38051L)
                        .header("authorization", customer1Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}