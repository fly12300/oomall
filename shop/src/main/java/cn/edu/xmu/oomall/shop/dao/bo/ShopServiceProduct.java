//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.dao.bo;

import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.shop.dao.ShopDao;
import cn.edu.xmu.oomall.shop.dao.openfeign.FreightDao;
import cn.edu.xmu.oomall.shop.dao.openfeign.GoodsDao;
import cn.edu.xmu.oomall.shop.dao.openfeign.bo.Product;
import cn.edu.xmu.oomall.shop.dao.openfeign.bo.Region;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@ToString(callSuper = true)
@NoArgsConstructor
public class ShopServiceProduct extends OOMallObject implements Serializable {
    /**
     * 无效
     */
    public static final Byte INVALID = 1;
    /**
     * 有效
     */
    public static final Byte VALID = 0;

    @Setter
    @Getter
    private Long id;
    @Setter
    @Getter
    private Long productId;

    private Product product;
    @Setter
    @Getter
    private Long maintainerId;

    private Shop shop;

    @Setter
    @Getter
    private Long regionId;

    private Region region;

    @Setter
    private ShopDao shopDao;

    public Shop getShop() {
        if (null != this.maintainerId && null == this.shop) {
            this.shop = this.shopDao.findById(this.maintainerId).orElse(null);
        }
        return this.shop;
    }

    @Setter
    private GoodsDao goodsDao;

    public Product getProduct() {
        if (null != this.productId && null == this.product) {
            this.product = this.goodsDao.retriveProductById(this.productId);
        }
        return this.product;
    }

    @Setter
    private FreightDao freightDao;

    public Region getRegion() {
        if (null != this.regionId && null == this.region) {
            this.region = this.freightDao.findRegionById(this.regionId);
        }
        return this.region;
    }


    /**
     * 开始时间
     */
    @Setter
    @Getter
    private LocalDateTime beginTime;

    /**
     * 终止时间
     */
    @Setter
    @Getter
    private LocalDateTime endTime;

    /**
     * 有效 0 无效 1
     */
    @Setter
    @Getter
    private Byte invalid;

    /**
     * 优先级 0 最高
     */
    @Setter
    @Getter
    private Integer priority;

    public ShopServiceProduct(LocalDateTime beginTime, LocalDateTime endTime, Byte invalid, Integer priority, Long productId, Long maintainerId, Long regionId){
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.invalid = invalid;
        this.priority = priority;
        this.productId = productId;
        this.maintainerId = maintainerId;
        this.regionId = regionId;
    }
}
