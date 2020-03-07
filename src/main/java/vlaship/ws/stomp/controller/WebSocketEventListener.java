package vlaship.ws.stomp.controller;

import vlaship.ws.stomp.model.NotificationPrincipal;
import vlaship.ws.stomp.model.NotificationUsersMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private static final String BAD_AUTH = "bad auth";
    private final NotificationUsersMap notificationUsersMap;

    @EventListener
    public void handleWebSocketConnectListener(final SessionConnectedEvent event) {

        final NotificationPrincipal principal = (NotificationPrincipal) getPrincipal(event);

        if (principal != null) {
            notificationUsersMap.add(principal);
            log.info("user connect {}", principal.getName());
        } else {
            log.error(BAD_AUTH);
            throw new IllegalStateException(BAD_AUTH);
        }
    }

    private Principal getPrincipal(final AbstractSubProtocolEvent event) {
        return (Principal) event.getMessage().getHeaders().get("simpUser");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(final SessionDisconnectEvent event) {

        final NotificationPrincipal principal = (NotificationPrincipal) getPrincipal(event);

        if (principal != null) {
            notificationUsersMap.remove(principal);
            log.info("user disconnect {}", principal.getName());
        } else {
            log.error(BAD_AUTH);
            throw new IllegalStateException(BAD_AUTH);
        }
    }
}
