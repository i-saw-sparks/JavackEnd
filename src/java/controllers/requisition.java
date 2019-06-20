/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import database.Database;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author manie
 */
@Path("/pedido")
@Produces(MediaType.APPLICATION_JSON)
public class requisition {
    
    /// El get esta terminado pero sin probar
    @GET
    public Response getReq(
            @QueryParam("id") Integer id, 
            @QueryParam("user") Integer user
            ){
        Connection conn = Database.getConnection();
        if(id != null){
            try {
                String query = "SELECT * FROM cocollector.\"Pedido\" WHERE \"ID\" = ?";
                PreparedStatement st = conn.prepareStatement(query);
                st.setInt(1, id);
                ResultSet rs = st.executeQuery();
                JSONObject resp = new JSONObject();
                if(rs.next())
                {
                    String userid = rs.getString("ID");
                    String total = rs.getString("Total");
                    String cantidad = rs.getString("Cantidad");
                    String orden = rs.getString("Orden");
                    String producto = rs.getString("Producto");
                    resp.put("ID", userid);
                    resp.put("Total", total);
                    resp.put("Cantidad", cantidad);
                    resp.put("Orden", orden);
                    resp.put("Producto", producto);                   
                }
                return Response.ok(resp.toJSONString()).build();
            } catch (SQLException ex) {
                Logger.getLogger(order.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return Response.status(400).build(); 
    }
    
    /// El post sin terminar
    @POST
    public Response createReq(InputStream input){
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
        return Response.status(404).build();
    }
    
    /// El put sin terminar
    @PUT
    public Response modifyReq(InputStream input){
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
    
    /// El delete esta terminado pero sin probar
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
