package com.example.useraccountservice.kafka.service;

import com.example.useraccountservice.kafka.dto.UserRegistrationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // topics
    private static  final String USER_TOPIC = "user-registered-events";

    public void publishedUserRegistrationEvent(UserRegistrationEvent event) {
        try {
            kafkaTemplate.send(USER_TOPIC, event.getEmail(), event);
            log.info("Event sent: {}", event.getEmail());
        } catch (Exception e) {
            log.info("Failed to publish user registered event: {}", e.getMessage());
        }
    }
}
