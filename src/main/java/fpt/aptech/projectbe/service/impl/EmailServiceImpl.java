package fpt.aptech.projectbe.service.impl;

import fpt.aptech.projectbe.entites.Order;
import fpt.aptech.projectbe.entites.OrderItem;
import fpt.aptech.projectbe.service.EmailService;
import fpt.aptech.projectbe.service.OrderItemService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private OrderItemService orderItemService;

    @Override
    public void sendOrderConfirmationEmail(Order order) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(order.getReceiverEmail());
            helper.setSubject("Xác nhận đơn hàng #" + order.getOrderCode());
            
            // Get order items
            List<OrderItem> orderItems = orderItemService.findByOrder(order);
            
            // Format currency
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            
            // Build HTML content
            StringBuilder htmlContent = new StringBuilder();
            htmlContent.append("<html><body>");
            htmlContent.append("<h2>Cảm ơn bạn đã đặt hàng!</h2>");
            htmlContent.append("<p>Xin chào ").append(order.getReceiverName()).append(",</p>");
            htmlContent.append("<p>Đơn hàng của bạn đã được xác nhận với mã đơn hàng: <strong>").append(order.getOrderCode()).append("</strong></p>");
            
            htmlContent.append("<h3>Chi tiết đơn hàng:</h3>");
            htmlContent.append("<table border='1' cellpadding='5' style='border-collapse: collapse;'>");
            htmlContent.append("<tr><th>Sản phẩm</th><th>Kích cỡ</th><th>Số lượng</th><th>Đơn giá</th><th>Thành tiền</th></tr>");
            
            for (OrderItem item : orderItems) {
                htmlContent.append("<tr>");
                htmlContent.append("<td>").append(item.getProduct().getName()).append("</td>");
                htmlContent.append("<td>").append(item.getSize().getName()).append("</td>");
                htmlContent.append("<td>").append(item.getQuantity()).append("</td>");
                htmlContent.append("<td>").append(currencyFormatter.format(item.getPrice())).append("</td>");
                
                // Convert Integer quantity to BigDecimal before multiplying
                BigDecimal itemTotal = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
                htmlContent.append("<td>").append(currencyFormatter.format(itemTotal)).append("</td>");
                
                htmlContent.append("</tr>");
            }
            
            htmlContent.append("</table>");
            
            htmlContent.append("<p><strong>Tổng cộng:</strong> ").append(currencyFormatter.format(order.getTotal())).append("</p>");
            
            htmlContent.append("<h3>Thông tin giao hàng:</h3>");
            htmlContent.append("<p>Người nhận: ").append(order.getReceiverName()).append("</p>");
            htmlContent.append("<p>Địa chỉ: ").append(order.getReceiverAddress()).append("</p>");
            htmlContent.append("<p>Số điện thoại: ").append(order.getReceiverPhone()).append("</p>");
            
            htmlContent.append("<p>Phương thức thanh toán: ").append(order.getPaymentStatus()).append("</p>");
            
            htmlContent.append("<p>Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ với chúng tôi.</p>");
            htmlContent.append("<p>Trân trọng,<br>Đội ngũ hỗ trợ khách hàng</p>");
            htmlContent.append("</body></html>");
            
            helper.setText(htmlContent.toString(), true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            // Log the error but don't throw exception to prevent order processing from failing
            System.err.println("Error sending confirmation email: " + e.getMessage());
        }
    }
} 