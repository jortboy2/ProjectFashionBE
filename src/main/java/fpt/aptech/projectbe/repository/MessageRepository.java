package fpt.aptech.projectbe.repository;

import fpt.aptech.projectbe.entites.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    
    @Query("SELECT m FROM Message m WHERE (m.sender.id = :userId AND m.receiver.id = :adminId) " +
           "OR (m.sender.id = :adminId AND m.receiver.id = :userId) " +
           "ORDER BY m.sentAt ASC")
    List<Message> findMessagesBetweenUsers(@Param("userId") int userId, @Param("adminId") int adminId);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver.id = :userId AND m.isRead = false")
    long countUnreadMessages(@Param("userId") int userId);
    
    @Query("SELECT DISTINCT m.sender.id FROM Message m WHERE m.receiver.id = :adminId")
    List<Integer> findUsersChattingWithAdmin(@Param("adminId") int adminId);

    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.isRead = true WHERE " +
            "(m.receiver.id = :userId AND m.sender.id = :senderId) " +
            "OR (m.receiver.id = :senderId AND m.sender.id = :userId)")
    int markMessagesAsRead(@Param("userId") int userId, @Param("senderId") int senderId);
} 