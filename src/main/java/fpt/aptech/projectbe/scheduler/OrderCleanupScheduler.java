package fpt.aptech.projectbe.scheduler;

import fpt.aptech.projectbe.entites.Order;
import fpt.aptech.projectbe.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class OrderCleanupScheduler {
    private static final Logger logger = LoggerFactory.getLogger(OrderCleanupScheduler.class);

    @Autowired
    private OrderService orderService;

    // Chạy mỗi 5 phút
    @Scheduled(fixedRate = 300000)
    public void cancelExpiredOrders() {
        logger.info("Bắt đầu kiểm tra đơn hàng hết hạn...");
        
        try {
            // Lấy danh sách đơn hàng có payment_status = "Chờ thanh toán" và đã hết hạn
            List<Order> expiredOrders = orderService.findByPaymentStatusAndExpiredAtBefore("Chờ thanh toán", new Date());
            
            if (expiredOrders.isEmpty()) {
                logger.info("Không có đơn hàng nào hết hạn");
                return;
            }

            logger.info("Tìm thấy {} đơn hàng hết hạn", expiredOrders.size());
            
            // Cập nhật trạng thái các đơn hàng hết hạn
            for (Order order : expiredOrders) {
                order.setStatus("Đã hủy");
                order.setPaymentStatus("Đã hủy");
                orderService.update(order);
                logger.info("Đã hủy đơn hàng: {}", order.getOrderCode());
            }
            
            logger.info("Hoàn thành việc hủy đơn hàng hết hạn");
        } catch (Exception e) {
            logger.error("Lỗi khi hủy đơn hàng hết hạn: {}", e.getMessage());
        }
    }
} 