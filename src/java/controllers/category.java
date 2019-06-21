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
 * @author lghhs
 */
@Path("/categorias")
@Produces(MediaType.APPLICATION_JSON)
public class category {
    
    /**
     * Obtiene como parámetro el id de la categoría que se quiere
     * obtener.
     * En caso de que se realice la consulta regresa la categoria en espécifico y un código 200.
     * Si no se encuentra el JSON estará vacío.
     * Si algún error ocurre en el proceso manda un código de error 400.
     * @param id
     * @return 
     */
    @GET
    public Response getCat(@QueryParam("id") Integer id){
        Connection conn = Database.getConnection();
        if(id != null){
            try {
                String query = "SELECT * FROM cocollector.\"Categoria\" WHERE \"ID\" = ?";
                PreparedStatement st = conn.prepareStatement(query);
                st.setInt(1, id);
                ResultSet rs = st.executeQuery();
                JSONObject resp = new JSONObject();
                if(rs.next())
                {
                    String categoryid = rs.getString("ID");
                    String nombre = rs.getString("Nombre");
                    String des = rs.getString("Descripcion");
                    resp.put("ID", categoryid);
                    resp.put("Nombre", nombre);
                    resp.put("Descripcion", des);                  
                }
                return Response.ok(resp.toJSONString()).build();
            } catch (SQLException ex) {
                Logger.getLogger(order.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            try {
                String query = "SELECT * FROM cocollector.\"Categoria\"";
                PreparedStatement st = conn.prepareStatement(query);
                ResultSet rs = st.executeQuery();
                JSONArray respArr = new JSONArray();
                while(rs.next())
                {
                    JSONObject resp = new JSONObject();
                    String categoryid = rs.getString("ID");
                    String nombre = rs.getString("Nombre");
                    String des = rs.getString("Descripcion");
                    resp.put("ID", categoryid);
                    resp.put("Nombre", nombre);
                    resp.put("Descripcion", des);  
                    respArr.add(resp);
                }
                return Response.ok(respArr.toJSONString()).build();
            } catch (SQLException ex) {
                Logger.getLogger(order.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return Response.status(400).build(); 
    }
    
    /**
     * Por medio del InputStream recibe todos los valores
     * necesarios para crear una nueva categoria.
     * Realiza la query insertando los datos recibidos y en caso
     * de realizarse de manera correcta envía un código 200 de confirmación
     * y retorna el ID del pedido recién creado.
     * Si ocurre algún error al momento de la inserción regresa un 400.
     * @param input
     * @return 
     */
    @POST
    public Response createCat(InputStream input){
        try {
            Connection conn = Database.getConnection();
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject)jsonParser.parse(new InputStreamReader(input, "UTF-8"));
            
            String query = "INSERT into cocollector.\"Categoria\"(\"Nombre\", "
                    + "\"Descripcion\") "
                    + "VALUES (?,?) returning \"ID\"";
       
            PreparedStatement st;
            try {
                st = conn.prepareStatement(query);
                st.setString(1, jsonObject.get("nombre").toString());
                st.setString(2, jsonObject.get("descripcion").toString());
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
    
    /**
     * Por medio del InputStream recibe todos los valores
     * necesarios para modificar una categoria.
     * Primero verifica que la id esté entre los valores.
     * Realiza un SELECT a la base de datos para obtener los datos previos
     * y los guarda, para posteriormente modificar los valores solo si vienen 
     * en los parámetros y si no están vacíos.
     * Retorna un código 200 si se hizo la modificación correctamente.
     * Si ocurre algún error al momento de la inserción regresa un 400.
     * @param input
     * @return 
     */
    @PUT
    public Response modifyCat(InputStream input){
        try {
            Connection conn = Database.getConnection();
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject)jsonParser.parse(new InputStreamReader(input, "UTF-8"));
            if(jsonObject.containsKey("id"))
            {
                String query = "SELECT * FROM cocollector.\"Categoria\" WHERE \"ID\" = ?";
                PreparedStatement st = conn.prepareStatement(query);
                st.setInt(1, Integer.parseInt(jsonObject.get("id").toString()));
                ResultSet rs = st.executeQuery();
                String nombre = "", descripcion = "";
                if(rs.next())
                {
                    nombre = rs.getString("Nombre");
                    descripcion = rs.getString("Descripcion");                  
                }
                nombre = (jsonObject.containsKey("nombre")) ? (!(jsonObject.get("nombre").toString().equals("")) ? jsonObject.get("nombre").toString():nombre):nombre;
                descripcion = (jsonObject.containsKey("descripcion")) ? (!(jsonObject.get("descripcion").toString().equals("")) ? jsonObject.get("descripcion").toString():descripcion):descripcion;
                query = "UPDATE cocollector.\"Categoria\" SET \"Nombre\" = ?, "
                    + "\"Descripcion\" = ? WHERE \"ID\" = ?";
                st = conn.prepareStatement(query);
                st.setString(1, nombre);
                st.setString(2, descripcion);
                st.setInt(3, Integer.parseInt(jsonObject.get("id").toString()));
                st.execute();
                return Response.ok().build();
            }
        } catch (SQLException ex) {
            Logger.getLogger(order.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(requisition.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(requisition.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(requisition.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(400).build(); 
      }
    
    /**
     * Recibe como parámetro un ID y posteriormente
     * realiza un delete en la base de datos donde el ID
     * sea igual al recibido. 
     * Si se realiza correctamente regresa un código 200 de confirmación
     * y si ocurre algún percance un 400 de error.
     * @param id
     * @return 
     */
    @DELETE
    public Response deleteCat(@QueryParam("id") Integer id){
        Connection conn = Database.getConnection();
        String query = "DELETE FROM cocollector.\"Categoria\" WHERE \"ID\" = ?";
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

