package cn.edu.xmu.oomall.order.dao.openfeign;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.oomall.order.dao.openfeign.bo.FreightPrice;
import cn.edu.xmu.oomall.order.dao.openfeign.bo.Shop;
import cn.edu.xmu.oomall.order.dao.openfeign.vo.FreightCalcVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("shop-service")
public interface ShopDao {
    @GetMapping("/shops/{id}")
    InternalReturnObject<Shop> getShopById(@PathVariable Long id);

    @PostMapping("/internal/templates/{id}/regions/{rid}/freightprice")
    InternalReturnObject<FreightPrice> calculateFreightPrice(@PathVariable Long id, @PathVariable Long rid,
                                                             @RequestBody List<FreightCalcVo> items);
}
