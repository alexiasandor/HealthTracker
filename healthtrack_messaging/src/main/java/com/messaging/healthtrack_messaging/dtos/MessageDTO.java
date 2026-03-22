package com.messaging.healthtrack_messaging.dtos;

import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MessageDTO {
    private UUID messageId;
    private UUID senderId;
    private UUID receiverId;
    private String messageContent;
    private Timestamp timestamp;
    private boolean messageRead;
}
