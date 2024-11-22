package hk.polyu.comp.project2411.bms.service;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import hk.polyu.comp.project2411.bms.exceptions.AuthenticationException;
import hk.polyu.comp.project2411.bms.model.Account;
import hk.polyu.comp.project2411.bms.model.Banquet;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
public class BMSRestController {

    private BMSMain bmsMain = new BMSMain();
    private static Gson gson = new Gson();

    @POST
    @Path("/initDatabase")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createDatabase(String clearIfExists) {
        Map<String, Object> response = new HashMap<>();
        try {
            JsonObject jsonObject = gson.fromJson(clearIfExists, JsonObject.class);
            boolean clearIfExist = jsonObject.get("clearIfExist").getAsBoolean();
            boolean initResult = bmsMain.initDatabase(clearIfExist);
            response.put("status", initResult ? "success" : "failure");
            String jsonResponse = gson.toJson(response);
            return Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            String jsonResponse = gson.toJson(response);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonResponse).build();
        }
    }

    @POST
    @Path("/createBanquet")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBanquet(String banquetData) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> data = gson.fromJson(banquetData, Map.class);
            Banquet createdBanquet = bmsMain.createBanquet(new Banquet(data));
            response.put("status", "success");
            response.put("banquet", createdBanquet);
            String jsonResponse = gson.toJson(response);
            return Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            String jsonResponse = gson.toJson(response);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonResponse).build();
        }
    }

    @POST
    @Path("/updateBanquet")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBanquet(String banquetData) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> data = gson.fromJson(banquetData, Map.class);
            boolean updateResult = bmsMain.updateBanquet(new Banquet(data));
            response.put("status", updateResult ? "success" : "failure");
            String jsonResponse = gson.toJson(response);
            return Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            String jsonResponse = gson.toJson(response);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonResponse).build();
        }
    }

    
    @POST
    @Path("/authenticateAccount")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticateAccount(String accountData) {
        Map<String, Object> response = new HashMap<>();
        try {
            JsonObject jsonObject = gson.fromJson(accountData, JsonObject.class);
            String email = jsonObject.get("email").getAsString();
            String password = jsonObject.get("password").getAsString();
            Account authenticatedAccount = bmsMain.authenticateAccount(email, password);
            response.put("status", "success");
            response.put("account", authenticatedAccount);
            String jsonResponse = gson.toJson(response);
            return Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();
        } catch (AuthenticationException e) {
            response.put("status", "failure");
            response.put("message", "Authentication failed");
            String jsonResponse = gson.toJson(response);
            return Response.status(Response.Status.UNAUTHORIZED).entity(jsonResponse).build();
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            String jsonResponse = gson.toJson(response);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonResponse).build();
        }
    }
}
