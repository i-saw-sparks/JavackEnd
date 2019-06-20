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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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

@Path("/producto-imagen")
@Produces(MediaType.APPLICATION_JSON)
public class product_image {

    /**
     * Método para subir la una imagen de un producto.
     * @return 
     */
    @GET
    public Response getImage() {
        Connection conn = Database.getConnection();
        String query = " SELECT \"Ruta\", \"Producto\", \"Categoria\""
                    + " FROM cocollector.\"Imagen\" "
                    + " JOIN cocollector.\"Producto\" "
                    + " ON cocollector.\"Imagen\".\"Producto\" = cocollector.\"Producto\".\"ID_Producto\" "
                    + " ORDER BY RANDOM() LIMIT 5 ";
        
        try {
            PreparedStatement st = conn.prepareStatement(query);
            
            ResultSet rs = st.executeQuery();
            JSONArray respArr = new JSONArray();
            
            if(rs.next()) {
                JSONObject resp = new JSONObject();
                String ruta = rs.getString("Ruta");
                Integer producto = rs.getInt("Producto");
                Integer categoria = rs.getInt("Categoria");
                
                resp.put("Ruta", ruta);
                resp.put("Producto", producto);
                resp.put("Categoria", categoria);
                
                respArr.add(resp);
            }
            
            return Response.ok(respArr.toJSONString()).build();
        } catch (SQLException ex) {
            Logger.getLogger(product_image.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(400).build();
    }
    
    /**
     * Dependiendo del método usado en el request se ejecuta alguno 
     * de los 4 métodos disponibles para producto.
     * @param input
     * @return 
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response uploadImage(InputStream input) {
        
        try {
            Connection conn = Database.getConnection();
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject)jsonParser.parse(new InputStreamReader(input, "UTF-8"));
            
            String query = "INSERT INTO \"Imagen\"(\"Ruta\", \"Producto\") VALUES (?, ?)";
            
            PreparedStatement st;
            
            try {
                st = conn.prepareStatement(query);
                
                st.setString(1, (jsonObject.get("ruta").toString()));
                st.setInt(2, (Integer.parseInt(jsonObject.get("id").toString())));
                
                st.executeUpdate();
                
                return Response.ok().build();
            } catch (SQLException ex) {
                Logger.getLogger(product_image.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (IOException ex) {
            Logger.getLogger(product_image.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(product_image.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(400).build();
    }
}
