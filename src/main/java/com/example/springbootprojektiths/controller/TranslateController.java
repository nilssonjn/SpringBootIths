package com.example.springbootprojektiths.controller;

import com.example.springbootprojektiths.entity.Message;
import com.example.springbootprojektiths.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class TranslateController {

    @Autowired
    MessageRepository messageRepository;
    @RequestMapping(value = "/translate/{messageId}", method = RequestMethod.GET)
    @ResponseBody
    String translateMessage(@PathVariable Long messageId) {
        // Retrieve the message from the repository
        Optional<Message> optionalMessage = messageRepository.findById(messageId);
        System.out.println("TESTTEST----------------------------------------------------");
        if (optionalMessage.isPresent()) {
            Message message = optionalMessage.get();

            // Prepare the request body for translation
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("q", message.getChatMessage());
            requestBody.put("source", "sv");
            requestBody.put("target", "en");

            // Make HTTP POST request to translation service
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://localhost:5000/translate", requestBody, String.class);

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                // Parse and return the translated message
                String translatedMessage = responseEntity.getBody();
                message.setChatMessage(translatedMessage);
                System.out.println(message.getChatMessage());

                String chatMessage = message.getChatMessage();
                String[] parts = chatMessage.split("\\{");
                String[] translatedParts = parts[1].split(":");
                String[] textParts = translatedParts[1].split("\"");
                String translatedText = textParts[1];
                
                return translatedText;
               // return message.getChatMessage();
            } else {
                // Handle error response from translation service
                // You may want to throw an exception or return an appropriate error message
                return null; // or throw new TranslationFailedException();
            }
        } else {
            // Handle the case where the message with the given ID is not found
            // You may want to return an appropriate error response
            return null; // or throw new MessageNotFoundException();
        }
    }



}