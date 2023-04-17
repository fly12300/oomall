package cn.edu.xmu.oomall.freight.controller;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.javaee.core.util.JwtHelper;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.FreightApplication;
import cn.edu.xmu.oomall.freight.controller.vo.CreateShopLogisticsVo;
import cn.edu.xmu.oomall.freight.controller.vo.ShopLogisticsVo;
import cn.edu.xmu.oomall.freight.controller.vo.WarehouseVo;
import cn.edu.xmu.oomall.freight.dao.bo.Region;
import cn.edu.xmu.oomall.freight.dao.openfeign.RegionDao;
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

import java.util.Objects;

import static org.hamcrest.CoreMatchers.is;


@SpringBootTest(classes = FreightApplication.class)
@AutoConfigureMockMvc
class ShopFreightControllerTest {

    private static String shop1Token;
    private final String WAREHOUSES = "/shops/{shopId}/warehouses";
    private final String WAREHOUSES_ID = "/shops/{shopId}/warehouses/{id}";
    private final String WAREHOUSES_ID_SUSPEND = "/shops/{shopId}/warehouses/{id}/suspend";
    private final String WAREHOUSES_ID_RESUME = "/shops/{shopId}/warehouses/{id}/resume";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RegionDao regionDao;

    @MockBean
    RedisUtil redisUtil;
    @BeforeAll
    public static void start() {
        JwtHelper jwt = new JwtHelper();
        shop1Token = jwt.createToken(2L, "1号选手", 1L, 1, 6000);
    }

    @Test
    @Transactional
    void createWarehouse() throws Exception {
        WarehouseVo vo = new WarehouseVo("张三仓", "幻想乡", 303001L, "张三", "110");
        Region region = new Region(303001L, "北京，瑶海区");
        Mockito.when(regionDao.getRegionById(Mockito.any())).thenReturn(new InternalReturnObject<>(region));

        mockMvc.perform(MockMvcRequestBuilders.post(WAREHOUSES, 1)
                        .header("authorization", shop1Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo))))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.CREATED.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name", is("张三仓")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.status", is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.createdBy.userName", is("1号选手")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    void retrieveWarehouses() throws Exception {
        Region region = new Region(303001L, "北京，瑶海区");
        Mockito.when(regionDao.getRegionById(Mockito.any())).thenReturn(new InternalReturnObject<>(region));


        mockMvc.perform(MockMvcRequestBuilders.get(WAREHOUSES, 1)
                        .header("authorization", shop1Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].status", is(1)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    void updateWarehouse() throws Exception {
        WarehouseVo vo = new WarehouseVo("张三仓", "幻想乡", 303001L, "王二麻子", "110");
        Region region = new Region(303001L, "北京，瑶海区");
        Mockito.when(regionDao.getRegionById(Mockito.any())).thenReturn(new InternalReturnObject<>(region));


        mockMvc.perform(MockMvcRequestBuilders.put(WAREHOUSES_ID, 1, 1)
                        .header("authorization", shop1Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    void suspendWarehouse() throws Exception {
        Region region = new Region(303001L, "北京，瑶海区");
        Mockito.when(regionDao.getRegionById(Mockito.any())).thenReturn(new InternalReturnObject<>(region));


        mockMvc.perform(MockMvcRequestBuilders.put(WAREHOUSES_ID_SUSPEND, 1, 1)
                        .header("authorization", shop1Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    void resumeWarehouse() throws Exception {
        Region region = new Region(303001L, "北京，瑶海区");
        Mockito.when(regionDao.getRegionById(Mockito.any())).thenReturn(new InternalReturnObject<>(region));

        mockMvc.perform(MockMvcRequestBuilders.put(WAREHOUSES_ID_RESUME, 1, 1)
                        .header("authorization", shop1Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    void deleteWarehouse() throws Exception {
        Region region = new Region(303001L, "北京，瑶海区");
        Mockito.when(regionDao.getRegionById(Mockito.any())).thenReturn(new InternalReturnObject<>(region));
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.delete(WAREHOUSES_ID, 1, 1)
                        .header("authorization", shop1Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void retrieveShopLogistics1() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/{shopId}/shoplogistics", 1)
                        .header("authorization", shop1Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].id", is(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].priority", is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].invalid", is(1)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    public void updateShopLogisticsById1() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.get(Mockito.anyString())).thenReturn(null);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/{shopId}/shoplogistics/{id}/suspend", 1, 1)
                        .header("authorization", shop1Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.STATENOTALLOW.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    public void updateShopLogisticsById2() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.get(Mockito.anyString())).thenReturn(null);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/{shopId}/shoplogistics/{id}/resume", 1, 1)
                        .header("authorization", shop1Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    public void updateShopLogisticsById3() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.get(Mockito.anyString())).thenReturn(null);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        ShopLogisticsVo vo = new ShopLogisticsVo("secret1", 5);

        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/{shopId}/shoplogistics/{id}", 1, 1)
                        .header("authorization", shop1Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    public void createShopLogistics1() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.get(Mockito.anyString())).thenReturn(null);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        CreateShopLogisticsVo vo = new CreateShopLogisticsVo(1L, "secret1", 666);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/shops/{shopId}/shoplogistics", 1)
                        .header("authorization", shop1Token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo))))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.CREATED.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

}