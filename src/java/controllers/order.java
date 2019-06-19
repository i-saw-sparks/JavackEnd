/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import database.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.simple.JSONArray;

/**
 *
 * @author manie
 */
@Path("/orden")
@Produces(MediaType.APPLICATION_JSON)
public class order {
    
    @GET
    public Response getOrder(
            @QueryParam("id") Integer id, 
            @QueryParam("user") Integer user
            ){
        Connection conn = Database.getConnection();
        if(id != null){
            try {
                String query = "SELECT * FROM cocollector.\"Orden\" WHERE \"ID\" = ?";
                PreparedStatement st = conn.prepareStatement(query);
                st.setInt(1, id);
                ResultSet rs = st.executeQuery();
                JSONObject resp = new JSONObject();
                if(rs.next())
                {
                    String userid = rs.getString("ID");
                    String total = rs.getString("Total");
                    String status = rs.getString("Status");
                    String fecha = rs.getString("Fecha_pedido");
                    String direccion = rs.getString("Direccion");
                    String usuario = rs.getString("Usuario");
                    resp.put("ID", userid);
                    resp.put("Total", total);
                    resp.put("Status", status);
                    resp.put("Fecha_pedido", fecha);
                    resp.put("Direccion", direccion);
                    resp.put("Usuario", usuario);
                    
                }
                return Response.ok(resp.toJSONString()).build();
            } catch (SQLException ex) {
                Logger.getLogger(order.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            try {
                String query = "SELECT * FROM cocollector.\"Orden\" WHERE \"Usuario\" = ?";
                PreparedStatement st = conn.prepareStatement(query);
                st.setInt(1, user);
                ResultSet rs = st.executeQuery();
                JSONArray respArr = new JSONArray();
                while(rs.next())
                {
                    JSONObject resp = new JSONObject();
                    String total = rs.getString("Total");
                    String status = rs.getString("Status");
                    String fecha = rs.getString("Fecha_pedido");
                    String direccion = rs.getString("Direccion");
                    String usuario = rs.getString("Usuario");
                    resp.put("Total", total);
                    resp.put("Status", status);
                    resp.put("Fecha_pedido", fecha);
                    resp.put("Direccion", direccion);
                    resp.put("Usuario", usuario);
                    respArr.add(resp);
                }
                return Response.ok(respArr.toJSONString()).build();
            } catch (SQLException ex) {
                Logger.getLogger(order.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return Response.status(400).build();
          
    }
    
    @POST
    public Response createOrder(InputStream input){
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject)jsonParser.parse(new InputStreamReader(input, "UTF-8"));
            
            JSONObject resp = new JSONObject();
            
            
            if(jsonObject.containsKey("total")){
                String j = jsonObject.get("total").toString();
                resp.put("respuesta", j);
            }else{
                return Response.status(400).build();
            }
            
            
            
            
            //Aqui se toman los argumentos y se realiza la query
            
            return Response.ok(resp.toJSONString()).build();
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(order.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(order.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(404).build();
    }
    
    @PUT
    public Response modifyOrder(InputStream input){
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject)jsonParser.parse(new InputStreamReader(input, "UTF-8"));
            
            //Aqui se toman los argumentos y se realiza la query
            
            return Response.ok().build();
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(order.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(order.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.serverError().build();
    }
    
    @DELETE
    public Response deleteOrder(InputStream input){
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject)jsonParser.parse(new InputStreamReader(input, "UTF-8"));
            
            String id = jsonObject.get("id").toString();
            //Aqui se toman los argumentos y se realiza la query
            
            return Response.ok().build();
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(order.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(order.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.serverError().build();
    }
}
