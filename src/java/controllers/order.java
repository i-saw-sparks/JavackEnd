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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ws.rs.Consumes;
import org.json.simple.JSONArray;

/**
 *
 * @author manie
 */
@Path("/orden")
@Produces(MediaType.APPLICATION_JSON)
public class order {
    
    
    /**
     * Obtiene como parámetros el id de la orden que se quiere
     * regresar, o el usuario del cual se necesitan obtener las ordenes.
     * En caso de ser por id regresa la orden en espécifico y un código 200.
     * En caso de ser por usuario regresa todas las órdenes pertenecientes a el y un código 200.
     * Si algún error ocurre en el proceso manda un código de error 400.
     * @param id
     * @param user
     * @return 
     */
    @GET
    public Response getOrder(
            @QueryParam("id") Integer id, 
            @QueryParam("user") Integer user //Quitar con token
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
                    Integer userid = rs.getInt("ID");
                    Integer total = rs.getInt("Total");
                    String status = rs.getString("Status");
                    Date fecha = rs.getDate("Fecha_pedido");
                    Integer direccion = rs.getInt("Direccion");
                    Integer usuario = rs.getInt("Usuario");
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
                    Integer total = rs.getInt("Total");
                    String status = rs.getString("Status");
                    Date fecha = rs.getDate("Fecha_pedido");
                    Integer direccion = rs.getInt("Direccion");
                    Integer usuario = rs.getInt("Usuario");
                    resp.put("Total",  total);
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
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createOrder(InputStream input){
        try {
            Connection conn = Database.getConnection();
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject)jsonParser.parse(new InputStreamReader(input, "UTF-8"));
            
            String query = "INSERT into cocollector.\"Orden\"(\"Total\", "
                    + "\"Status\", "
                    + "\"Fecha_pedido\", "
                    + "\"Direccion\", "
                    + "\"Usuario\") VALUES (?,?::estado,?,?,?) returning \"ID\"";
       
            PreparedStatement st;
            try {
                st = conn.prepareStatement(query);
                st.setInt(1, Integer.parseInt(jsonObject.get("total").toString()));
                st.setString(2, (jsonObject.get("estado").toString()));
                
                st.setDate(3, new java.sql.Date(System.currentTimeMillis()));
                
                st.setInt(4, (Integer.parseInt(jsonObject.get("direccion").toString())));
                st.setInt(5, (Integer.parseInt(jsonObject.get("usuario").toString()))); //Quitar con token
                ResultSet rs = st.executeQuery();
                JSONObject resp = new JSONObject();
                if(rs.next()){
                    resp.put("id", rs.getString("ID"));
                }
                return Response.ok(resp.toJSONString()).build();
            } catch (SQLException ex) {
                Logger.getLogger(order.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(order.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(order.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(400).build();
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
        return Response.status(400).build();
    }
    
    @DELETE
    public Response deleteOrder(@QueryParam("id") Integer id){
        Connection conn = Database.getConnection();
        String query = "DELETE FROM cocollector.\"Orden\" WHERE \"ID\" = ?";
        PreparedStatement st;
        try {
            st = conn.prepareStatement(query);
            st.setInt(1, id);
            st.execute();
            return Response.ok().build();
        } catch (SQLException ex) {
            Logger.getLogger(order.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(400).build();
    }
}
