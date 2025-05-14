package fpt.aptech.projectbe.service;

import fpt.aptech.projectbe.entites.Order;

public interface EmailService {
    void sendOrderConfirmationEmail(Order order);
} 