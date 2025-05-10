package fpt.aptech.projectbe.mapper;

import fpt.aptech.projectbe.dto.PaymentDTO;
import fpt.aptech.projectbe.entites.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {
    
    public PaymentDTO toDTO(Payment payment) {
        if (payment == null) {
            return null;
        }
        
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setOrderId(payment.getOrder().getId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setStatus(payment.getStatus());
        
        return dto;
    }
    
    public Payment toEntity(PaymentDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Payment payment = new Payment();
        payment.setId(dto.getId());
        payment.setAmount(dto.getAmount());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setPaymentDate(dto.getPaymentDate());
        payment.setStatus(dto.getStatus());
        
        return payment;
    }
} 