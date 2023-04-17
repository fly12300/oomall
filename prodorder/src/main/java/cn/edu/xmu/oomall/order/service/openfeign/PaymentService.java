package cn.edu.xmu.oomall.order.service.openfeign;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.oomall.order.service.openfeign.dto.IdDto;
import cn.edu.xmu.oomall.order.service.openfeign.dto.PaymentDto;
import cn.edu.xmu.oomall.order.service.openfeign.vo.PaymentVo;
import cn.edu.xmu.oomall.order.service.openfeign.vo.RefundVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("payment-service")
public interface PaymentService {

    @PostMapping("/internal/payments")
    InternalReturnObject<IdDto> createPayment(@RequestBody PaymentVo vo);

    @PostMapping("/internal/shops/{shopId}/payments/{id}/refunds")
    InternalReturnObject<IdDto> createRefund(@PathVariable Long shopId,
                                             @PathVariable Long id,
                                             @RequestBody RefundVo vo);

    @GetMapping("/internal/shops/{shopId}/payments/{id}")
    InternalReturnObject<PaymentDto> getPaymentById(@PathVariable Long shopId, @PathVariable Long id);
}
