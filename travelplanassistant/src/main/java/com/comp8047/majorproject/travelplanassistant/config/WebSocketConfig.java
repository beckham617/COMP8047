package com.comp8047.majorproject.travelplanassistant.config;

import com.comp8047.majorproject.travelplanassistant.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 50)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        // SockJS fallback endpoint
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(@NonNull ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                
                if (accessor != null) {
                    // Handle CONNECT command for initial authentication
                    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                        String authToken = accessor.getFirstNativeHeader("Authorization");
                        if (authToken != null && authToken.startsWith("Bearer ")) {
                            String token = authToken.substring(7);
                            try {
                                String username = extractUsernameFromToken(token);
                                if (username != null) {
                                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                                    UsernamePasswordAuthenticationToken auth = 
                                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                                    accessor.setUser(auth);
                                    SecurityContextHolder.getContext().setAuthentication(auth);
                                }
                            } catch (Exception e) {
                                // Invalid token, continue without authentication
                            }
                        }
                    }
                    // Handle MESSAGE commands - preserve authentication from session or re-authenticate
                    else if (StompCommand.SEND.equals(accessor.getCommand())) {
                        UserDetails userDetails = null;
                        
                        // Try to get user from session first
                        if (accessor.getUser() instanceof UsernamePasswordAuthenticationToken) {
                            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) accessor.getUser();
                            if (auth != null && auth.getPrincipal() instanceof UserDetails) {
                                userDetails = (UserDetails) auth.getPrincipal();
                            }
                        } 
                        // Fallback: check for Authorization header in message
                        else {
                            String authToken = accessor.getFirstNativeHeader("Authorization");
                            if (authToken != null && authToken.startsWith("Bearer ")) {
                                String token = authToken.substring(7);
                                try {
                                    String username = extractUsernameFromToken(token);
                                    if (username != null) {
                                        userDetails = userDetailsService.loadUserByUsername(username);
                                    }
                                } catch (Exception e) {
                                    // Invalid token, continue without authentication
                                }
                            }
                        }
                        
                        // Store user in message header for access in handler
                        if (userDetails != null) {
                            accessor.setHeader("user", userDetails);
                            UsernamePasswordAuthenticationToken auth = 
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        }
                    }
                }
                return message;
            }
        });
    }

    private String extractUsernameFromToken(String token) {
        try {
            return jwtTokenUtil.extractUsername(token);
        } catch (Exception e) {
            return null;
        }
    }
}


