package com.messaging.healthtrack_messaging.controllers;


import com.messaging.healthtrack_messaging.controllers.handlers.ResourceNotFoundException;
import com.messaging.healthtrack_messaging.dtos.MessageDTO;
import com.messaging.healthtrack_messaging.services.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
public class MessageController {
    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping(value = "/messageSent")
    @SendTo(value = "/topic/sendMessage")
    public MessageDTO sendMessage(@Valid @RequestBody MessageDTO messageDTO) {

        return messageService.saveMessage(messageDTO);
    }

    @GetMapping(value = "/message/{id}")
    public ResponseEntity<MessageDTO> getMessageById(@PathVariable("id") UUID messageId){
        try {
            MessageDTO messageDTO = messageService.findMessageById(messageId);

            return new ResponseEntity<>(messageDTO, HttpStatus.OK);
        }
        catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, e.getStatus());
        }
    }

    @GetMapping(value = "/conversation/{receiver}/{sender}")
    public ResponseEntity<List<MessageDTO>> getConversation(@PathVariable("receiver") UUID receiver, @PathVariable("sender") UUID sender) {
        List<MessageDTO> messageDTOList = messageService.findConversation(receiver, sender);

        return new ResponseEntity<>(messageDTOList, HttpStatus.OK);
    }

    @GetMapping( value = "/messagesRead/{receiver}/{sender}")
    public ResponseEntity<String> markMessagesAsRead(@PathVariable("receiver") UUID receiver, @PathVariable("sender") UUID sender) {
        try {
            String readMessage = messageService.markMessagesAsRead(receiver, sender);

            return new ResponseEntity<>(readMessage, HttpStatus.OK);
        }
        catch (ResourceNotFoundException e) {
            return new ResponseEntity<>("Conversation could not be read!", e.getStatus());
        }
    }

    @GetMapping(value = "/conversationOf/{userId}")
    public ResponseEntity<List<UUID>> getUsersWhoSpokeWithUserId(@PathVariable("userId") UUID userId) {
        List<UUID> usersIds = messageService.findAllUsersWhoSpokeWithUserId(userId);

        return new ResponseEntity<>(usersIds, HttpStatus.OK);
    }

    @GetMapping(value = "/unreadMessages/{userId}")
    public ResponseEntity<List<MessageDTO>> getUnreadMessages(@PathVariable("userId") UUID userId) {
        List<MessageDTO> messageDTOList = messageService.findUnreadMessages(userId);

        return new ResponseEntity<>(messageDTOList, HttpStatus.OK);
    }
}
