package cn.edu.xmu.oomall.freight.mapper.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "freight_express")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpressPo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String billCode;
    private Long shopLogisticsId;
    private Long senderRegionId;
    private String senderAddress;
    private Long deliveryRegionId;
    private String deliverAddress;
    private String senderName;
    private String senderMobile;
    private String deliveryName;
    private Byte status;
    private Long shopId;
    private Long creatorId;
    private String creatorName;
    private Long modifierId;
    private String modifierName;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private String deliverMobile;
}
