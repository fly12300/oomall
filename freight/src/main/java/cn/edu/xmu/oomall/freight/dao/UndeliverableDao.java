package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.dao.bo.ShopLogistics;
import cn.edu.xmu.oomall.freight.dao.bo.Undeliverable;
import cn.edu.xmu.oomall.freight.dao.openfeign.RegionDao;
import cn.edu.xmu.oomall.freight.mapper.UndeliverablePoMapper;
import cn.edu.xmu.oomall.freight.mapper.po.ShopLogisticsPo;
import cn.edu.xmu.oomall.freight.mapper.po.UndeliverablePo;
import com.github.pagehelper.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.*;


@Repository
public class UndeliverableDao {
    private final Logger logger = LoggerFactory.getLogger(UndeliverableDao.class);
    private final static String KEY = "U%d";
    @Value("${oomall.freight.timeout}")
    private int timeout;

    private final RedisUtil redisUtil;

    private UndeliverablePoMapper undeliverablePoMapper;

    private RegionDao regionDao;

    @Autowired
    public UndeliverableDao(RedisUtil redisUtil,UndeliverablePoMapper undeliverablePoMapper,RegionDao regionDao){
        this.redisUtil=redisUtil;
        this.undeliverablePoMapper=undeliverablePoMapper;
        this.regionDao=regionDao;
    }


    /**
     * 根据id查找不可达
     *
     * @param id 不可达id
     * @return
     */
    public Undeliverable findById(Long id) throws RuntimeException {
        logger.debug("findUndeliverableById: id = {}", id);
        if (null == id) {
            return null;
        }
        String key = String.format(KEY, id);
        if (redisUtil.hasKey(key)) {
            Undeliverable bo = (Undeliverable) redisUtil.get(key);
            this.setBo(bo);
            return bo;
        }
        Optional<UndeliverablePo> boOpt = this.undeliverablePoMapper.findById(id);
        if (boOpt.isPresent()) {
            return this.getBo(boOpt.get(), Optional.of(key));
        } else {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "不可达", id));
        }
    }

    /**
     * 根据shopLogisticId查询不可达信息
     * @param shopLogisticId
     * @param page
     * @param pageSize
     * @return
     */
    public List<Undeliverable> retrieveByShopLogisticId(Long shopLogisticId,Integer page,Integer pageSize) {
        logger.debug("retrieveByShopLogisticId,id={}",shopLogisticId);
        Pageable pageable = PageRequest.of(page-1, pageSize);
        List<Undeliverable> ret = this.undeliverablePoMapper.findByShopLogisticsId(shopLogisticId, pageable).getResult().stream().map(
                po -> this.getBo(po, Optional.of(String.format(KEY, po.getId())))
        ).collect(Collectors.toList());

        logger.debug("retrieveByShopLogisticId,undeliverableList={}",ret);
        return ret;
    }

    public Undeliverable getBo(UndeliverablePo po, Optional<String> redisKey){
        Undeliverable bo = cloneObj(po, Undeliverable.class);
        this.setBo(bo);
        redisKey.ifPresent(key -> redisUtil.set(key, bo, timeout));
        return bo;
    }

    public void setBo(Undeliverable undeliverable){
        undeliverable.setRegionDao(this.regionDao);
    }

    /**
     * 新增商铺物流对应某个地区的不可达
     * @param undeliverable
     * @param userDto
     */
    public void insert(Undeliverable undeliverable, UserDto userDto) {
        UndeliverablePo po = cloneObj(undeliverable, UndeliverablePo.class);
        putGmtFields(po,"create");
        putUserFields(po,"creator",userDto);
        if(null!=po.getId()){
            po.setId(null);
        }
        this.undeliverablePoMapper.save(po);
    }

    /**
     * 修改商铺物流的不可达信息
     * @param undeliverable
     * @param userDto
     * @return
     */
    public String save(Undeliverable undeliverable, UserDto userDto) {
        UndeliverablePo po = cloneObj(undeliverable, UndeliverablePo.class);
        putGmtFields(po,"modified");
        putUserFields(po,"modifier",userDto);
        this.undeliverablePoMapper.save(po);
        return String.format(KEY,po.getId());
    }
}
