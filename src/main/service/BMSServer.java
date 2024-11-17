package main.service;

import java.util.*;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import com.google.gson.Gson;
import main.service.BMSMain;
import main.service.*;
import main.models.*;

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
                    Banquet banquet = parseBanquetFromData(data);
                    try {
                        Banquet createdBanquet = backendService.createBanquet(banquet);
                        response.put("status", "success");
                        response.put("banquet", createdBanquet);
                    } catch (Exception e) {
                        response.put("status", "error");
                        response.put("message", e.getMessage());
                    }
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

    // Helper method to parse a Banquet object from the data map
    private Banquet parseBanquetFromData(Map<String, Object> data) {
        Banquet banquet = new Banquet();
        banquet.setBIN(data.get("BIN") != null ? ((Number) data.get("BIN")).intValue() : 0);
        banquet.setName((String) data.get("Name"));
        banquet.setDate((String) data.get("Date"));
        banquet.setTime((String) data.get("Time"));
        banquet.setAddress((String) data.get("Address"));
        banquet.setLocation((String) data.get("Location"));
        banquet.setContactFirstName((String) data.get("FirstName"));
        banquet.setContactLastName((String) data.get("LastName"));
        banquet.setAvailable((String) data.get("Available"));
        banquet.setQuota(data.get("Quota") != null ? ((Number) data.get("Quota")).intValue() : 0);
        return banquet;
    }

    // Implement parsing methods for other entities as needed
}