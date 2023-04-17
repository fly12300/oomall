package cn.edu.xmu.oomall.shop.dao.bo.template;

import cn.edu.xmu.javaee.core.model.bo.OOMallObject;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 运费模板对象
 */
@ToString(callSuper = true)
@NoArgsConstructor
@Data
public class Template extends OOMallObject implements Serializable, Cloneable {
    /**
     * 默认模板
     */
    public static final Byte DEFAULT = 1;
    public static final Byte COMMON = 0;
    /**
     * 商铺id
     */
    private Long shopId;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 1 默认
     */
    private Byte defaultModel;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Template template = (Template)  super.clone();
        template.setDefaultModel(COMMON);
        return template;
    }
}
