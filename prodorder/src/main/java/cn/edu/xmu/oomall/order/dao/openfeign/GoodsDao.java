//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.order.dao.openfeign;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.oomall.order.dao.openfeign.bo.Coupon;
import cn.edu.xmu.oomall.order.dao.openfeign.bo.Onsale;
import cn.edu.xmu.oomall.order.dao.openfeign.bo.Product;
import cn.edu.xmu.oomall.order.service.openfeign.dto.DiscountDto;
import cn.edu.xmu.oomall.order.service.openfeign.vo.SimpleOrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "goods-service")
public interface GoodsDao {
    @GetMapping("/shops/{shopId}/products/{id}")
    InternalReturnObject<Product> getProductById(@PathVariable Long shopId, @PathVariable Long id);

    @GetMapping("/shops/{shopId}/onsales/{id}")
    InternalReturnObject<Onsale> getOnsaleById(@PathVariable Long shopId, @PathVariable Long id);

    @GetMapping("/shops/{shopId}/couponactivities/{id}")
    InternalReturnObject<Coupon> getCouponById(@PathVariable Long shopId, @PathVariable Long id);

    @PostMapping("/couponactivities/{id}/caculate")
    InternalReturnObject<List<DiscountDto>> calculateDiscount(@PathVariable Long id, @RequestBody List<SimpleOrderItemVo> vo);
}
