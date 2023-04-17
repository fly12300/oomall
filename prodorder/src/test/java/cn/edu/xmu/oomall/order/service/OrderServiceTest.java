package cn.edu.xmu.oomall.order.service;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.order.OrderTestApplication;
import cn.edu.xmu.oomall.order.dao.OrderDao;
import cn.edu.xmu.oomall.order.dao.bo.Order;
import cn.edu.xmu.oomall.order.dao.bo.OrderItem;
import cn.edu.xmu.oomall.order.dao.openfeign.CustomerDao;
import cn.edu.xmu.oomall.order.dao.openfeign.ExpressDao;
import cn.edu.xmu.oomall.order.dao.openfeign.GoodsDao;
import cn.edu.xmu.oomall.order.dao.openfeign.ShopDao;
import cn.edu.xmu.oomall.order.dao.openfeign.bo.FreightPrice;
import cn.edu.xmu.oomall.order.dao.openfeign.bo.Onsale;
import cn.edu.xmu.oomall.order.dao.openfeign.bo.Product;
import cn.edu.xmu.oomall.order.service.dto.ConsigneeDto;
import cn.edu.xmu.oomall.order.service.dto.IdNameDto;
import cn.edu.xmu.oomall.order.service.openfeign.PaymentService;
import cn.edu.xmu.oomall.order.service.openfeign.dto.DiscountDto;
import cn.edu.xmu.oomall.order.service.openfeign.dto.IdDto;
import cn.edu.xmu.oomall.order.service.openfeign.dto.PaymentDto;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = OrderTestApplication.class)
@Transactional
class OrderServiceTest {
    @MockBean
    private PaymentService paymentService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDao orderDao;
    @MockBean
    private CustomerDao customerDao;
    @MockBean
    private ExpressDao expressDao;
    @MockBean
    private GoodsDao goodsDao;
    @MockBean
    private ShopDao shopDao;
    @MockBean
    private RocketMQTemplate rocketMQTemplate;
    @MockBean
    private RedisUtil redisUtil;

    @Test
    void saveOrder() {
        Map<Long, List<OrderItem>> packs = new HashMap<>();
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(OrderItem.builder().orderId(1L).name("testOrderItem").price(100L).activityId(1L).build());
        packs.put(1L, orderItems);
        ConsigneeDto consignee = ConsigneeDto.builder().name("testName").address("testAddress").mobile("12341234").regionId(1234L).build();
        UserDto userDto = new UserDto(1L, "testUser", -1L, 1);

        this.orderService.saveOrder(packs, consignee, "testMessage", userDto);
    }

    @Test
    void cancelOrderExecute() {
        Order order = this.orderDao.findById(1L);

        UserDto userDto = new UserDto(1L, "testAdmin", 0L, 1);
        PaymentDto paymentDto = new PaymentDto(1L, 10L, 3L);
        Mockito.when(this.paymentService.getPaymentById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(new InternalReturnObject<>(paymentDto));
        Mockito.when(this.paymentService.createRefund(Mockito.anyLong(), Mockito.anyLong(), Mockito.any())).thenReturn(new InternalReturnObject<>(new IdDto(1L)));
        Mockito.when(this.customerDao.getCustomerById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(new InternalReturnObject<>());
        Mockito.when(this.goodsDao.getOnsaleById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(new InternalReturnObject<>());
        Mockito.when(this.goodsDao.getProductById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(new InternalReturnObject<>());
        Mockito.when(this.expressDao.getExpressById(Mockito.anyLong())).thenReturn(new InternalReturnObject<>());
        Mockito.when(this.shopDao.getShopById(Mockito.anyLong())).thenReturn(new InternalReturnObject<>());

        this.orderService.cancelOrderExecute(order, userDto);

        order.setStatus(102);
        this.orderService.cancelOrderExecute(order, userDto);
    }

    @Test
    void payOrderExecute() {
        Order order = this.orderDao.findById(1L);
        UserDto userDto = new UserDto(1L, "testAdmin", 0L, 1);
        order.setStatus(101);
        Onsale onsale = Onsale.builder().id(1L).price(100L).type((byte) 0).quantity(100).maxQuantity(100).build();

        Mockito.when(this.customerDao.getCustomerById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(new InternalReturnObject<>());
        Mockito.when(this.goodsDao.getOnsaleById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(new InternalReturnObject<>(onsale));
        Mockito.when(this.goodsDao.getProductById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(new InternalReturnObject<>(new Product(1L, "testProduct", new IdNameDto(1L, "testTemplate"), 100)));
        Mockito.when(this.expressDao.getExpressById(Mockito.anyLong())).thenReturn(new InternalReturnObject<>());
        Mockito.when(this.shopDao.getShopById(Mockito.anyLong())).thenReturn(new InternalReturnObject<>());
        Mockito.when(this.goodsDao.calculateDiscount(Mockito.anyLong(), Mockito.anyList())).thenReturn(new InternalReturnObject<>(new ArrayList<>() {{
            add(new DiscountDto(1L, 10, 100L, 50L));
        }}));

        Mockito.when(this.paymentService.createPayment(Mockito.any())).thenReturn(new InternalReturnObject<>(new IdDto(1L)));
        Mockito.when(this.shopDao.calculateFreightPrice(Mockito.anyLong(), Mockito.anyLong(), Mockito.any())).thenReturn(new InternalReturnObject<>(new FreightPrice(100L)));

        this.orderService.payOrderExecute(order, 10L, userDto, 1L, new ArrayList<>());
    }
}