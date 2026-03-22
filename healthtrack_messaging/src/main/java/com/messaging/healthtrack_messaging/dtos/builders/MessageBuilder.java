package com.messaging.healthtrack_messaging.dtos.builders;

import com.messaging.healthtrack_messaging.dtos.MessageDTO;
import com.messaging.healthtrack_messaging.entities.Message;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MessageBuilder {
    public static MessageDTO mapToMessageDTO(Message message) {
        return MessageDTO.builder()
                .messageId(message.getMessageId())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .messageContent(message.getMessageContent())
                .timestamp(message.getTimestamp())
                .messageRead(message.isMessageRead())
                .build();
    }

    public static Message mapToMessageEntity(MessageDTO messageDTO) {
        return Message.builder()
                .messageId(messageDTO.getMessageId())
                .senderId(messageDTO.getSenderId())
                .receiverId(messageDTO.getReceiverId())
                .messageContent(messageDTO.getMessageContent())
                .timestamp(messageDTO.getTimestamp())
                .messageRead(messageDTO.isMessageRead())
                .build();
    }
}
