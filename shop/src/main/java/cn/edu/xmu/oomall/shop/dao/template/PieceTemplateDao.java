//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.dao.template;

import cn.edu.xmu.oomall.shop.dao.bo.template.PieceTemplate;
import cn.edu.xmu.oomall.shop.dao.bo.template.RegionTemplate;
import cn.edu.xmu.oomall.shop.mapper.po.RegionTemplatePo;
import cn.edu.xmu.oomall.shop.mapper.PieceTemplatePoMapper;
import cn.edu.xmu.oomall.shop.mapper.po.PieceTemplatePo;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static cn.edu.xmu.javaee.core.util.Common.cloneObj;
import static cn.edu.xmu.javaee.core.util.Common.copyObj;

@Repository
public class PieceTemplateDao implements TemplateDao{

    private static final Logger logger = LoggerFactory.getLogger(PieceTemplateDao.class);

    private PieceTemplatePoMapper mapper;

    @Autowired
    public PieceTemplateDao(PieceTemplatePoMapper pieceTemplatePoMapper) {
        this.mapper = pieceTemplatePoMapper;
    }

    @Override
    public RegionTemplate getRegionTemplate(RegionTemplatePo po) throws RuntimeException {
        PieceTemplate bo = cloneObj(po, PieceTemplate.class);
        Optional<PieceTemplatePo> wPo = this.mapper.findById(new ObjectId(po.getObjectId())) ;
        wPo.ifPresent(templatePo -> {
            copyObj(templatePo, bo);
            logger.debug("getRegionTemplate: templatePo = {}, bo = {}", templatePo, bo);
        });
        return bo;
    }

    @Override
    public void save(RegionTemplate bo) throws RuntimeException{
        PieceTemplatePo po = cloneObj(bo, PieceTemplatePo.class);
        this.mapper.save(po);
    }

    @Override
    public String insert(RegionTemplate bo) throws RuntimeException {
        PieceTemplatePo po = cloneObj(bo, PieceTemplatePo.class);
        PieceTemplatePo newPo = this.mapper.insert(po);
        return newPo.getObjectId().toString();    }

}
