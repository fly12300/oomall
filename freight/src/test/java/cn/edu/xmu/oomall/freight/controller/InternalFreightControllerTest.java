package cn.edu.xmu.oomall.freight.controller;


import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.javaee.core.util.JwtHelper;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.controller.vo.ExpressVo;
import cn.edu.xmu.oomall.freight.controller.vo.StatusVo;
import cn.edu.xmu.oomall.freight.dao.ShopLogisticsDao;
import cn.edu.xmu.oomall.freight.dao.bo.Consignee;
import cn.edu.xmu.oomall.freight.dao.bo.ShopLogistics;
import cn.edu.xmu.oomall.freight.mapper.ExpressPoMapper;
import cn.edu.xmu.oomall.freight.mapper.LogisticsPoMapper;
import cn.edu.xmu.oomall.freight.mapper.RoutePoMapper;
import cn.edu.xmu.oomall.freight.mapper.po.ExpressPo;
import cn.edu.xmu.oomall.freight.mapper.po.RoutePo;
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
import java.util.Objects;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;

@SpringBootTest(classes = FreightApplication.class)
@AutoConfigureMockMvc
public class InternalFreightControllerTest {
    private static String shop2Token;
    private static String adminToken;
    private final String PACKAGES_SHOPID = "/internal/shops/{shopId}/packages";
    private final String PACKAGES_ID = "/internal/packages/{id}";
    private final String PACKAGES_SHOPID_ID_CONFIRM = "/internal/shops/{shopId}/packages/{id}/confirm";
    private final String PACKAGES_SHOPID_ID_CANCLE = "/internal/shops/{shopId}/packages/{id}/cancel";
    @Autowired
    private MockMvc mockMvc;
//    @MockBean
    private ShopLogisticsDao shopLogisticsDao;

    @MockBean
    private RedisUtil redisUtil;
    @MockBean
    private ExpressPoMapper expressPoMapper;

    @MockBean
    private RoutePoMapper routePoMapper;
    @Autowired
    private LogisticsPoMapper logisticsPoMapper;


    @BeforeAll
    public static void start() {
        JwtHelper jwt = new JwtHelper();
        shop2Token = jwt.createToken(2L, "1号选手", 2L, 1, 6000);
        adminToken = jwt.createToken(1L, "13088admin", 0L, 1, 3600);
    }

