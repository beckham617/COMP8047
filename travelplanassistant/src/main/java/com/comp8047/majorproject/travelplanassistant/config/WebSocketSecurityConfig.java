package com.comp8047.majorproject.travelplanassistant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;

@Configuration
public class WebSocketSecurityConfig {
    
    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager() {
        return (authentication, context) -> {
            // Allow all for now, or implement custom logic
            return new AuthorizationDecision(true);
        };
    }
}
