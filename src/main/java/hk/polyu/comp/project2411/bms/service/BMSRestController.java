package hk.polyu.comp.project2411.bms.service;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

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

}
