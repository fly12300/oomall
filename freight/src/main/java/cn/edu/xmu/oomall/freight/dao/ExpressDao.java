package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.dao.bo.Express;
import cn.edu.xmu.oomall.freight.dao.bo.Warehouse;
import cn.edu.xmu.oomall.freight.dao.openfeign.RegionDao;
import cn.edu.xmu.oomall.freight.mapper.ExpressPoMapper;
import cn.edu.xmu.oomall.freight.mapper.po.ExpressPo;
import cn.edu.xmu.oomall.freight.service.dto.SimplePackageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static cn.edu.xmu.javaee.core.util.Common.*;

@Repository
public class ExpressDao {

    private final Logger logger = LoggerFactory.getLogger(ExpressDao.class);
    private final static String KEY = "E%d";
    private ExpressPoMapper expressPoMapper;
    private RegionDao regionDao;
    private ShopLogisticsDao shopLogisticsDao;
    private final RedisUtil redisUtil;

    @Value("${oomall.freight.timeout}")
    private int timeout;


    @Autowired
    public ExpressDao(ExpressPoMapper expressPoMapper, RegionDao regionDao, ShopLogisticsDao shopLogisticsDao, RedisUtil redisUtil) {
        this.expressPoMapper = expressPoMapper;
        this.regionDao = regionDao;
        this.shopLogisticsDao = shopLogisticsDao;
        this.redisUtil = redisUtil;
    }


    private void setBo(Express bo){
        bo.setRegionDao(this.regionDao);
        bo.setShopLogisticsDao(this.shopLogisticsDao);
    }

    private Express getBo(ExpressPo po, Optional<String> redisKey){
        Express obj = cloneObj(po, Express.class);
        setBo(obj);
        redisKey.ifPresent(key -> redisUtil.set(key, obj, timeout));
        return obj;
    }

    public SimplePackageDto saveExpressByBo(Express bo, UserDto user){
        ExpressPo po = cloneObj(bo, ExpressPo.class);
        logger.debug("saveExpressByBo: po = {}", po);
        po.setId(null);
        putGmtFields(po, "create");
        putUserFields(po, "creator", user);
        logger.debug("saveExpressByBolater: po = {}", po);
        ExpressPo save = expressPoMapper.save(po);
        logger.debug("save: po = {}",save);
        return new SimplePackageDto(po.getId(),bo.getBillCode());
    }
    public Express findById(Long id){
        if (null==id) return null;
        String key = String.format(KEY, id);
        if (redisUtil.hasKey(key)) {
            Express bo = (Express) redisUtil.get(key);
            this.setBo(bo);
            return bo;
        }
        Optional<ExpressPo> po = expressPoMapper.findById(id);
        if (po.isEmpty()){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST,
                    String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "运单", id));
        }
        Express bo = getBo(po.get(), Optional.of(key));
        return bo;
    }

    public Express findByBillCode(String billCode){
        Optional<ExpressPo> po = expressPoMapper.findByBillCode(billCode);
        if (po.isEmpty()){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST,
                    String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "运单", 1));
        }
        return getBo(po.get(), Optional.of(String.format(KEY, po.get().getId())));
    }
    public void updateExpress(Express bo,UserDto user){
        ExpressPo po = cloneObj(bo, ExpressPo.class);
        logger.debug("updateExpress: po = {}", po);
        putGmtFields(po, "modified");
        putUserFields(po, "modifier", user);
        logger.debug("updateExpresslater: po = {}", po);
        ExpressPo save = expressPoMapper.save(po);
        logger.debug("save: po = {}",save);
        redisUtil.del(String.format(KEY, bo.getId()));
    }

}
