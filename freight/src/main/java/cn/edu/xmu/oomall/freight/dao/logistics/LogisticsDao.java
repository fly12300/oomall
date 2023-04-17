package cn.edu.xmu.oomall.freight.dao.logistics;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.oomall.freight.dao.ExpressDao;
import cn.edu.xmu.oomall.freight.dao.bo.Express;
import cn.edu.xmu.oomall.freight.dao.bo.Logistics;
import cn.edu.xmu.oomall.freight.mapper.ExpressPoMapper;
import cn.edu.xmu.oomall.freight.mapper.LogisticsPoMapper;
import cn.edu.xmu.oomall.freight.mapper.po.LogisticsPo;
import cn.edu.xmu.oomall.freight.service.dto.SimpleLogisticsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static cn.edu.xmu.javaee.core.util.Common.clearFields;
import static cn.edu.xmu.javaee.core.util.Common.cloneObj;


@Repository
public class LogisticsDao {
    private final Logger logger = LoggerFactory.getLogger(LogisticsDao.class);

    private final ApplicationContext context;
    private final LogisticsPoMapper logisticsPoMapper;

    private final ExpressDao expressDao;

    @Autowired
    public LogisticsDao(ApplicationContext context, LogisticsPoMapper logisticsPoMapper,ExpressDao expressDao) {
        this.context = context;
        this.logisticsPoMapper = logisticsPoMapper;
        this.expressDao=expressDao;
    }

    /**
     * 找到物流对应的Dao
     *
     * @param po 物流po
     * @return 物流Dao
     */
    private LogisticsInf findLogisticsDao(LogisticsPo po) {
        return (LogisticsInf) context.getBean(po.getLogisticsClass());
    }

    /**
     * 通过id获取物流
     *
     * @param id 物流id
     * @return 物流
     */
    public Logistics findById(Long id) {
        logger.debug("findById: id = {}", id);
        if (null == id) {
            return null;
        }

        Optional<LogisticsPo> poOpt = this.logisticsPoMapper.findById(id);
        if (poOpt.isPresent()) {
            LogisticsPo po = poOpt.get();
            LogisticsInf inf = this.findLogisticsDao(po);
            return inf.getLogistics(po);
        } else {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "物流", id));
        }
    }

    /**
     * 根据运单号查询属于哪家物流公司
     * @param billCode 运单号
     * @return
     */
    public SimpleLogisticsDto findByBillCode(String billCode) {
        Express express = this.expressDao.findByBillCode(billCode);

        Logistics logistics = express.getLogistics();
        SimpleLogisticsDto logisticsDto = cloneObj(logistics, SimpleLogisticsDto.class);
        return logisticsDto;
    }
}
