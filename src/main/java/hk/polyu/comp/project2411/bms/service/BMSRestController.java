package hk.polyu.comp.project2411.bms.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import hk.polyu.comp.project2411.bms.exceptions.AuthenticationException;
import hk.polyu.comp.project2411.bms.exceptions.RegistrationException;
import hk.polyu.comp.project2411.bms.exceptions.ValidationException;
import hk.polyu.comp.project2411.bms.model.Account;
import hk.polyu.comp.project2411.bms.model.AttendeeAccount;
import hk.polyu.comp.project2411.bms.model.Banquet;
import hk.polyu.comp.project2411.bms.model.RegistrationResult;
import hk.polyu.comp.project2411.bms.model.Reserve;
import hk.polyu.comp.project2411.bms.model.SearchCriteria;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
public class BMSRestController {

    private static BMSMain bmsMain;

    static {
        try {
            bmsMain = new BMSMain();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }
    
    private static Gson gson = new Gson();

    @POST
    @Path("/initDatabase")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createDatabase(String clearIfExists) {
        System.out.println("Received request at /initDatabase: " + clearIfExists);
        Map<String, Object> response = new HashMap<>();
        try {
            JsonObject jsonObject = gson.fromJson(clearIfExists, JsonObject.class);
            boolean clearIfExist = jsonObject.get("clearIfExist").getAsBoolean();
            boolean initResult = bmsMain.initDatabase(clearIfExist);
            response.put("status", initResult ? "success" : "failure");
            String jsonResponse = gson.toJson(response);
            return Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
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
        System.out.println("Received request at /createBanquet: " + banquetData);
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> data = gson.fromJson(banquetData, Map.class);
            Banquet createdBanquet = bmsMain.createBanquet(new Banquet(data));
            response.put("status", "success");
            response.put("banquet", createdBanquet);
            String jsonResponse = gson.toJson(response);
            return Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            // e.printStackTrace(); 
            System.out.println("Error: " + e.getMessage());
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
        System.out.println("Received request at /updateBanquet: " + banquetData);
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> data = gson.fromJson(banquetData, Map.class);
            boolean updateResult = bmsMain.updateBanquet(new Banquet(data));
            response.put("status", updateResult ? "success" : "failure");
            String jsonResponse = gson.toJson(response);
            return Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
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
        System.out.println("Received request at /authenticateAccount: " + accountData);

        Map<String, Object> response = new HashMap<>();
        try {
            JsonObject jsonObject = gson.fromJson(accountData, JsonObject.class);
            String email = jsonObject.get("email").getAsString();
            String password = jsonObject.get("password").getAsString();
            Account authenticatedAccount = bmsMain.authenticateAccount(email, password);
            response.put("status", "success");
            response.put("user", authenticatedAccount);
            String jsonResponse = gson.toJson(response);
            return Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();
        } catch (AuthenticationException e) {
            System.out.println("Authentication failed: " + e.getMessage());
            response.put("status", "failure");
            response.put("message", "Authentication failed");
            String jsonResponse = gson.toJson(response);
            return Response.status(Response.Status.UNAUTHORIZED).entity(jsonResponse).build();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            response.put("status", "error");
            response.put("message", e.getMessage());
            String jsonResponse = gson.toJson(response);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonResponse).build();
        }
    }

    @GET
    @Path("/getAllBanquets")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBanquets() {
        System.out.println("Received request at /getAllBanquets");
        Map<String, Object> response = new HashMap<>();
        try {
            List<Banquet> banquets = bmsMain.getAllBanquets();
            response.put("status", "success");
            response.put("banquets", banquets);
            String jsonResponse = gson.toJson(response);
            return Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
            response.put("status", "error");
            response.put("message", e.getMessage());
            String jsonResponse = gson.toJson(response);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonResponse).build();
        }
    }

