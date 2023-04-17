//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.dao.template;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.shop.dao.bo.divide.DivideStrategy;
import cn.edu.xmu.oomall.shop.dao.bo.divide.PackAlgorithm;
import cn.edu.xmu.oomall.shop.dao.bo.template.RegionTemplate;
import cn.edu.xmu.oomall.shop.mapper.RegionTemplatePoMapper;
import cn.edu.xmu.oomall.shop.mapper.po.RegionTemplatePo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static cn.edu.xmu.javaee.core.util.Common.*;

/**
 * 运费模板的dao
 */
@Repository
public class RegionTemplateDao {
    private static final Logger logger = LoggerFactory.getLogger(RegionTemplateDao.class);

    private static final String KEY = "RT%d";

    @Value("${oomall.shop.region-template.timeout}")
    private Long timeout;

    @Value("${oomall.shop.region-template.strategy}")
    private String strategy;

    @Value("${oomall.shop.region-template.algorithm}")
    private String algorithm;

    private RegionTemplatePoMapper regionTemplatePoMapper;

    private ApplicationContext context;

    private RedisUtil redisUtil;

    @Autowired
    public RegionTemplateDao(ApplicationContext context, RegionTemplatePoMapper regionTemplatePoMapper, RedisUtil redisUtil) {
        this.context = context;
        this.regionTemplatePoMapper = regionTemplatePoMapper;
        this.redisUtil = redisUtil;
    }

    /**
     * 返回Bean对象
     * @author Ming Qiu
     * <p>
     * date: 2022-11-22 16:11
     * @param po
     * @return
     */
    private TemplateDao findTemplateDao(RegionTemplatePo po){
        return (TemplateDao) context.getBean(po.getTemplateDao());
    }

    /**
     * 根据关键字找到运费模板
     * @author Ming Qiu
     * <p>
     * date: 2022-11-22 12:22
     * @param id
     * @return
     * @throws RuntimeException
     */
    public RegionTemplate findById(Long id) throws RuntimeException {
        String key = String.format(KEY, id);
        if (redisUtil.hasKey(key)) {
            return (RegionTemplate) redisUtil.get(key);
        }
        logger.debug("findById: id = {}",id);

        Optional<RegionTemplatePo> ret = regionTemplatePoMapper.findById(id);
        if (ret.isEmpty()) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "运费模板", id));
        } else {
            TemplateDao dao = this.findTemplateDao(ret.get());
            RegionTemplate bo = dao.getRegionTemplate(ret.get());
            logger.debug("findById: bo = {}",bo);
            DivideStrategy divideStrategy;
            PackAlgorithm packAlgorithm;
            try {
                packAlgorithm = (PackAlgorithm) Class.forName(this.algorithm).getDeclaredConstructor().newInstance();
                logger.debug("findById: packAlgorithm = {}",packAlgorithm);

                try {
                    divideStrategy = (DivideStrategy) Class.forName(this.strategy).getDeclaredConstructor(PackAlgorithm.class).newInstance(packAlgorithm);
                    bo.setStrategy(divideStrategy);
                } catch (Exception e) {
                    logger.error("findById: message = {}",e.getMessage());
                    throw new BusinessException(ReturnNo.APPLICATION_PARAM_ERR, String.format(ReturnNo.APPLICATION_PARAM_ERR.getMessage(), "oomall.shop.region-template.strategy"));
                }

            } catch (Exception e) {
                logger.error("findById: message = {}",e.getMessage());
                throw new BusinessException(ReturnNo.APPLICATION_PARAM_ERR, String.format(ReturnNo.APPLICATION_PARAM_ERR.getMessage(), "oomall.shop.region-template.algorithm"));
            }

            redisUtil.set(key, bo, timeout);
            return bo;
        }
    }

    /**
     * 修改模板
     * @author Ming Qiu
     * <p>
     * date: 2022-11-22 17:14
     * @param bo
     * @param user
     * @throws RuntimeException
     */
    public List<String> save(RegionTemplate bo, UserDto user) throws RuntimeException{
        logger.debug("save: bo ={}, user = {}",bo, user);
        List<String> delKeys = new ArrayList<>();
        String key = String.format(KEY, bo.getId());
        RegionTemplatePo po = cloneObj(bo, RegionTemplatePo.class);
        TemplateDao dao = this.findTemplateDao(po);
        if (regionTemplatePoMapper.existsById(bo.getId())){
            dao.save(bo);
            putUserFields(po, "modifier", user);
            putGmtFields(po, "modified");
            delKeys.add(key);
            regionTemplatePoMapper.save(po);
        }else{
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(),"运费模板", bo.getId()));
        }
        return delKeys;
    }

    /**
     * 新增模板
     * @author Ming Qiu
     * <p>
     * date: 2022-11-22 17:14
     * @param bo
     * @param user
     * @throws RuntimeException
     */
    public RegionTemplate insert(RegionTemplate bo, UserDto user) throws RuntimeException{
        logger.debug("save: bo ={}, user = {}",bo, user);
        RegionTemplatePo po = cloneObj(bo, RegionTemplatePo.class);
        TemplateDao dao = this.findTemplateDao(po);
        String objectId = dao.insert(bo);
        po.setObjectId(objectId);
        putUserFields(po, "creator", user);
        putGmtFields(po, "create");
        logger.debug("save: po = {}",po);
        RegionTemplatePo newPo = regionTemplatePoMapper.save(po);
        logger.debug("save: newPo = {}",newPo);
        bo.setId(newPo.getId());
        return bo;
    }


}
