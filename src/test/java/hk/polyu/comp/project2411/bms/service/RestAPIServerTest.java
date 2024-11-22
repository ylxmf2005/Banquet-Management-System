package hk.polyu.comp.project2411.bms.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class RestAPIServerTest {

    private static final String BASE_URI = "http://localhost:2411"; 

    private Client client;

    @BeforeEach
    public void setUp() {
        client = ClientBuilder.newClient();
    }

    @AfterEach
    public void tearDown() {
        client.close();
    }

    @Test
    public void testCreateDatabase() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("clearIfExist", true);

        Response response = client.target(BASE_URI)
                .path("/initDatabase")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonObject.toString(), MediaType.APPLICATION_JSON));

        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        String jsonResponse = response.readEntity(String.class);
        System.out.println(jsonResponse);


        response.close();
    }

    @Test
    public void testCreateBanquet() {
        JsonObject banquetData = new JsonObject();
        banquetData.addProperty("Name", "Test Banquet");
        banquetData.addProperty("DateTime", "2024-12-31 18:00:00");
        banquetData.addProperty("Address", "Test Address");
        banquetData.addProperty("Location", "Test Location");
        banquetData.addProperty("FirstName", "John");
        banquetData.addProperty("LastName", "Doe");
        banquetData.addProperty("Available", "Y");
        banquetData.addProperty("Quota", 100);

        Response response = client.target(BASE_URI)
                .path("/createBanquet")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(banquetData.toString(), MediaType.APPLICATION_JSON));

        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        String jsonResponse = response.readEntity(String.class);
        System.out.println("Create Banquet Response: " + jsonResponse);


        response.close();
    }

    @Test
    public void testUpdateBanquet() {
        JsonObject banquetData = new JsonObject();
        banquetData.addProperty("BIN", 1); 
        banquetData.addProperty("Name", "Updated Banquet Name");
        banquetData.addProperty("DateTime", "2024-12-31 20:00:00");
        banquetData.addProperty("Address", "Updated Address");
        banquetData.addProperty("Location", "Updated Location");
        banquetData.addProperty("FirstName", "Jane");
        banquetData.addProperty("LastName", "Doe");
        banquetData.addProperty("Available", "N");
        banquetData.addProperty("Quota", 80);

        Response response = client.target(BASE_URI)
                .path("/updateBanquet")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(banquetData.toString(), MediaType.APPLICATION_JSON));

        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        String jsonResponse = response.readEntity(String.class);
        System.out.println("Update Banquet Response: " + jsonResponse);

        response.close();
    }
    
}