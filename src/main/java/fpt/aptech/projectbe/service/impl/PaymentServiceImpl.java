package fpt.aptech.projectbe.service.impl;

import fpt.aptech.projectbe.entites.Payment;
import fpt.aptech.projectbe.entites.Order;
import fpt.aptech.projectbe.repository.PaymentRepository;
import fpt.aptech.projectbe.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    @Override
    public Optional<Payment> findById(Integer id) {
        return paymentRepository.findById(id);
    }

    @Override
    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public Payment update(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public void deleteById(Integer id) {
        paymentRepository.deleteById(id);
    }

    @Override
    public List<Payment> findByOrder(Order order) {
        return paymentRepository.findByOrder(order);
    }

    @Override
    public List<Payment> findByStatus(String status) {
        return paymentRepository.findByStatus(status);
    }

    @Override
    public List<Payment> findByPaymentMethod(String paymentMethod) {
        return paymentRepository.findByPaymentMethod(paymentMethod);
    }

    @Override
    public List<Payment> findByOrderId(Integer orderId) {
        return paymentRepository.findByOrderId(orderId);
    }
} 