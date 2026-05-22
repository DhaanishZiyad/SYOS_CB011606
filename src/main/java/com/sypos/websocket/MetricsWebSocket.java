package com.sypos.websocket;

import com.sypos.concurrency.CheckoutQueueManager;
import com.sypos.concurrency.SystemMetrics;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/metrics")
public class MetricsWebSocket {

    private static final Set<Session> sessions =
            ConcurrentHashMap.newKeySet();

    @OnOpen
    public void onOpen(Session session) {

        sessions.add(session);

        System.out.println(
                "WebSocket connected: "
                        + session.getId()
        );

        sendMetrics(session);
    }

    @OnClose
    public void onClose(Session session) {

        sessions.remove(session);

        System.out.println(
                "WebSocket disconnected: "
                        + session.getId()
        );
    }

    public static void broadcast() {

        for (Session session : sessions) {

            sendMetrics(session);
        }
    }

    private static void sendMetrics(Session session) {

        try {

            String json =
                    "{"

                            + "\"queueSize\":"
                            + CheckoutQueueManager
                            .getQueue()
                            .size()
                            + ","

                            + "\"httpRequests\":"
                            + SystemMetrics
                            .totalHttpRequests
                            .get()
                            + ","

                            + "\"processedBills\":"
                            + SystemMetrics
                            .totalProcessedBills
                            .get()
                            + ","

                            + "\"queuedBills\":"
                            + SystemMetrics
                            .totalQueuedBills
                            .get()

                            + "}";

            synchronized (session) {

                session.getBasicRemote()
                        .sendText(json);
            }

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}