package com.group18.chessgame.config;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.stereotype.Component;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ActiveUserListener implements HttpSessionListener {
    private static final Set<String> activeSessions = ConcurrentHashMap.newKeySet();

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        activeSessions.add(se.getSession().getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        activeSessions.remove(se.getSession().getId());
    }

    public static int getActiveSessionCount() {
        return activeSessions.size();
    }

    public static void addSession(String sessionId) {
        if (sessionId != null) {
            activeSessions.add(sessionId);
        }
    }
}
