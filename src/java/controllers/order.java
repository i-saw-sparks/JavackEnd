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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
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
            @HeaderParam("Authorization") String token
            ){
        if(!Token.authenticated(token)){
                return Response.status(403).build();
        }
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
                    resp.put("Fecha_pedido", fecha.toString());
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
                st.setInt(1, Token.getId(token));
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
                    resp.put("Fecha_pedido", fecha.toString());
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
    public Response createOrder(InputStream input, @HeaderParam("Authorization") String token)
    {
        try {
            if(!Token.authenticated(token)){
                System.out.println("No hay token ajjaj");
                return Response.status(403).build();
            }
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
                Integer idOrden  = 1;
                st = conn.prepareStatement(query);
                st.setInt(1, Integer.parseInt(jsonObject.get("total").toString()));
                st.setString(2, (jsonObject.get("estado").toString()));
                
                st.setDate(3, new java.sql.Date(System.currentTimeMillis()));
                
                st.setInt(4, (Integer.parseInt(jsonObject.get("direccion").toString())));
                st.setInt(5, (Token.getId(token))); //Quitar con token
                ResultSet rs = st.executeQuery();
                JSONObject resp = new JSONObject();
                if(rs.next()){
                    resp.put("id", rs.getInt("ID"));
                    idOrden = rs.getInt("ID");
                }
                
                query = "SELECT * FROM cocollector.\"Usuario\" WHERE \"ID\" = ?";
                
                st = conn.prepareStatement(query);
                st.setInt(1, Token.getId(token));
                
                rs = st.executeQuery();
                
                if(rs.next())
                {
                    
                    URL url = new URL("http://192.168.84.66:6543/transaccion");
                    
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    
                    con.setRequestMethod("POST");
                    con.setConnectTimeout(5000);
                    con.setReadTimeout(5000);
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setDoOutput(true);
                    JSONObject obj = new JSONObject();
                    obj.put("monto", Integer.parseInt(jsonObject.get("total").toString()));
                    obj.put("descripcion", "Compra en tienda desde JAVA");
                    obj.put("institucion", "Cocollector");
                    obj.put("tarjeta", rs.getString("Tarjeta_credito"));
                    
                    try(OutputStream os = con.getOutputStream()) {
                        byte[] innn = obj.toJSONString().getBytes("utf-8");
                        os.write(innn, 0, innn.length);  
                    }
                    String status;
                    if(con.getResponseCode() == 200)
                    {
                        status = "Confirmado";
                    }
                    else 
                    {
                       status = "Rechazado";
                    }
                    
                    query = "UPDATE cocollector.\"Orden\" SET \"Status\" = ?::estado WHERE \"ID\" = ?";
                    st = conn.prepareStatement(query);
                    
                    st.setString(1, status);
                    st.setInt(2, idOrden);
                    
                    st.execute();
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
    //@Consumes(MediaType.APPLICATION_JSON)
    public Response modifyOrder(InputStream input, @HeaderParam("Authorization") String token){
        try {
            if(!Token.authenticated(token)){
                return Response.status(403).build();
            }
            Connection conn = Database.getConnection();
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject)jsonParser.parse(new InputStreamReader(input, "UTF-8"));
            Integer id =  Integer.parseInt(jsonObject.get("id").toString());
            String query = "SELECT * from cocollector.\"Orden\" where \"ID\" = ?";
            PreparedStatement st;
            try{
                st = conn.prepareStatement(query);
                st.setInt(1, id);
                ResultSet rs = st.executeQuery();
           
                if(rs.next()){
                    int total;
                    String estado;
                    java.sql.Date fpedido;
                    int direccion;
                    int usuario;
                
                    if(jsonObject.containsKey("total")){
                        total = Integer.parseInt(jsonObject.get("total").toString());
                    }else{
                        total = rs.getInt("Total");
                    }
                    
                    if(jsonObject.containsKey("estado")){
                        estado = jsonObject.get("estado").toString();
                    }else{
                        estado = rs.getString("Status");
                    }
                    
                    fpedido = rs.getDate("Fecha_pedido");
                    
                    
                    if(jsonObject.containsKey("direccion")){
                        direccion = Integer.parseInt(jsonObject.get("direccion").toString());
                    }else{
                        direccion = rs.getInt("Direccion");
                    }
                    
                    usuario=Token.getId(token);
                    
                    String querx = "UPDATE cocollector.\"Orden\" SET "
                            + "\"Total\" = ?, "
                            + "\"Status\" = ?::estado, "
                            + "\"Fecha_pedido\" = ?, "
                            + "\"Direccion\" = ?, "
                            + "\"Usuario\" = ? where \"ID\" = ?";
                    PreparedStatement sts;
                    sts = conn.prepareStatement(querx);
                    sts.setInt(1, total);
                    sts.setString(2, estado);
                    sts.setDate(3, fpedido);
                    sts.setInt(4, direccion);
                    sts.setInt(5, usuario);
                    sts.setInt(6, id);
                    
                    sts.execute();                        
                }  
                return Response.ok().build();
            }catch(SQLException ex){
                Logger.getLogger(order.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(order.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(order.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(400).build();
    }
    
    @DELETE
    public Response deleteOrder(@QueryParam("id") Integer id, @HeaderParam("Authorization") String token){
        if(!Token.authenticated(token)){
                return Response.status(403).build();
        }
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
