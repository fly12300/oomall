package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.dao.bo.ShopLogistics;
import cn.edu.xmu.oomall.freight.dao.bo.Warehouse;
import cn.edu.xmu.oomall.freight.dao.logistics.LogisticsDao;
import cn.edu.xmu.oomall.freight.mapper.ShopLogisticsPoMapper;
import cn.edu.xmu.oomall.freight.mapper.po.ShopLogisticsPo;
import cn.edu.xmu.oomall.freight.mapper.po.WarehousePo;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.*;

@Repository
public class ShopLogisticsDao {
    private final Logger logger = LoggerFactory.getLogger(ShopLogisticsDao.class);
    private final static String KEY = "SL%d";
    @Value("${oomall.freight.timeout}")
    private int timeout;

    private final RedisUtil redisUtil;
    private final ShopLogisticsPoMapper shopLogisticsPoMapper;
    private final LogisticsDao logisticsDao;
    private final UndeliverableDao undeliverableDao;

    @Autowired
    @Lazy
    public ShopLogisticsDao(RedisUtil redisUtil, ShopLogisticsPoMapper shopLogisticsPoMapper, LogisticsDao logisticsDao,UndeliverableDao undeliverableDao) {
        this.redisUtil = redisUtil;
        this.shopLogisticsPoMapper = shopLogisticsPoMapper;
        this.logisticsDao = logisticsDao;
        this.undeliverableDao=undeliverableDao;
    }

    /**
     * 为bo对象设置dao
     *
     * @param bo 仓库bo
     */
    private void setBo(ShopLogistics bo) {
        bo.setLogisticsDao(this.logisticsDao);
        bo.setUndeliverableDao(this.undeliverableDao);
    }

    /**
     * 获取bo对象
     */
    private ShopLogistics getBo(ShopLogisticsPo po, Optional<String> redisKey) {
        ShopLogistics bo = cloneObj(po, ShopLogistics.class);
        this.setBo(bo);
        redisKey.ifPresent(key -> redisUtil.set(key, bo, timeout));
        return bo;
    }

    /**
     * 通过id查找商铺物流
     *
     * @param id 商铺物流id
     * @return ShopLogistics
     */
    public ShopLogistics findById(Long id) throws RuntimeException {
        logger.debug("findById: id = {}", id);
        if (null == id) {
            return null;
        }
        String key = String.format(KEY, id);
        if (redisUtil.hasKey(key)) {
            ShopLogistics bo = (ShopLogistics) redisUtil.get(key);
            this.setBo(bo);
            return bo;
        }
        Optional<ShopLogisticsPo> boOpt = this.shopLogisticsPoMapper.findById(id);
        if (boOpt.isPresent()) {
            return this.getBo(boOpt.get(), Optional.of(key));
        } else {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "商铺物流", id));
        }
    }

    public List<ShopLogistics> retrieveByShopId(Long shopId, Integer page, Integer pageSize) throws RuntimeException {
        logger.debug("retrieveByShopId: shopId = {}, page = {}, pageSize = {}", shopId, page, pageSize);
        if (null == shopId) {
            return null;
        }
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<ShopLogisticsPo> poPage = shopLogisticsPoMapper.findByShopId(shopId, pageable);
        return poPage.stream()
                .map(po -> this.getBo(po, Optional.of(String.format(ShopLogisticsDao.KEY, po.getId()))))
                .collect(Collectors.toList());
    }

    public String save(ShopLogistics bo, UserDto user) throws RuntimeException {
        ShopLogisticsPo po = cloneObj(bo, ShopLogisticsPo.class);
        putUserFields(po, "modifier", user);
        putGmtFields(po, "modified");
        logger.debug("save: po = {}", po);
        shopLogisticsPoMapper.save(po);
        if(po.getId().equals(-1L)) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "商铺物流", bo.getId()));
        }
        return String.format(KEY, bo.getId());
    }

    public String insert(ShopLogistics shopLogistics, UserDto user) throws RuntimeException {
        List<ShopLogisticsPo> poList = this.shopLogisticsPoMapper.findByLogisticsIdAndSecretAndPriority(shopLogistics.getLogisticsId(), shopLogistics.getSecret(), shopLogistics.getPriority());
        logger.debug("poList = {}", poList);
        if (poList.size() > 0) return null;
        this.setBo(shopLogistics);
        shopLogistics.setId(null);
        ShopLogisticsPo po = cloneObj(shopLogistics, ShopLogisticsPo.class);
        putGmtFields(po, "create");
        putUserFields(po, "creator", user);
        logger.debug("insert: po = {}", po);
        po = this.shopLogisticsPoMapper.save(po);
        shopLogistics.setId(po.getId());
        return String.format(KEY, shopLogistics.getId());
    }

}
