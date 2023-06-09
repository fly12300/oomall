//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.shop.ShopApplication;
import cn.edu.xmu.oomall.shop.dao.bo.Shop;
import cn.edu.xmu.oomall.shop.dao.bo.ShopServiceProduct;
import cn.edu.xmu.oomall.shop.dao.openfeign.GoodsDao;
import cn.edu.xmu.oomall.shop.dao.openfeign.bo.Product;
import cn.edu.xmu.oomall.shop.mapper.po.ShopPo;
import cn.edu.xmu.oomall.shop.service.dto.SimpleShopDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = ShopApplication.class)
@Transactional
public class ShopServiceProductDaoTest {

    @Autowired
    ShopServiceProductDao shopServiceProductDao;

    @MockBean
    GoodsDao goodsDao;


    @MockBean
    RedisUtil redisUtil;

    @Test
    public void retrieveValidByShopId(){

        Mockito.when(goodsDao.retriveProductById(5107L)).thenReturn(new Product(){
            {
                setId(5107L);
            }
            });
        Mockito.when(goodsDao.retriveProductById(5108L)).thenReturn(new Product(){
            {
                setId(5108L);
            }
        });
        Mockito.when(goodsDao.retriveProductById(5109L)).thenReturn(new Product(){
            {
                setId(5109L);
            }
        });

        List<Product> ret = shopServiceProductDao.retrieveValidByShopId(45L, 0,10);

        assertEquals(2, ret.size());
    }

    @Test
    public void retrieveByShopId(){

        Mockito.when(goodsDao.retriveProductById(5107L)).thenReturn(new Product(){
            {
                setId(5107L);
            }
        });
        Mockito.when(goodsDao.retriveProductById(5108L)).thenReturn(new Product(){
            {
                setId(5108L);
            }
        });
        Mockito.when(goodsDao.retriveProductById(5109L)).thenReturn(new Product(){
            {
                setId(5109L);
            }
        });

        List<Product> ret = shopServiceProductDao.retrieveByShopId(45L, 0,10);

        assertEquals(3, ret.size());
    }

    @Test
    public void findById1(){
        ShopServiceProduct bo = new ShopServiceProduct();
        bo.setId(1L);
        bo.setProductId(5107L);
        bo.setMaintainerId(45L);
        bo.setInvalid(ShopServiceProduct.VALID);
        bo.setRegionId(152L);
        bo.setPriority(1000);

        Mockito.when(redisUtil.hasKey(String.format(ShopServiceProductDao.KEY, 1))).thenReturn(true);
        Mockito.when(redisUtil.get(String.format(ShopServiceProductDao.KEY, 1))).thenReturn(bo);

        Optional<ShopServiceProduct> ret = shopServiceProductDao.findById(1L);
        ShopServiceProduct retBo = ret.orElse(null);

        assertThat(retBo.getId()).isEqualTo(Long.valueOf(1));
        assertThat(retBo.getProductId()).isEqualTo(bo.getProductId());
        assertThat(retBo.getMaintainerId()).isEqualTo(bo.getMaintainerId());
        assertThat(retBo.getRegionId()).isEqualTo(bo.getRegionId());
    }

    @Test
    public void findById2(){
        Mockito.when(redisUtil.hasKey(String.format(ShopServiceProductDao.KEY, 1))).thenReturn(false);

        Optional<ShopServiceProduct> ret = shopServiceProductDao.findById(1L);
        ShopServiceProduct retBo = ret.orElse(null);

        assertThat(retBo.getId()).isEqualTo(Long.valueOf(1));
        assertThat(retBo.getProductId()).isEqualTo(5107L);
        assertThat(retBo.getMaintainerId()).isEqualTo(45L);
        assertThat(retBo.getRegionId()).isEqualTo(152L);
    }

    @Test
    public void findById3(){
        Mockito.when(redisUtil.hasKey(String.format(ShopServiceProductDao.KEY, 50))).thenReturn(false);
        assertThrows(BusinessException.class, ()-> shopServiceProductDao.findById(50L));
    }

    @Test
    public void save(){
        UserDto user = new UserDto();
        user.setId(2L);
        user.setName("test1");
        user.setUserLevel(1);
        ShopServiceProduct bo = new ShopServiceProduct();
        bo.setProductId(5110L);
        bo.setRegionId(3L);
        bo.setMaintainerId(45L);
        bo.setInvalid(ShopServiceProduct.INVALID);
        bo.setPriority(1000);

        ShopServiceProduct retBo = shopServiceProductDao.save(bo, user);
        assertThat(retBo.getProductId()).isEqualTo(bo.getProductId());
        assertThat(retBo.getRegionId()).isEqualTo(bo.getRegionId());
        assertThat(retBo.getMaintainerId()).isEqualTo(bo.getMaintainerId());
        assertThat(retBo.getInvalid()).isEqualTo(bo.getInvalid());
        assertThat(retBo.getPriority()).isEqualTo(bo.getPriority());
    }

    @Test
    public void saveById(){
        UserDto user = new UserDto();
        user.setId(2L);
        user.setName("test1");
        user.setUserLevel(1);
        ShopServiceProduct bo = new ShopServiceProduct();
        bo.setId(1L);
        bo.setInvalid(ShopServiceProduct.INVALID);
        bo.setPriority(1);

        Set<String> delKeys = shopServiceProductDao.saveById(bo, user);
        assertThat(delKeys.contains(String.format(ShopServiceProductDao.KEY, bo.getId()))).isTrue();
    }

    @Test
    public void retrieveProductServiceByProductIdAndRegionId(){
        PageDto<SimpleShopDto> pageDto = shopServiceProductDao.retrieveProductServiceByProductIdAndRegionId(5107L, 152L, 1, 10);
        SimpleShopDto shopDto = pageDto.getList().get(0);
        assertThat(shopDto.getId()).isEqualTo(45L);
    }

}
