package com.group18.chessgame.config;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ActiveUserListener implements HttpSessionListener {
    private static final AtomicInteger activeSession = new AtomicInteger(0);

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        activeSession.incrementAndGet();
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        activeSession.decrementAndGet();
    }

    public static int getActiveSessionCount() {
        return activeSession.get();
    }
}
