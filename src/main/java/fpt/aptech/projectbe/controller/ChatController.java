package fpt.aptech.projectbe.controller;

import fpt.aptech.projectbe.dto.MessageDTO;
import fpt.aptech.projectbe.entites.Message;
import fpt.aptech.projectbe.entites.User;
import fpt.aptech.projectbe.repository.MessageRepository;
import fpt.aptech.projectbe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MessageDTO messageDTO) {
        try {
            logger.info("Received message: {}", messageDTO);

            // Save message
            Message message = new Message();
            User sender = userRepository.findById(messageDTO.getSenderId())
                    .orElseThrow(() -> new RuntimeException("Sender not found"));
            User receiver = userRepository.findById(messageDTO.getReceiverId())
                    .orElseThrow(() -> new RuntimeException("Receiver not found"));

            message.setSender(sender);
            message.setReceiver(receiver);
            message.setMessage(messageDTO.getMessage());

            // Kiểm tra và gán localId nếu có từ client (Quan trọng cho Optimistic UI)
            if (messageDTO.getLocalId() != null) {
                // Bạn cần thêm trường `localId` vào Entity `Message` và DTO
                // hoặc xử lý theo cách khác nếu không muốn lưu localId vào DB
                // Ví dụ: message.setLocalId(messageDTO.getLocalId());
            }

            Message savedMessage = messageRepository.save(message);
            logger.info("Saved message: {}", savedMessage);

            // Convert to DTO
            MessageDTO savedMessageDTO = convertToDTO(savedMessage);

            // Gửi lại localId cho client nếu có để client có thể khớp tin nhắn optimistic
            savedMessageDTO.setLocalId(messageDTO.getLocalId()); // Đảm bảo DTO có setter cho localId

            // Gửi tin nhắn đến người nhận (admin)
            String receiverDestination = "/user/" + messageDTO.getReceiverId() + "/queue/messages";
            logger.info("Sending message to receiver destination: {}", receiverDestination);
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(messageDTO.getReceiverId()),
                    "/queue/messages",
                    savedMessageDTO
            );

            // Gửi tin nhắn trở lại cho người gửi (user)
            // Điều này đảm bảo tin nhắn xuất hiện ngay lập tức cho người gửi
            String senderDestination = "/user/" + messageDTO.getSenderId() + "/queue/messages";
            logger.info("Sending message back to sender destination: {}", senderDestination);
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(messageDTO.getSenderId()),
                    "/queue/messages",
                    savedMessageDTO
            );

        } catch (Exception e) {
            logger.error("Error sending message", e);
            throw e;
        }
    }

    @GetMapping("/messages/{userId}/{adminId}")
    public List<MessageDTO> getMessages(@PathVariable int userId, @PathVariable int adminId) {
        logger.info("Getting messages between user {} and admin {}", userId, adminId);
        return messageRepository.findMessagesBetweenUsers(userId, adminId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/users/{adminId}")
    public List<User> getUsersChattingWithAdmin(@PathVariable int adminId) {
        logger.info("Getting users chatting with admin {}", adminId);
        List<Integer> userIds = messageRepository.findUsersChattingWithAdmin(adminId);
        return userRepository.findAllById(userIds);
    }

    @GetMapping("/unread/{userId}")
    public long getUnreadMessageCount(@PathVariable int userId) {
        logger.info("Getting unread message count for user {}", userId);
        return messageRepository.countUnreadMessages(userId);
    }

    @PostMapping("/read/{userId}/{senderId}")
    public void markMessagesAsRead(@PathVariable int userId, @PathVariable int senderId) {
        logger.info("Marking messages as read between user {} and sender {}", userId, senderId);
        messageRepository.markMessagesAsRead(userId, senderId);
    }

    @GetMapping("/last-message/{userId}/{adminId}")
    public MessageDTO getLastMessage(@PathVariable int userId, @PathVariable int adminId) {
        logger.info("Getting last message between user {} and admin {}", userId, adminId);
        List<Message> messages = messageRepository.findMessagesBetweenUsers(userId, adminId);
        if (messages.isEmpty()) {
            return null;
        }
        return convertToDTO(messages.get(messages.size() - 1));
    }

    @GetMapping("/chat-users/{adminId}")
    public List<User> getChatUsers(@PathVariable int adminId) {
        logger.info("Getting all users for admin {}", adminId);
        return userRepository.findByRoleId(2); // Get all users (role_id = 2)
    }

    private MessageDTO convertToDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setSenderId(message.getSender().getId());
        dto.setSenderName(message.getSender().getUsername());
        dto.setReceiverId(message.getReceiver().getId());
        dto.setReceiverName(message.getReceiver().getUsername());
        dto.setMessage(message.getMessage());
        dto.setSentAt(message.getSentAt());
        dto.setIsRead(message.getIsRead());
        return dto;
    }
} 