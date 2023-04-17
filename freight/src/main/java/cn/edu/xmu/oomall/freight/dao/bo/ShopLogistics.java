package cn.edu.xmu.oomall.freight.dao.bo;

import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.freight.dao.UndeliverableDao;
import cn.edu.xmu.oomall.freight.dao.logistics.LogisticsDao;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;

import java.io.Serializable;
import java.util.*;

@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShopLogistics extends OOMallObject implements Serializable {
    /**
     * 有效
     */
    @JsonIgnore
    public static final Byte VALID = 0;
    /**
     * 无效
     */
    @JsonIgnore
    public static final Byte INVALID = 1;
    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    private Long shopId;
    @Getter
    @Setter
    private Long logisticsId;
    @Getter
    @Setter
    private Byte invalid;
    @Getter
    @Setter
    private String secret;
    @Getter
    @Setter
    private Integer priority;
    @JsonIgnore
    @ToString.Exclude
    private Logistics logistics;
    @JsonIgnore
    @ToString.Exclude
    @Setter
    private LogisticsDao logisticsDao;

    @JsonIgnore
    @ToString.Exclude
    @Setter
    private UndeliverableDao undeliverableDao;

    @JsonIgnore
    @ToString.Exclude
    private List<Undeliverable> undeliverableList;

    @ToString.Include
    @JsonIgnore
    public static final Map<Byte, String> STATUSNAMES = new HashMap() {
        {
            put(VALID, "有效");
            put(INVALID, "无效");
        }
    };

    @JsonIgnore
    @ToString.Exclude
    private static final Map<Byte, Set<Byte>> toStatus = new HashMap<>() {
        {
            put(VALID, new HashSet<>() {
                {
                    add(INVALID);
                }
            });
            put(INVALID, new HashSet<>() {
                {
                    add(VALID);
                }
            });
        }
    };

    public boolean allowStatus(Byte status) {
        boolean ret = false;

        if (null != status && null != this.invalid) {
            Set<Byte> allowStatusSet = toStatus.get(this.invalid);
            if (null != allowStatusSet) {
                ret = allowStatusSet.contains(status);
            }
        }
        return ret;
    }

    public Logistics getLogistics() {
        if (null == this.logistics && null != this.logisticsDao) {
            this.logistics = this.logisticsDao.findById(logisticsId);
        }
        return this.logistics;
    }

    @JsonIgnore
    public String getStatusName() {
        return STATUSNAMES.get(this.invalid);
    }

    public List<Undeliverable> getUndeliverable(Integer page,Integer pageSize) {
        if(null==this.undeliverableList&&null!=this.undeliverableDao){
            this.undeliverableList=this.undeliverableDao.retrieveByShopLogisticId(id,page,pageSize);
        }
        return this.undeliverableList;
    }

}
