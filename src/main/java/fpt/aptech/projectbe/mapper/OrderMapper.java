package fpt.aptech.projectbe.mapper;

import fpt.aptech.projectbe.dto.*;
import fpt.aptech.projectbe.entites.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {
    
    public OrderDTO toDTO(Order order) {
        if (order == null) {
            return null;
        }

        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUser().getId());
        dto.setTotal(order.getTotal());
        dto.setStatus(order.getStatus());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        
        dto.setReceiverName(order.getReceiverName());
        dto.setReceiverEmail(order.getReceiverEmail());
        dto.setReceiverPhone(order.getReceiverPhone());
        dto.setReceiverAddress(order.getReceiverAddress());
        
        if (order.getOrderItems() != null) {
            List<OrderItemDTO> orderItemDTOs = order.getOrderItems().stream()
                .map(this::toOrderItemDTO)
                .collect(Collectors.toList());
            dto.setOrderItems(orderItemDTOs);
        }

        return dto;
    }

    public OrderItemDTO toOrderItemDTO(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }

        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(orderItem.getId());
        dto.setOrderId(orderItem.getOrder().getId());
        dto.setProductId(orderItem.getProduct().getId());
        dto.setSizeId(orderItem.getSize().getId());
        dto.setQuantity(orderItem.getQuantity());
        dto.setPrice(orderItem.getPrice());

        // Gán ProductDTO
        Product product = orderItem.getProduct();
        if (product != null) {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setId(product.getId());
            productDTO.setName(product.getName());
            productDTO.setPrice(product.getPrice());
            List<ProductImage> images = product.getProductImages();
            if (images != null && !images.isEmpty()) {
                List<String> imageUrls = images.stream()
                        .map(ProductImage::getImageUrl) // hoặc getPath(), tuỳ theo field bạn có
                        .collect(Collectors.toList());
                productDTO.setImageUrls(imageUrls);
            }
            dto.setProduct(productDTO);
        }

        // Gán SizeDTO
        Size size = orderItem.getSize();
        if (size != null) {
            SizeDTO sizeDTO = new SizeDTO();
            sizeDTO.setId(size.getId());
            sizeDTO.setName(size.getName());
            dto.setSize(sizeDTO);
        }

        return dto;
    }


    public PaymentDTO toPaymentDTO(Payment payment) {
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
} 