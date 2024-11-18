package com.project2411.bms.service;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.project2411.bms.model.Banquet;

import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;


// WebSocket server endpoint at "/websocket"
@ServerEndpoint("/websocket")
public class BMSServer {

    private BMSMain bmsMain = new BMSMain();
    private static Gson gson = new Gson();

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("New WebSocket session opened: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // Parse the message from the front-end
        Map<String, Object> request = gson.fromJson(message, Map.class);
        String action = (String) request.get("action");
        Map<String, Object> data = (Map<String, Object>) request.get("data");
        Map<String, Object> response = new HashMap<>();

        try {
            switch (action) {
                case "createBanquet":
                    try {
                        Banquet createdBanquet = bmsMain.createBanquet(new Banquet(data));
                        response.put("status", "success");
                        response.put("banquet", createdBanquet);
                    } catch (Exception e) {
                        response.put("status", "error");
                        response.put("message", e.getMessage());
                    }
                    break;
                
                case "updateBanquet":
                    boolean updateResult = bmsMain.updateBanquet(new Banquet(data));
                    response.put("status", updateResult ? "success" : "failure");
                    break;

                // Add other cases for different actions

                default:
                    response.put("status", "error");
                    response.put("message", "Unknown action: " + action);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }

        // Send the response back to the front-end
        String jsonResponse = gson.toJson(response);
        try {
            session.getBasicRemote().sendText(jsonResponse);
        } catch (Exception e) {
            System.err.println("Error sending response: " + e.getMessage());
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("WebSocket session closed: " + session.getId() + ", Reason: " + reason.getReasonPhrase());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error in session " + session.getId() + ": " + throwable.getMessage());
    }

    

    // Implement parsing methods for other entities as needed
}