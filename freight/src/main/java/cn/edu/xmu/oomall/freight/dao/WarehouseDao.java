package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.dao.bo.Warehouse;
import cn.edu.xmu.oomall.freight.dao.openfeign.RegionDao;
import cn.edu.xmu.oomall.freight.mapper.WarehousePoMapper;
import cn.edu.xmu.oomall.freight.mapper.po.WarehousePo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.*;

@Repository
public class WarehouseDao {
    private final Logger logger = LoggerFactory.getLogger(WarehouseDao.class);
    private final static String KEY = "W%d";
    private final static Long SAVE_FAILED_ID = -1L;
    @Value("${oomall.freight.timeout}")
    private int timeout;

    private final WarehousePoMapper warehousePoMapper;
    private final RegionDao regionDao;
    private final RedisUtil redisUtil;

    private final WarehouseLogisticsDao warehouseLogisticsDao;

    @Autowired
    public WarehouseDao(WarehousePoMapper warehousePoMapper, RegionDao regionDao, RedisUtil redisUtil,WarehouseLogisticsDao warehouseLogisticsDao) {
        this.warehousePoMapper = warehousePoMapper;
        this.regionDao = regionDao;
        this.redisUtil = redisUtil;
        this.warehouseLogisticsDao=warehouseLogisticsDao;
    }

    /**
     * 为bo对象设置dao
     *
     * @param bo 仓库bo
     */
    private void setBo(Warehouse bo) {
        bo.setRegionDao(this.regionDao);
        bo.setWarehouseLogisticsDao(this.warehouseLogisticsDao);
    }

    /**
     * 获取bo对象
     */
    private Warehouse getBo(WarehousePo po, Optional<String> redisKey) {
        Warehouse bo = cloneObj(po, Warehouse.class);
        this.setBo(bo);
        redisKey.ifPresent(key -> redisUtil.set(key, bo, timeout));
        return bo;
    }

    /**
     * 新增仓库
     *
     * @param warehouse 仓库bo
     * @return Warehouse
     */
    public void insert(Warehouse warehouse, UserDto user) throws RuntimeException {
        this.setBo(warehouse);
        warehouse.setId(null);
        WarehousePo po = cloneObj(warehouse, WarehousePo.class);
        putGmtFields(po, "create");
        putUserFields(po, "creator", user);
        logger.debug("save: po = {}", po);
        po = this.warehousePoMapper.save(po);
        warehouse.setId(po.getId());
    }

    /**
     * 通过商户id获取仓库
     *
     * @param shopId   商户id
     * @param page     页码
     * @param pageSize 页大小
     * @return List
     */
    public List<Warehouse> retrieveByShopId(Long shopId, Integer page, Integer pageSize) throws RuntimeException {
        logger.debug("retrieveByShopId: shopId = {}", shopId);
        if (null == shopId) {
            return null;
        }
        List<Warehouse> boList;
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<WarehousePo> pageObj = this.warehousePoMapper.findByShopId(shopId, pageable);
        if (pageObj.isEmpty()) {
            boList = new ArrayList<>();
        } else {
            boList = pageObj.stream()
                    .map(po -> this.getBo(po, Optional.of(String.format(KEY, po.getId()))))
                    .collect(Collectors.toList());
        }
        logger.debug("retrieveByShopId: boList = {}", boList);
        return boList;
    }

    /**
     * 通过id查找仓库
     *
     * @param id 仓库id
     * @return Warehouse
     */
    public Warehouse findById(Long id) throws RuntimeException {
        logger.debug("findById: id = {}", id);
        if (null == id) {
            return null;
        }
        String key = String.format(KEY, id);
        if (redisUtil.hasKey(key)) {
            Warehouse bo = (Warehouse) redisUtil.get(key);
            this.setBo(bo);
            return bo;
        }
        Optional<WarehousePo> boOpt = this.warehousePoMapper.findById(id);
        if (boOpt.isPresent()) {
            return this.getBo(boOpt.get(), Optional.of(key));
        } else {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "仓库", id));
        }
    }

    /**
     * 更新仓库
     *
     * @param bo 仓库bo
     */
    public String saveById(Warehouse bo, UserDto user) throws RuntimeException {
        logger.debug("saveById: bo = {}, user = {}", bo, user);
        WarehousePo po = cloneObj(bo, WarehousePo.class);
        putUserFields(po, "modifier", user);
        putGmtFields(po, "modified");
        logger.debug("saveById: po = {}", po);
        this.warehousePoMapper.save(po);
        if (SAVE_FAILED_ID.equals(po.getId())) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "仓库", bo.getId()));
        }
        return String.format(KEY, bo.getId());
    }

    /**
     * 删除仓库
     *
     * @param bo 仓库bo
     */
    public String deleteById(Warehouse bo) throws RuntimeException {
        logger.debug("deleteById: id = {}", bo.getId());
        this.warehousePoMapper.deleteById(bo.getId());
        return String.format(KEY, bo.getId());
    }
}
