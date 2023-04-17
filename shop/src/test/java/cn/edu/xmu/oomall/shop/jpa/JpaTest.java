package cn.edu.xmu.oomall.shop.jpa;

import cn.edu.xmu.javaee.core.util.Common;
import cn.edu.xmu.oomall.shop.ShopApplication;
import cn.edu.xmu.oomall.shop.mapper.ShopPoMapper;
import cn.edu.xmu.oomall.shop.mapper.po.ShopPo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = ShopApplication.class)
public class JpaTest {

    @Autowired
    ShopPoMapper shopPoMapper;

    @Test
    public void JapTest(){
        ShopPo po = new ShopPo();
        po.setId(10L);
        po.setName("jslsb");
        Common.putGmtFields(po,"modified");
        shopPoMapper.save(po);
        Optional<ShopPo> newPo = shopPoMapper.findById(10L);
        assertThat(newPo.get().getName().equals("jslsb"));
        assertThat(newPo.get().getDeposit().equals(5000000L));
    }
}
