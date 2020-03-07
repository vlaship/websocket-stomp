package vlaship.ws.stomp.config;

import lombok.NonNull;
import vlaship.ws.stomp.model.NotificationPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.config.annotation.*;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(final ChannelRegistration channelRegistration) {
        channelRegistration.interceptors(new ChannelInterceptorAdapter() {

            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {

                final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                final StompCommand command = accessor.getCommand();

                if (StompCommand.CONNECT.equals(command)) {
                    String user = accessor.getFirstNativeHeader("userId");
                    String token = accessor.getFirstNativeHeader("token");
                    String sessionId = accessor.getSessionId();
                    if (!StringUtils.isEmpty(user) && !StringUtils.isEmpty(token) && !StringUtils.isEmpty(sessionId)) {
                        final NotificationPrincipal principal = NotificationPrincipal.builder()
                                .name(user)
                                .token(token)
                                .session(sessionId)
                                .build();
                        accessor.setUser(principal);
                        log.info("connect {}", principal);
                    } else {
                        throw new IllegalStateException("Invalid websocket session");
                    }
                }
                return message;
            }
        });
    }

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry registry) {
        registry.setUserDestinationPrefix("/user");
        registry.setApplicationDestinationPrefixes("/app");
    }
}
