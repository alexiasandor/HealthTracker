package com.messaging.healthtrack_messaging.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

import java.sql.Timestamp;
@Entity
@Table(name = "message_t")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

public class Message {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID messageId;
    @Column(name = "sender_id")
    private UUID senderId;
    @Column(name = "receiver_id")
    private UUID receiverId;
    @Column(name = "message_content")
    private String messageContent;
    @Column(name = "timestamp")
    private Timestamp timestamp;
    @Column(name = "message_read")
    private boolean messageRead;
}
