package vlaship.ws.stomp.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
@Component
public class NotificationUsersMap {
    private final ConcurrentHashMap<String, Set<String>> map = new ConcurrentHashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public void add(final NotificationPrincipal principal) {
        lock.writeLock().lock();
        try {
            log.info("add to notification list user: {}", principal);
            if (map.containsKey(principal.getName())) {
                map.get(principal.getName()).add(principal.getSession());
                return;
            }
            map.put(principal.getName(), new HashSet<>());
            map.get(principal.getName()).add(principal.getSession());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void remove(final NotificationPrincipal principal) {
        lock.writeLock().lock();
        try {
            log.info("remove from notification list user: {}", principal);
            final Set<String> sessionsSet = map.get(principal.getName());
            if (sessionsSet == null) {
                return;
            }
            if (sessionsSet.isEmpty()) {
                map.remove(principal.getName());
                return;
            }
            sessionsSet.remove(principal.getSession());
            if (sessionsSet.isEmpty()) {
                map.remove(principal.getName());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Set<String> getSessionsSet(final String userId) {
        lock.readLock().lock();
        try {
            final Set<String> sessions = map.get(userId);
            return sessions != null ? sessions : new HashSet<>();
        } finally {
            lock.readLock().unlock();
        }
    }

}
