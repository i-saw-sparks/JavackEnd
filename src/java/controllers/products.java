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
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
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
@Path("/productos")
@Produces(MediaType.APPLICATION_JSON)
public class products {
    
    /**
     * Método que recibe un Request con un id ya sea de categoría o de producto 
     * mediante GET y dependiendo de lo que se haya
     * pedido retorna un objeto JSON con todos las columnas de la tabla y las 5 
     * rutas de imagen relacionadas a ese producto.
     * 
     * @param id
     * @param category_id
     * @return 
     */
    
    @GET
    public Response getProduct(
            @QueryParam("id") Integer id,
            @QueryParam("idCategoria") Integer category_id
    ){
        Connection conn = Database.getConnection();
        if(id != null) {
            try {
                String query = "SELECT *, "
                            + " (SELECT \"Ruta\" AS img1 FROM cocollector.\"Imagen\" WHERE \"Producto\" = ? LIMIT 1 OFFSET 0), "
                            + " (SELECT \"Ruta\" AS img2 FROM cocollector.\"Imagen\" WHERE \"Producto\" = ? LIMIT 1 OFFSET 1), "
                            + " (SELECT \"Ruta\" AS img3 FROM cocollector.\"Imagen\" WHERE \"Producto\" = ? LIMIT 1 OFFSET 2), "
                            + " (SELECT \"Ruta\" AS img4 FROM cocollector.\"Imagen\" WHERE \"Producto\" = ? LIMIT 1 OFFSET 3), "
                            + " (SELECT \"Ruta\" AS img5 FROM cocollector.\"Imagen\" WHERE \"Producto\" = ? LIMIT 1 OFFSET 4) "
                            + " FROM cocollector.\"Producto\" where \"ID_Producto\"= ? ";
                        
                PreparedStatement st = conn.prepareStatement(query);
                st.setInt(1, id);
                st.setInt(2, id);
                st.setInt(3, id);
                st.setInt(4, id);
                st.setInt(5, id);
                st.setInt(6, id);
                ResultSet rs = st.executeQuery();
                JSONObject resp = new JSONObject();
                
                if(rs.next()) {
                    Integer productId = rs.getInt("ID_Producto");
                    String descripcion = rs.getString("Descripcion");
                    String nombre = rs.getString("Nombre");
                    Integer precio = rs.getInt("Precio");
                    Integer stock = rs.getInt("Stock");
                    Integer categoria = rs.getInt("Categoria");
                    String img1 = rs.getString("img1");
                    String img2 = rs.getString("img2");
                    String img3 = rs.getString("img3");
                    String img4 = rs.getString("img4");
                    String img5 = rs.getString("img5");
                    
                    resp.put("ID_Producto", productId);
                    resp.put("Descripcion", descripcion);
                    resp.put("Nombre", nombre);
                    resp.put("Precio", precio);
                    resp.put("Stock", stock);
                    resp.put("Categoria", categoria);
                    resp.put("img1", img1);
                    resp.put("img2", img2);
                    resp.put("img3", img3);
                    resp.put("img4", img4);
                    resp.put("img5", img5);
                    
                }
                return Response.ok(resp.toJSONString()).build();
            } catch (SQLException ex) {
                Logger.getLogger(order.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else if(category_id != null) {
            try {
                String query = "SELECT *,"
                        + "(SELECT \"Ruta\" AS img1 FROM cocollector.\"Imagen\" WHERE cocollector.\"Producto\".\"ID_Producto\" = cocollector.\"Imagen\".\"Producto\" LIMIT 1 OFFSET 0)"
                        + " FROM cocollector.\"Producto\" WHERE \"Categoria\" = ?";
                PreparedStatement st = conn.prepareStatement(query);
                st.setInt(1, category_id);
                ResultSet rs = st.executeQuery();
                JSONArray respArr = new JSONArray();
                
                while(rs.next()) {
                    JSONObject resp = new JSONObject();
                    Integer productId = rs.getInt("ID_Producto");
                    String descripcion = rs.getString("Descripcion");
                    String nombre = rs.getString("Nombre");
                    Integer precio = rs.getInt("Precio");
                    Integer stock = rs.getInt("Stock");
                    Integer categoria = rs.getInt("Categoria");
                    String url = rs.getString("img1");
                            
                    resp.put("ID_Producto", productId);
                    resp.put("Descripcion", descripcion);
                    resp.put("Nombre", nombre);
                    resp.put("Precio", precio);
                    resp.put("Stock", stock);
                    resp.put("Categoria", categoria);
                    resp.put("img1", url);
                    respArr.add(resp);
                }
                
                return Response.ok(respArr.toJSONString()).build();
            } catch (SQLException ex) {
                Logger.getLogger(order.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                String query = "SELECT * FROM cocollector.\"Producto\"";
                PreparedStatement st = conn.prepareStatement(query);
                ResultSet rs = st.executeQuery();
                JSONArray respArr = new JSONArray();
                
                while(rs.next()) {
                    JSONObject resp = new JSONObject();
                    Integer productId = rs.getInt("ID_Producto");
                    String descripcion = rs.getString("Descripcion");
                    String nombre = rs.getString("Nombre");
                    Integer precio = rs.getInt("Precio");
                    Integer stock = rs.getInt("Stock");
                    Integer categoria = rs.getInt("Categoria");
                    
                    resp.put("ID_Producto", productId);
                    resp.put("Descripcion", descripcion);
                    resp.put("Nombre", nombre);
                    resp.put("Precio", precio);
                    resp.put("Stock", stock);
                    resp.put("Categoria", categoria);
                    respArr.add(resp);
                }
                return Response.ok(respArr.toJSONString()).build();
                
            } catch (SQLException ex) {
                Logger.getLogger(products.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return Response.status(400).build();     
    }
    
    /**
     * Método para crear un producto, sólo puede ser ejecutado por un usuario 
     * de tipo administrador. La verificación se realiza
     * mediante un token. Se obtienen todos los parámetros a ingresar por medio 
     * de un Request.
     * 
     * @param input
     * @return 
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createProduct(InputStream input,@HeaderParam("Authorization") String token) {
        try {
            if(!Token.authenticated(token)){
                return Response.status(403).build();
            }
            Connection conn = Database.getConnection();
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject)jsonParser.parse(new InputStreamReader(input, "UTF-8"));
            
            String query = " INSERT INTO cocollector.\"Producto\"(\"Descripcion\", "
                        + " \"Nombre\", "
                        + " \"Precio\", "
                        + " \"Stock\", "
                        + " \"Categoria\") VALUES (?, ?, ?, ?, ?) ";
       
            PreparedStatement st;
            try {
                st = conn.prepareStatement(query);
                
                st.setString(1, jsonObject.get("descripcion").toString());
                st.setString(2, (jsonObject.get("nombre").toString()));
                st.setInt(3, (Integer.parseInt(jsonObject.get("precio").toString())));
                st.setInt(4, (Integer.parseInt(jsonObject.get("stock").toString())));
                st.setInt(5, (Integer.parseInt(jsonObject.get("categoria").toString())));
                
                st.execute();

                return Response.ok().build();
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
     * Método para modificar un producto, sólo puede ser ejecutado por un 
     * usuario de tipo administrador. La verificación se realiza
     * mediante un token. Se obtienen todos los parámetros que se quierean 
     * modificar por medio de un request. Sólo es necesario mandar
     * los campos que se deseen modificar en la tabla.
     * 
     * @param input
     * @return 
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response modifyProduct(InputStream input,@HeaderParam("Authorization") String token){
        if(!Token.authenticated(token)){
            return Response.status(403).build();
        }
        String descripcion = "", nombre = "";
        Integer precio = 0, stock = 0, categoria = 0;
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject)jsonParser.parse(new InputStreamReader(input, "UTF-8"));
            
            Connection conn = Database.getConnection();
            String query = " SELECT * FROM cocollector.\"Producto\" WHERE \"ID_Producto\" = ?";
            PreparedStatement st;
            
            try {
                st = conn.prepareStatement(query);
                st.setInt(1, (Integer.parseInt(jsonObject.get("id").toString())));
                
                ResultSet rs = st.executeQuery();
                
                while(rs.next()) {
                    descripcion = rs.getString("Descripcion");
                    nombre = rs.getString("Nombre");
                    precio = rs.getInt("Precio");
                    stock = rs.getInt("Stock");
                    categoria = rs.getInt("Categoria");
                }
                
                if (jsonObject.get("descripcion").toString() != null)
                    descripcion = jsonObject.get("descripcion").toString();
                
                if (jsonObject.containsKey("nombre"))
                    nombre = jsonObject.get("nombre").toString();
                
                if (jsonObject.containsKey("precio"))
                    precio = Integer.parseInt(jsonObject.get("precio").toString());
                
                if (jsonObject.containsKey("stock"))
                    stock = Integer.parseInt(jsonObject.get("stock").toString());
                
                if (jsonObject.containsKey("categoria"))
                    categoria = Integer.parseInt(jsonObject.get("categoria").toString());
                
                query = " UPDATE cocollector.\"Producto\" SET "
                        + " \"Descripcion\" = ?, "
                        + " \"Nombre\" = ?, "
                        + " \"Precio\" = ?, "
                        + " \"Stock\" = ?, "
                        + " \"Categoria\" = ? "
                        + " WHERE \"ID_Producto\" = ?";
                
                st = conn.prepareStatement(query);
                
                st.setString(1, descripcion);
                st.setString(2, nombre);
                st.setInt(3, precio);
                st.setInt(4, stock);
                st.setInt(5, categoria);
                st.setInt(6, (Integer.parseInt(jsonObject.get("id").toString())));
                
                st.executeUpdate();

                return Response.ok().build();
                
            } catch (SQLException ex) {
                Logger.getLogger(products.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return Response.ok().build();
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(order.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(order.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(400).build();
    }
    
    /**
     * Método para eliminar un producto, sólo puede ser ejecutado por un 
     * usuario de tipo administrador. La verificación se realiza
     * mediante un token. Se obtieneel id del producto a eliminar en el 
     * Request.
     * 
     * @param id
     * @return 
     */
    @DELETE
    public Response deleteProduct(@QueryParam("id") Integer id,@HeaderParam("Authorization") String token) {
        if(!Token.authenticated(token)){
            return Response.status(403).build();
        }
        Connection conn = Database.getConnection();
        String query = " DELETE FROM cocollector.\"Imagen\" WHERE \"Producto\" = ?; "
                    + " DELETE FROM cocollector.\"Producto\" WHERE \"ID_Producto\" = ? ";
        PreparedStatement st;
        try {
            st = conn.prepareStatement(query);
            st.setInt(1, id);
            st.setInt(2, id);
            st.executeUpdate();
            return Response.ok().build();
        } catch (SQLException ex) {
            Logger.getLogger(order.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(400).build();
    }
    
}
