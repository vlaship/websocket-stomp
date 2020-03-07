package vlaship.ws.stomp;

import vlaship.ws.stomp.model.NotificationUsersMap;
import vlaship.ws.stomp.model.RealTimeNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class Trigger {

    private final SimpMessageSendingOperations messagingTemplate;
    private final NotificationUsersMap notificationUsersMap;

    @GetMapping("/push/{userId}/{badgeId}")
    public ResponseEntity<String> push(@PathVariable String userId, @PathVariable String badgeId) {
        notificationUsersMap.getSessionsSet(userId).forEach(sessionId -> send(sessionId, badgeId, userId));
        return ResponseEntity.ok().build();
    }

    private void send(final String sessionId, final String badgeId, final String userId) {
        final SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);

        messagingTemplate.convertAndSendToUser(
                sessionId,
                "/queue/reply",
                RealTimeNotification.builder().content(badgeId).build(),
                headerAccessor.getMessageHeaders()
        );
        log.info("userId {} sessionId {} badgeId {}", userId, sessionId, badgeId);
    }

}
