package com.devhive.DevHive.websocket;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import com.devhive.DevHive.security.JwtUtil;
import com.devhive.DevHive.security.UserPrincipal;
import com.devhive.DevHive.service.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StompAuthorizationInterceptor implements ChannelInterceptor {

    private static final Pattern USER_NOTIFICATIONS =
            Pattern.compile("^/topic/notifications/(\\d+)$");

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            throw new MessageDeliveryException(message, "Invalid STOMP message");
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            authenticate(accessor, message);
        } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            authorizeSubscription(accessor, message);
        } else if (StompCommand.SEND.equals(accessor.getCommand())) {
            throw new MessageDeliveryException(message, "Client messages are not supported");
        }
        return message;
    }

    private void authenticate(StompHeaderAccessor accessor, Message<?> message) {
        List<String> headers = accessor.getNativeHeader("Authorization");
        if (headers == null || headers.isEmpty() || !headers.get(0).startsWith("Bearer ")) {
            throw new MessageDeliveryException(message, "A bearer token is required");
        }

        String token = headers.get(0).substring(7).trim();
        try {
            String email = jwtUtil.extractEmail(token);
            if (!jwtUtil.validateToken(token, email)) {
                throw new MessageDeliveryException(message, "Invalid or expired bearer token");
            }
            UserPrincipal principal = userDetailsService.loadUserByUsername(email);
            accessor.setUser(new UsernamePasswordAuthenticationToken(
                    principal, null, principal.getAuthorities()));
        } catch (MessageDeliveryException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new MessageDeliveryException(message, "Invalid or expired bearer token", exception);
        }
    }

    private void authorizeSubscription(StompHeaderAccessor accessor, Message<?> message) {
        if (!(accessor.getUser() instanceof UsernamePasswordAuthenticationToken authentication)
                || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new MessageDeliveryException(message, "Authentication is required");
        }
        Matcher destination = USER_NOTIFICATIONS.matcher(accessor.getDestination() == null
                ? "" : accessor.getDestination());
        if (!destination.matches() || !principal.getUserId().toString().equals(destination.group(1))) {
            throw new MessageDeliveryException(message, "You may only subscribe to your own notifications");
        }
    }
}