    @Test
    @Transactional
    void createExpress1() throws Exception {
        Consignee sender = new Consignee("fly", "123456", 10L, "上海");
        Consignee delivery = new Consignee("zs", "654321", 11L, "北京");
        ExpressVo vo = new ExpressVo(4L, sender, delivery);
        ShopLogistics shopLogistics = new ShopLogistics();
        shopLogistics.setId(1L);
        shopLogistics.setShopId(2L);
        shopLogistics.setLogisticsId(3L);
        shopLogistics.setInvalid((byte) 0);
        shopLogistics.setSecret("12456");
        shopLogistics.setPriority(0);
        //Mockito.when(shopLogisticsDao.findById(Mockito.any())).thenReturn(shopLogistics);
        String billCode = "test";
        mockMvc.perform(MockMvcRequestBuilders.post(PACKAGES_SHOPID, 2)
                        .header("authorization", shop2Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo))))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.CREATED.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * shopid不对应
     *
     * @throws Exception
     */
    @Test
    @Transactional
    void createExpress2() throws Exception {
        Consignee sender = new Consignee("fly", "123456", 10L, "上海");
        Consignee delivery = new Consignee("zs", "654321", 11L, "北京");
        ExpressVo vo = new ExpressVo(10L, sender, delivery);
        ShopLogistics shopLogistics = new ShopLogistics();
        shopLogistics.setId(1L);
        shopLogistics.setShopId(3L);
        shopLogistics.setLogisticsId(3L);
        shopLogistics.setInvalid((byte) 1);
        shopLogistics.setSecret("12456");
        shopLogistics.setPriority(0);
//        Mockito.when(shopLogisticsDao.findById(Mockito.any())).thenReturn(shopLogistics);
        String billCode = "test";
        mockMvc.perform(MockMvcRequestBuilders.post(PACKAGES_SHOPID, 2)
                        .header("authorization", shop2Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo))))
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.RESOURCE_ID_OUTSCOPE.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * shopLogistics不可用
     *
     * @throws Exception
     */
    @Test
    @Transactional
    void createExpress3() throws Exception {
        Consignee sender = new Consignee("fly", "123456", 10L, "上海");
        Consignee delivery = new Consignee("zs", "654321", 11L, "北京");
        ExpressVo vo = new ExpressVo(10L, sender, delivery);
        ShopLogistics shopLogistics = new ShopLogistics();
        shopLogistics.setId(1L);
        shopLogistics.setShopId(2L);
        shopLogistics.setLogisticsId(3L);
        shopLogistics.setInvalid((byte) 0);
        shopLogistics.setSecret("12456");
        shopLogistics.setPriority(0);
//        Mockito.when(shopLogisticsDao.findById(Mockito.any())).thenReturn(shopLogistics);
        String billCode = "test";
        mockMvc.perform(MockMvcRequestBuilders.post(PACKAGES_SHOPID, 2)
                        .header("authorization", shop2Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo))))
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.RESOURCE_ID_OUTSCOPE.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    void getExpress1() throws Exception {
        ExpressPo po = new ExpressPo();
        po.setId(1L);
        po.setBillCode("SF123456");
        po.setShopLogisticsId(2L);
        po.setSenderRegionId(1L);
        po.setSenderAddress("北京");
        po.setDeliveryRegionId(3L);
        po.setDeliverAddress("上海");
        po.setSenderName("fly");
        po.setSenderMobile("12345");
        po.setDeliveryName("zs");
        po.setStatus((byte) 1);
        po.setShopId(2L);
        po.setCreatorId(3L);
        po.setCreatorName("fly");
        po.setModifierId(4L);
        po.setModifierName("zs");
        po.setDeliverMobile("56789");
        Mockito.when(expressPoMapper.findByBillCode(Mockito.any())).thenReturn(Optional.of(po));
        RoutePo routePo = new RoutePo();
        routePo.setId("2");
        routePo.setExpressId(1L);
        routePo.setContent("测试物流");
        routePo.setGmtCreate(LocalDateTime.of(2000, 10, 2, 10, 10));
        Mockito.when(routePoMapper.findById(Mockito.anyString())).thenReturn(Optional.of(routePo));
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.get(PACKAGES_SHOPID, 2)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("billCode", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * shopid不对应
     *
     * @throws Exception
     */
    @Test
    @Transactional
    void getExpress2() throws Exception {

        RoutePo routePo = new RoutePo();
        routePo.setId("2");
        routePo.setExpressId(1L);
        routePo.setContent("测试物流");
        routePo.setGmtCreate(LocalDateTime.of(2000, 10, 2, 10, 10));
        Mockito.when(routePoMapper.findById(Mockito.anyString())).thenReturn(Optional.of(routePo));
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.get(PACKAGES_SHOPID, 2)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("billCode", "1"))
                .andExpect(MockMvcResultMatchers.status().is(404))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    void getExpress3() throws Exception {

        ExpressPo po = new ExpressPo();
        po.setId(1L);
        po.setBillCode("SF123456");
        po.setShopLogisticsId(2L);
        po.setSenderRegionId(1L);
        po.setSenderAddress("北京");
        po.setDeliveryRegionId(3L);
        po.setDeliverAddress("上海");
        po.setSenderName("fly");
        po.setSenderMobile("12345");
        po.setDeliveryName("zs");
        po.setStatus((byte) 1);
        po.setShopId(3L);
        po.setCreatorId(3L);
        po.setCreatorName("fly");
        po.setModifierId(4L);
        po.setModifierName("zs");
        po.setDeliverMobile("56789");
        Mockito.when(expressPoMapper.findByBillCode(Mockito.any())).thenReturn(Optional.of(po));
        RoutePo routePo = new RoutePo();
        routePo.setId("2");
        routePo.setExpressId(1L);
        routePo.setContent("测试物流");
        routePo.setGmtCreate(LocalDateTime.of(2000, 10, 2, 10, 10));
        Mockito.when(routePoMapper.findById(Mockito.anyString())).thenReturn(Optional.of(routePo));
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.get(PACKAGES_SHOPID, 2)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("billCode", "1"))
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.RESOURCE_ID_OUTSCOPE.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    void getExpressById1() throws Exception {
        ExpressPo po = new ExpressPo();
        po.setId(1L);
        po.setBillCode("SF123456");
        po.setShopLogisticsId(2L);
        po.setSenderRegionId(1L);
        po.setSenderAddress("北京");
        po.setDeliveryRegionId(3L);
        po.setDeliverAddress("上海");
        po.setSenderName("fly");
        po.setSenderMobile("12345");
        po.setDeliveryName("zs");
        po.setStatus((byte) 1);
        po.setShopId(2L);
        po.setCreatorId(3L);
        po.setCreatorName("fly");
        po.setModifierId(4L);
        po.setModifierName("zs");
        po.setDeliverMobile("56789");
        Mockito.when(expressPoMapper.findById(Mockito.any())).thenReturn(Optional.of(po));
        RoutePo routePo = new RoutePo();
        routePo.setId("2");
        routePo.setExpressId(1L);
        routePo.setContent("测试物流");
        routePo.setGmtCreate(LocalDateTime.of(2000, 10, 2, 10, 10));
        Mockito.when(routePoMapper.findById(Mockito.anyString())).thenReturn(Optional.of(routePo));
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        this.mockMvc.perform(MockMvcRequestBuilders.get(PACKAGES_ID, 1)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * id找不到数据
     *
     * @throws Exception
     */
    @Test
    @Transactional
    void getExpressById2() throws Exception {

        RoutePo routePo = new RoutePo();
        routePo.setId("2");
        routePo.setExpressId(1L);
        routePo.setContent("测试物流");
        routePo.setGmtCreate(LocalDateTime.of(2000, 10, 2, 10, 10));
        Mockito.when(routePoMapper.findById(Mockito.anyString())).thenReturn(Optional.of(routePo));
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.get(PACKAGES_SHOPID, 2)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("billCode", "1"))
                .andExpect(MockMvcResultMatchers.status().is(404))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }
    @Test
    @Transactional
    void confirmTest1() throws Exception {
        StatusVo vo = new StatusVo();
        vo.setStatus((byte)1);
        ExpressPo po = new ExpressPo();
        po.setId(1L);
        po.setBillCode("SF123456");
        po.setShopLogisticsId(2L);
        po.setSenderRegionId(1L);
        po.setSenderAddress("北京");
        po.setDeliveryRegionId(3L);
        po.setDeliverAddress("上海");
        po.setSenderName("fly");
        po.setSenderMobile("12345");
        po.setDeliveryName("zs");
        po.setStatus((byte) 1);
        po.setShopId(2L);
        po.setCreatorId(3L);
        po.setCreatorName("fly");
        po.setModifierId(4L);
        po.setModifierName("zs");
        po.setDeliverMobile("56789");
        Mockito.when(expressPoMapper.findById(Mockito.any())).thenReturn(Optional.of(po));
        mockMvc.perform(MockMvcRequestBuilders.put(PACKAGES_SHOPID_ID_CONFIRM, 2, 1)
                        .header("authorization", shop2Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
   }

    /**
     * id不存在
     * @throws Exception
     */
    @Test
    @Transactional
    void confirmTest2() throws Exception {
        StatusVo vo = new StatusVo();
        vo.setStatus((byte)1);
        mockMvc.perform(MockMvcRequestBuilders.put(PACKAGES_SHOPID_ID_CONFIRM, 2, 1)
                        .header("authorization", shop2Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo))))
                .andExpect(MockMvcResultMatchers.status().is(404))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * shopid不对应
     * @throws Exception
     */
    @Test
    @Transactional
    void confirmTest3() throws Exception {
        StatusVo vo = new StatusVo();
        vo.setStatus((byte)1);
        ExpressPo po = new ExpressPo();
        po.setId(1L);
        po.setBillCode("SF123456");
        po.setShopLogisticsId(2L);
        po.setSenderRegionId(1L);
        po.setSenderAddress("北京");
        po.setDeliveryRegionId(3L);
        po.setDeliverAddress("上海");
        po.setSenderName("fly");
        po.setSenderMobile("12345");
        po.setDeliveryName("zs");
        po.setStatus((byte) 1);
        po.setShopId(3L);
        po.setCreatorId(3L);
        po.setCreatorName("fly");
        po.setModifierId(4L);
        po.setModifierName("zs");
        po.setDeliverMobile("56789");
        Mockito.when(expressPoMapper.findById(Mockito.any())).thenReturn(Optional.of(po));
        mockMvc.perform(MockMvcRequestBuilders.put(PACKAGES_SHOPID_ID_CONFIRM, 2, 1)
                        .header("authorization", shop2Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo))))
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.RESOURCE_ID_OUTSCOPE.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * 状态转移失败
     * @throws Exception
     */
    @Test
    @Transactional
    void confirmTest4() throws Exception {
        StatusVo vo = new StatusVo();
        vo.setStatus((byte)1);
        ExpressPo po = new ExpressPo();
        po.setId(1L);
        po.setBillCode("SF123456");
        po.setShopLogisticsId(2L);
        po.setSenderRegionId(1L);
        po.setSenderAddress("北京");
        po.setDeliveryRegionId(3L);
        po.setDeliverAddress("上海");
        po.setSenderName("fly");
        po.setSenderMobile("12345");
        po.setDeliveryName("zs");
        po.setStatus((byte) 0);
        po.setShopId(2L);
        po.setCreatorId(3L);
        po.setCreatorName("fly");
        po.setModifierId(4L);
        po.setModifierName("zs");
        po.setDeliverMobile("56789");
        Mockito.when(expressPoMapper.findById(Mockito.any())).thenReturn(Optional.of(po));
        mockMvc.perform(MockMvcRequestBuilders.put(PACKAGES_SHOPID_ID_CONFIRM, 2, 1)
                        .header("authorization", shop2Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo))))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.STATENOTALLOW.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    void cancelExpressByShopIdAndId1() throws Exception {
        ExpressPo po = new ExpressPo();
        po.setId(1L);
        po.setBillCode("SF123456");
        po.setShopLogisticsId(2L);
        po.setSenderRegionId(1L);
        po.setSenderAddress("北京");
        po.setDeliveryRegionId(3L);
        po.setDeliverAddress("上海");
        po.setSenderName("fly");
        po.setSenderMobile("12345");
        po.setDeliveryName("zs");
        po.setStatus((byte) 0);
        po.setShopId(2L);
        po.setCreatorId(3L);
        po.setCreatorName("fly");
        po.setModifierId(4L);
        po.setModifierName("zs");
        po.setDeliverMobile("56789");
        Mockito.when(expressPoMapper.findById(Mockito.any())).thenReturn(Optional.of(po));
        this.mockMvc.perform(MockMvcRequestBuilders.put(PACKAGES_SHOPID_ID_CANCLE, 2, 1)
                        .header("authorization", shop2Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());

    }
    @Test
    @Transactional
    void cancelExpressByShopIdAndId2() throws Exception {
        ExpressPo po = new ExpressPo();
        po.setId(1L);
        po.setBillCode("SF123456");
        po.setShopLogisticsId(2L);
        po.setSenderRegionId(1L);
        po.setSenderAddress("北京");
        po.setDeliveryRegionId(3L);
        po.setDeliverAddress("上海");
        po.setSenderName("fly");
        po.setSenderMobile("12345");
        po.setDeliveryName("zs");
        po.setStatus((byte) 1);
        po.setShopId(2L);
        po.setCreatorId(3L);
        po.setCreatorName("fly");
        po.setModifierId(4L);
        po.setModifierName("zs");
        po.setDeliverMobile("56789");
        Mockito.when(expressPoMapper.findById(Mockito.any())).thenReturn(Optional.of(po));
        this.mockMvc.perform(MockMvcRequestBuilders.put(PACKAGES_SHOPID_ID_CANCLE, 2, 1)
                        .header("authorization", shop2Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.STATENOTALLOW.getErrNo())))
                .andDo(MockMvcResultHandlers.print());

    }
}
