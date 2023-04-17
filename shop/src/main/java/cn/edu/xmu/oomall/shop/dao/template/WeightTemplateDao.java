//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.dao.template;

import cn.edu.xmu.oomall.shop.dao.bo.template.RegionTemplate;
import cn.edu.xmu.oomall.shop.dao.bo.template.WeightTemplate;
import cn.edu.xmu.oomall.shop.mapper.po.RegionTemplatePo;
import cn.edu.xmu.oomall.shop.mapper.WeightTemplatePoMapper;
import cn.edu.xmu.oomall.shop.mapper.po.WeightTemplatePo;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static cn.edu.xmu.javaee.core.util.Common.cloneObj;
import static cn.edu.xmu.javaee.core.util.Common.copyObj;

@Repository
public class WeightTemplateDao implements TemplateDao{

    private static final Logger logger = LoggerFactory.getLogger(WeightTemplateDao.class);

    private WeightTemplatePoMapper mapper;

    @Autowired
    public WeightTemplateDao(WeightTemplatePoMapper weightTemplatePoMapper) {
        this.mapper = weightTemplatePoMapper;
    }

    @Override
    public RegionTemplate getRegionTemplate(RegionTemplatePo po) throws RuntimeException {
        WeightTemplate bo = cloneObj(po, WeightTemplate.class);
        Optional<WeightTemplatePo> wPo = this.mapper.findById(new ObjectId(po.getObjectId())) ;
        wPo.ifPresent(templatePo ->copyObj(templatePo, bo));
        return bo;
    }

    @Override
    public void save(RegionTemplate bo) throws RuntimeException{
        WeightTemplatePo po = cloneObj(bo, WeightTemplatePo.class);
        this.mapper.save(po);
    }

    @Override
    public String insert(RegionTemplate bo) throws RuntimeException {
        WeightTemplatePo po = cloneObj(bo, WeightTemplatePo.class);
        WeightTemplatePo newPo = this.mapper.insert(po);
        return newPo.getObjectId().toString();
    }

}