    @GET
    @Path("/getAvailableUnregisteredBanquets")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAvailableUnregisteredBanquets(@QueryParam("attendeeEmail") String attendeeEmail) {
        System.out.println("Received request at /getAvailableUnregisteredBanquets for email: " + attendeeEmail);
        Map<String, Object> response = new HashMap<>();
        try {
            if (attendeeEmail == null || attendeeEmail.trim().isEmpty()) {
                response.put("status", "error");
                response.put("message", "Attendee email is required");
                String jsonResponse = gson.toJson(response);
                return Response.status(Response.Status.BAD_REQUEST).entity(jsonResponse).build();
            }
            
            List<Banquet> banquets = bmsMain.getAvailableUnregisteredBanquets(attendeeEmail);
            response.put("status", "success");
            response.put("banquets", banquets);
            String jsonResponse = gson.toJson(response);
            return Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            response.put("status", "error");
            response.put("message", e.getMessage());
            String jsonResponse = gson.toJson(response);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonResponse).build();
        }
    }

    // Depraecated
    // @POST
    // @Path("/addMealToBanquet")
    // @Consumes(MediaType.APPLICATION_JSON)
    // @Produces(MediaType.APPLICATION_JSON)
    // public Response addMealToBanquet(String requestData) {
    //     System.out.println("Received request at /addMealToBanquet: " + requestData);
    //     Map<String, Object> response = new HashMap<>();
    //     try {
    //         JsonObject jsonObject = gson.fromJson(requestData, JsonObject.class);
    //         int banquetBIN = jsonObject.get("banquetBIN").getAsInt();
    //         Meal meal = gson.fromJson(jsonObject.get("meal"), Meal.class);
    //         boolean result = bmsMain.addMealToBanquet(banquetBIN, meal);
    //         response.put("status", result ? "success" : "failure");
    //         String jsonResponse = gson.toJson(response);
    //         return Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();
    //     } catch (Exception e) {
    //         System.out.println("Error: " + e.getMessage());
    //         response.put("status", "error");
    //         response.put("message", e.getMessage());
    //         String jsonResponse = gson.toJson(response);
    //         return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonResponse).build();
    //     }
    // }

    @GET
    @Path("/getAttendeeByEmail")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAttendeeByEmail(@QueryParam("email") String email) {
        System.out.println("Received request at /getAttendeeByEmail: " + email);
        Map<String, Object> response = new HashMap<>();
        try {
            AttendeeAccount attendee = bmsMain.getAttendeeByEmail(email);
            
            response.put("status", "success");
            response.put("attendee", attendee);
            // System.out.println("Attendee: " + attendee);
            String jsonResponse = gson.toJson(response);
            return Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            response.put("status", "error");
            response.put("message", e.getMessage());
            String jsonResponse = gson.toJson(response);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonResponse).build();
        }
    }

    @GET
    @Path("/getReservesByAttendeeEmail")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReservesByAttendeeEmail(@QueryParam("email") String email) {
        System.out.println("Received request at /getReservesByAttendeeEmail: " + email);
        Map<String, Object> response = new HashMap<>();
        try {
            List<Reserve> reserves = bmsMain.getReservesByAttendeeEmail(email);
            response.put("status", "success");
            response.put("registrations", reserves);
            System.out.println("Reserves: " + reserves);
            String jsonResponse = gson.toJson(response);
            return Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
            response.put("status", "error");
            response.put("message", e.getMessage());
            String jsonResponse = gson.toJson(response);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonResponse).build();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            response.put("status", "error");
            response.put("message", e.getMessage());
            String jsonResponse = gson.toJson(response);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonResponse).build();
        }
    }

    @POST
    @Path("/updateAttendeeProfile")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAttendeeProfile(String attendeeData) {
        System.out.println("Received request at /updateAttendeeProfile: " + attendeeData);
        Map<String, Object> response = new HashMap<>();
        try {
            AttendeeAccount attendee = gson.fromJson(attendeeData, AttendeeAccount.class);
            boolean result = bmsMain.updateAttendeeProfile(attendee);
            response.put("status", result ? "success" : "failure");
            String jsonResponse = gson.toJson(response);
            return Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();
        } catch (ValidationException e) {
            System.out.println("Validation error: " + e.getMessage());
            response.put("status", "error");
            response.put("message", e.getMessage());
            String jsonResponse = gson.toJson(response);
            return Response.status(Response.Status.BAD_REQUEST).entity(jsonResponse).build();
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
            response.put("status", "error");
            response.put("message", e.getMessage());
            String jsonResponse = gson.toJson(response);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonResponse).build();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            response.put("status", "error");
            response.put("message", e.getMessage());
            String jsonResponse = gson.toJson(response);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonResponse).build();
        }
    }


    @POST
    @Path("/updateAttendeeRegistrationData")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAttendeeRegistrationData(String requestData) {
        System.out.println("Received request at /updateAttendeeRegistrationData: " + requestData);
        Map<String, Object> response = new HashMap<>();
        try {
            JsonObject jsonObject = gson.fromJson(requestData, JsonObject.class);
            Reserve registrationData = gson.fromJson(jsonObject.get("registrationData"), Reserve.class);
            boolean result = bmsMain.updateAttendeeRegistrationData(registrationData);
            response.put("status", result ? "success" : "failure");
            String jsonResponse = gson.toJson(response);
            return Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            response.put("status", "error");
            response.put("message", e.getMessage());
            String jsonResponse = gson.toJson(response);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonResponse).build();
        }
    }

    @POST
    @Path("/deleteBanquet")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteBanquet(String requestData) {
        System.out.println("Received request at /deleteBanquet: " + requestData);
        Map<String, Object> response = new HashMap<>();
        try {
            JsonObject jsonObject = gson.fromJson(requestData, JsonObject.class);
            int banquetBIN = jsonObject.get("banquetBIN").getAsInt();
            boolean result = bmsMain.deleteBanquet(banquetBIN);
            response.put("status", result ? "success" : "failure");
            String jsonResponse = gson.toJson(response);
            return Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            response.put("status", "error");
            response.put("message", e.getMessage());
            String jsonResponse = gson.toJson(response);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonResponse).build();
        }
    }

    @POST
    @Path("/deleteReserve")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteReserve(String requestData) {
        System.out.println("Received request at /deleteReserve: " + requestData);
        Map<String, Object> response = new HashMap<>();
        try {
            JsonObject jsonObject = gson.fromJson(requestData, JsonObject.class);
            String attendeeEmail = jsonObject.get("attendeeEmail").getAsString();
            int banquetBIN = jsonObject.get("banquetBIN").getAsInt();
            boolean result = bmsMain.deleteReserve(attendeeEmail, banquetBIN);
            response.put("status", result ? "success" : "failure");
            String jsonResponse = gson.toJson(response);
            return Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
            response.put("status", "error");
            response.put("message", e.getMessage());
            String jsonResponse = gson.toJson(response);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonResponse).build();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            response.put("status", "error");
            response.put("message", e.getMessage());
            String jsonResponse = gson.toJson(response);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonResponse).build();
        }
    }

    @POST
    @Path("/registerAttendee")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerAttendee(String attendeeData) {
        System.out.println("Received request at /registerAttendee: " + attendeeData);
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> data = gson.fromJson(attendeeData, Map.class);
            boolean result = bmsMain.registerAttendee(new AttendeeAccount(data));
            response.put("status", result ? "success" : "failure");
            String jsonResponse = gson.toJson(response);
            return Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();
        } catch (ValidationException e) {
            System.out.println("Validation error: " + e.getMessage());
            response.put("status", "error");
            response.put("message", e.getMessage());
            String jsonResponse = gson.toJson(response);
            return Response.status(Response.Status.BAD_REQUEST).entity(jsonResponse).build();
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
            response.put("status", "error");
            response.put("message", e.getMessage());
            String jsonResponse = gson.toJson(response);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonResponse).build();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            response.put("status", "error");
            response.put("message", e.getMessage());
            String jsonResponse = gson.toJson(response);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonResponse).build();
        }
    }

    @POST
    @Path("/registerForBanquet")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerForBanquet(String registrationData) {
        System.out.println("Received request at /registerForBanquet: " + registrationData);
        Map<String, Object> response = new HashMap<>();
        try {
            Reserve reserve = gson.fromJson(registrationData, Reserve.class);
            RegistrationResult result = bmsMain.registerForBanquet(reserve);
            response.put("status", "success");
            response.put("registrationResult", result);
            String jsonResponse = gson.toJson(response);
            return Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();
        } catch (RegistrationException e) {
            System.out.println("Registration error: " + e.getMessage());
            response.put("status", "error");
            response.put("message", e.getMessage());
            String jsonResponse = gson.toJson(response);
            return Response.status(Response.Status.BAD_REQUEST).entity(jsonResponse).build();
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", e.getMessage());
            String jsonResponse = gson.toJson(response);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonResponse).build();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", e.getMessage());
            String jsonResponse = gson.toJson(response);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonResponse).build();
        }
    }

    @POST
    @Path("/searchRegisteredBanquets")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchRegisteredBanquets(String searchData) {
        System.out.println("Received request at /searchRegisteredBanquets: " + searchData);
        Map<String, Object> response = new HashMap<>();
        try {
            JsonObject jsonObject = gson.fromJson(searchData, JsonObject.class);
            String attendeeEmail = jsonObject.get("attendeeEmail").getAsString();
            SearchCriteria criteria = gson.fromJson(jsonObject.get("criteria"), SearchCriteria.class);
            
            if (attendeeEmail == null || attendeeEmail.trim().isEmpty()) {
                response.put("status", "error");
                response.put("message", "Attendee email is required");
                String jsonResponse = gson.toJson(response);
                return Response.status(Response.Status.BAD_REQUEST).entity(jsonResponse).build();
            }
            
            List<Banquet> banquets = bmsMain.searchRegisteredBanquets(attendeeEmail, criteria);
            response.put("status", "success");
            response.put("banquets", banquets);
            String jsonResponse = gson.toJson(response);
            return Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            response.put("status", "error");
            response.put("message", e.getMessage());
            String jsonResponse = gson.toJson(response);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonResponse).build();
        }
    }

    
}
