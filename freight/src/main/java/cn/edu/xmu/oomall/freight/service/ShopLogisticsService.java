package cn.edu.xmu.oomall.freight.service;


import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.controller.vo.CreateShopLogisticsVo;
import cn.edu.xmu.oomall.freight.controller.vo.ShopLogisticsVo;
import cn.edu.xmu.oomall.freight.dao.ShopLogisticsDao;
import cn.edu.xmu.oomall.freight.dao.bo.ShopLogistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.model.Constants.MAX_RETURN;


@Service
public class ShopLogisticsService {
    private static final Logger logger = LoggerFactory.getLogger(ShopLogisticsService.class);

    private ShopLogisticsDao shopLogisticsDao;
    private RedisUtil redisUtil;

    @Autowired
    public ShopLogisticsService(ShopLogisticsDao shopLogisticsDao, RedisUtil redisUtil) {
        this.shopLogisticsDao = shopLogisticsDao;
        this.redisUtil = redisUtil;
    }

    @Transactional
    public PageDto<ShopLogistics> retrieveShopLogistics(Long shopId, Integer page, Integer pageSize) {
        logger.debug("retrieveShopLogistics: shopId = {}, page = {}, pageSize = {}", shopId, page, pageSize);
        List<ShopLogistics> boList = shopLogisticsDao.retrieveByShopId(shopId, page, pageSize)
                .stream()
                .sorted(Comparator.comparing(ShopLogistics::getPriority))
                .collect(Collectors.toList());
        return new PageDto<>(boList, page, pageSize);
    }

    @Transactional
    public void updateShopLogisticsStatusById(Long id, Byte status, ShopLogistics shopLogistics, UserDto user) {
        logger.debug("updateShopLogisticsStatusById: id = {}, status = {}", id, status);
        ShopLogistics bo = (null == shopLogistics) ? this.shopLogisticsDao.findById(id) : shopLogistics;
        if (!bo.allowStatus(status)) {
            if (null == shopLogistics) {
                throw new BusinessException(ReturnNo.STATENOTALLOW, String.format(ReturnNo.STATENOTALLOW.getMessage(), "商铺物流", id, bo.getStatusName()));
            }
            return;
        }
        bo.setInvalid(status);
        String key = this.shopLogisticsDao.save(bo, user);
        this.redisUtil.del(key);
    }

    @Transactional
    public void updateShopLogisticsById(Long id, ShopLogisticsVo vo, UserDto user) {
        ShopLogistics bo = this.shopLogisticsDao.findById(id);
        logger.debug("updateShopLogisticsById: bo = {}", bo);
        bo.setSecret(vo.getSecret());
        bo.setPriority(vo.getPriority());
        bo.setId(id);
        String key = this.shopLogisticsDao.save(bo, user);
        this.redisUtil.del(key);
    }

    @Transactional
    public ReturnObject createShopLogistics(CreateShopLogisticsVo vo, Long shopId, UserDto user) {
        ShopLogistics bo = new ShopLogistics();
        bo.setLogisticsId(vo.getLogisticsId());
        bo.setPriority(vo.getPriority());
        bo.setSecret(vo.getSecret());
        bo.setShopId(shopId);
        bo.setInvalid((byte) 0);
        logger.debug("createShopLogistics: bo = {}", bo);
        String ret = this.shopLogisticsDao.insert(bo, user);
        logger.debug("createShopLogistics: ret = {}", ret);
        if (ret == null) {
            return new ReturnObject(ReturnNo.FREIGHT_SHOPLOGISTICS_EXIST);
        } else {
            return new ReturnObject(ReturnNo.CREATED);
        }
    }
}
