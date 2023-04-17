package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.dao.bo.Undeliverable;
import cn.edu.xmu.oomall.freight.dao.bo.Warehouse;
import cn.edu.xmu.oomall.freight.dao.bo.WarehouseLogistics;
import cn.edu.xmu.oomall.freight.dao.openfeign.RegionDao;
import cn.edu.xmu.oomall.freight.mapper.UndeliverablePoMapper;
import cn.edu.xmu.oomall.freight.mapper.WarehouseLogisticsPoMapper;
import cn.edu.xmu.oomall.freight.mapper.po.UndeliverablePo;
import cn.edu.xmu.oomall.freight.mapper.po.WarehouseLogisticsPo;
import cn.edu.xmu.oomall.freight.mapper.po.WarehousePo;
import com.github.pagehelper.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.*;

@Repository
public class WarehouseLogisticsDao {
    private final Logger logger = LoggerFactory.getLogger(WarehouseLogistics.class);

    private final static String KEY = "WL%d";
    @Value("${oomall.freight.timeout}")
    private int timeout;

    private final RedisUtil redisUtil;
    private final WarehouseLogisticsPoMapper warehouseLogisticsPoMapper;

    private final ShopLogisticsDao shopLogisticsDao;

    private final WarehouseDao warehouseDao;

    @Autowired
    @Lazy
    public WarehouseLogisticsDao(RedisUtil redisUtil,WarehouseLogisticsPoMapper warehouseLogisticsPoMapper,ShopLogisticsDao shopLogisticsDao,WarehouseDao warehouseDao){
        this.redisUtil=redisUtil;
        this.warehouseLogisticsPoMapper=warehouseLogisticsPoMapper;
        this.shopLogisticsDao=shopLogisticsDao;
        this.warehouseDao=warehouseDao;
    }

    /**
     * 根据id查找仓库物流
     * @param id
     * @return
     */
    public WarehouseLogistics findById(Long id) throws RuntimeException {
        logger.debug("findWarehouseLogistics: id = {}", id);
        if (null == id) {
            return null;
        }
        String key = String.format(KEY, id);
        if (redisUtil.hasKey(key)) {
            WarehouseLogistics bo = (WarehouseLogistics) redisUtil.get(key);
            this.setBo(bo);
            return bo;
        }
        Optional<WarehouseLogisticsPo> boOpt = this.warehouseLogisticsPoMapper.findById(id);
        if (boOpt.isPresent()) {
            return this.getBo(boOpt.get(), Optional.of(key));
        } else {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "不可达", id));
        }
    }

    public void setBo(WarehouseLogistics warehouseLogistics){
        warehouseLogistics.setShopLogisticsDao(this.shopLogisticsDao);
        warehouseLogistics.setWarehouseDao(this.warehouseDao);
    }

    /**
     * 获得bo对象
     * @param po
     * @param redisKey
     * @return
     */
    public WarehouseLogistics getBo(WarehouseLogisticsPo po, Optional<String> redisKey){
        WarehouseLogistics bo = cloneObj(po, WarehouseLogistics.class);
        this.setBo(bo);
        redisKey.ifPresent(key -> redisUtil.set(key, bo, timeout));
        return bo;
    }

    /**
     * 根据仓库id获得仓库物流
     * @param id
     * @return
     */
    public List<WarehouseLogistics> getByWarehouseId(Long id,Integer page,Integer pageSize) {

        logger.debug("retrieveByWarehouseId: warehouseId = {}", id);
        if (null == id) {
            return null;
        }
        List<WarehouseLogistics> boList;
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<WarehouseLogisticsPo> pageObj = this.warehouseLogisticsPoMapper.findByWarehouseId(id, pageable);
        if (pageObj.isEmpty()) {
            boList = new ArrayList<>();
        } else {
            boList = pageObj.stream()
                    .map(po -> this.getBo(po, Optional.of(String.format(KEY, po.getId()))))
                    .collect(Collectors.toList());
        }
        logger.debug("retrieveByWarehouseId: boList = {}", boList);
        return boList;
    }

    /**
     * 修改仓库物流
     * @param warehouseLogistics
     * @param userDto
     * @return
     */
    public String save(WarehouseLogistics warehouseLogistics, UserDto userDto) {
        WarehouseLogisticsPo po = cloneObj(warehouseLogistics, WarehouseLogisticsPo.class);
        putGmtFields(po,"modified");
        putUserFields(po,"modifier",userDto);
        this.warehouseLogisticsPoMapper.save(po);
        return String.format(KEY,po.getId());
    }

    /**
     * 新增仓库物流
     * @param warehouseLogistics
     * @param userDto
     * @return
     */
    public void insert(WarehouseLogistics warehouseLogistics, UserDto userDto) {
        WarehouseLogisticsPo po = cloneObj(warehouseLogistics, WarehouseLogisticsPo.class);
        putGmtFields(po,"create");
        putUserFields(po,"creator",userDto);
        if(null!=po.getId()){
            po.setId(null);
        }
        this.warehouseLogisticsPoMapper.save(po);
    }
}
