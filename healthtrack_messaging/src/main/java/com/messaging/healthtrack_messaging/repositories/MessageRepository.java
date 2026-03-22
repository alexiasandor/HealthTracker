package com.messaging.healthtrack_messaging.repositories;

import com.messaging.healthtrack_messaging.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findAllByReceiverId(UUID receiver);
    List<Message> findAllBySenderId(UUID sender);
}
