package vlaship.ws.stomp.model;

import lombok.Builder;
import lombok.Value;

import java.security.Principal;

@Value
@Builder
public class NotificationPrincipal implements Principal {
    String name;
    String token;
    String session;
}
