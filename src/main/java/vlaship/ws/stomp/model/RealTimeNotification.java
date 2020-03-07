package vlaship.ws.stomp.model;

import lombok.Builder;
import lombok.Value;

import java.io.Serializable;

@Value
@Builder
public class RealTimeNotification implements Serializable {
    String content;
}
