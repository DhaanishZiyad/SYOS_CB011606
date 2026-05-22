package com.sypos.websocket;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/inventory")
public class InventoryWebSocket {

    private static final Set<Session> sessions =
            ConcurrentHashMap.newKeySet();

    @OnOpen
    public void onOpen(Session session) {

        sessions.add(session);

        System.out.println(
                "Inventory websocket connected: "
                        + session.getId()
        );
    }

    @OnClose
    public void onClose(Session session) {

        sessions.remove(session);
    }

    public static void broadcast() {

        for (Session session : sessions) {

            try {

                synchronized (session) {

                    session.getBasicRemote()
                            .sendText("inventory-updated");
                }

            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }
}