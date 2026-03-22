package com.messaging.healthtrack_messaging.services;

import com.messaging.healthtrack_messaging.controllers.handlers.ResourceNotFoundException;
import com.messaging.healthtrack_messaging.dtos.MessageDTO;
import com.messaging.healthtrack_messaging.dtos.builders.MessageBuilder;
import com.messaging.healthtrack_messaging.entities.Message;
import com.messaging.healthtrack_messaging.repositories.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Timestamp;
import java.util.*;

@Service
public class MessageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);
    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Transactional
    public MessageDTO saveMessage(MessageDTO messageDTO) {
        Message message = MessageBuilder.mapToMessageEntity(messageDTO);
        message.setMessageRead(false);

        message = messageRepository.save(message);
        LOGGER.debug("Message with id \"{}\" was inserted in db!", message.getMessageId());

        return MessageBuilder.mapToMessageDTO(message);
    }

    @Transactional
    public MessageDTO findMessageById(UUID messageId) throws ResourceNotFoundException {
        Optional<Message> optionalMessage = messageRepository.findById(messageId);

        if(optionalMessage.isEmpty()) {
            LOGGER.error("No message with id {} was found!", messageId);
            throw new ResourceNotFoundException(Message.class.getSimpleName() + " with id: " + messageId);
        }

        return MessageBuilder.mapToMessageDTO(optionalMessage.get());
    }

    @Transactional
    public List<MessageDTO> findConversation(UUID receiver, UUID sender) {
        List<Message> messageList = messageRepository.findAll();
        List<MessageDTO> messageDTOList = new ArrayList<>();

        for(Message message: messageList) {
            if((message.getReceiverId().equals(receiver) && message.getSenderId().equals(sender)) || (message.getReceiverId().equals(sender) && message.getSenderId().equals(receiver))) {
                messageDTOList.add(MessageBuilder.mapToMessageDTO(message));
            }
        }

        messageDTOList.sort(Comparator.comparing(MessageDTO::getTimestamp).reversed());
        LOGGER.debug("Conversation between user with id \"{}\" and user with id \"{}\" received!", receiver, sender);

        return messageDTOList;
    }

    @Transactional
    public String markMessagesAsRead(UUID receiver, UUID sender) throws ResourceNotFoundException{
        List<Message> responseReceiver = messageRepository.findAllByReceiverId(receiver);
        if(responseReceiver.isEmpty()) {
            LOGGER.error("No message with receiver id \"{}\" was found!", receiver);
            throw new ResourceNotFoundException(Message.class.getSimpleName() + " receiver with id: " + receiver);
        }

        List<Message> responseSender = messageRepository.findAllBySenderId(sender);
        if(responseSender.isEmpty()) {
            LOGGER.error("No message with sender id \"{}\" was found!", sender);
            throw new ResourceNotFoundException(Message.class.getSimpleName() + " receiver with id: " + sender);
        }

        List<Message> messageList = messageRepository.findAll();

        for(Message message: messageList) {
            if(!message.isMessageRead()) {
                if(message.getReceiverId().equals(receiver) && message.getSenderId().equals(sender)) {
                    message.setMessageRead(true);
                    messageRepository.save(message);
                }
            }
        }

        LOGGER.debug("Conversation between user with id \"{}\" and user with id \"{}\" was read!", receiver, sender);

        return "Conversation read!";
    }

    @Transactional
    public List<UUID> findAllUsersWhoSpokeWithUserId(UUID userId) {
        List<Message> messageList = messageRepository.findAll();
        List<UUID> userIds = new ArrayList<>();
        messageList.sort(Comparator.comparing(Message::getTimestamp).reversed());

        for(Message message: messageList) {
            if(message.getReceiverId().equals(userId) || message.getSenderId().equals(userId)) {
                UUID otherUserId;
                if(message.getReceiverId().equals(userId)) {
                    otherUserId = message.getSenderId();
                }
                else {
                    otherUserId = message.getReceiverId();
                }

                if(!userIds.contains(otherUserId)) {
                    userIds.add(otherUserId);
                }
            }
        }

        return userIds;
    }

    @Transactional
    public List<MessageDTO> findUnreadMessages(UUID userId) {
        List<Message> messageList = messageRepository.findAll();
        List<MessageDTO> messageDTOList = new ArrayList<>();
        messageList.sort(Comparator.comparing(Message::getTimestamp).reversed());

        for(Message message: messageList) {
            if(message.getReceiverId().equals(userId)) {
                if(!message.isMessageRead()) {
                    messageDTOList.add(MessageBuilder.mapToMessageDTO(message));
                }
            }
        }

        return messageDTOList;
    }
}
