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
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author manie
 */
@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
public class users 
{
    /**
     * Obtiene el usuario en cuestión, dependiendo de qué 
     * parámetros se envíen
     * @param data Si la consulta es de un usuario específico
     * @param token Si la consulta es del mismo usuario, es por header por AWT
     * @return  JSON con los datos 
     */
    @GET
    public Response getUser(@QueryParam("id") Integer data,
            @HeaderParam("Authorization") String token)
    {
        Integer id = data;
        /*if(token!=null || token.equals(""))
        {
            id = Integer.parseInt(token); //AQUI VA EL AWT
        }*/
        Connection con = Database.getConnection();
        if(id == null)
        {
            return Response.status(404).build();
        }
        else 
        {
            try {
                String query = "SELECT * FROM cocollector.\"Usuario\" "
                        + "WHERE \"ID\" = ?";
                PreparedStatement st;
                st = con.prepareStatement(query);
                st.setInt(1, id);
                
                ResultSet rs = st.executeQuery();
                
                JSONObject resp = new JSONObject();
                if(rs.next())
                {
                    resp.put("ID", rs.getInt("ID"));
                    resp.put("Nombre_usuario", rs.getString("Nombre_usuario"));
                    resp.put("Correo", rs.getString("Correo"));
                    resp.put("Nombre", rs.getString("Nombre"));
                    resp.put("Apellido_paterno", rs.getString("Apellido_paterno"));
                    resp.put("Apellido_materno", rs.getString("Apellido_materno"));
                    resp.put("Tipo", rs.getString("Tipo"));
                    return Response.ok(resp.toJSONString()).build();
                }
            } catch (SQLException ex) {
                Logger.getLogger(users.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return Response.status(400).build();
    }
    
    /**
     * Metodo POST: Agregar usuario
     * Agrega un usuario a la base de datos
     * con los datos correspondientes
     * @param input Request, es el body del JSON que se envió por POST
     * @return Response, 400 si hubo error y 200 + token + tipo si no.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(InputStream input)
    {
        try {
            Connection conn = Database.getConnection();
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject)jsonParser.parse(new InputStreamReader(input, "UTF-8"));
            
            String query = "INSERT INTO cocollector.\"Usuario\""
                    + "(\"Nombre_usuario\","
                    + "\"Correo\","
                    + "\"Contrasena\","
                    + "\"Nombre\","
                    + "\"Apellido_paterno\","
                    + "\"Apellido_materno\","
                    + "\"Tarjeta_credito\","
                    + "\"Fecha_Expiracion\","
                    + "\"Tipo\") VALUES (?,?,?,?,?,?,?,?,'Usuario') RETURNING \"ID\"";
       
            PreparedStatement st;
            try {
                st = conn.prepareStatement(query);
                st.setString(1, (jsonObject.get("nombreUsuario").toString()));
                st.setString(2, jsonObject.get("correo").toString());
                st.setString(3, jsonObject.get("contrasena").toString());
                st.setString(4, jsonObject.get("nombre").toString());
                st.setString(5, jsonObject.get("apellidoPaterno").toString());
                st.setString(6, jsonObject.get("apellidoMaterno").toString());
                st.setString(7, jsonObject.get("tarjeta").toString());                
                st.setDate(8, new java.sql.Date(2020,12,31));
                
                st.execute();
                JSONObject resp = new JSONObject();
                
                query = "SELECT * FROM cocollector.\"Usuario\" WHERE \"Nombre_usuario\" = ?";
                
                st = conn.prepareStatement(query);
              
                st.setString(1, jsonObject.get("nombreUsuario").toString());
                
                ResultSet rs = st.executeQuery();
                
                if(rs.next())
                {
                    resp.put("token", rs.getString("ID")); //AQUI VA LA CHINGADERA DEL TOKEN
                    resp.put("userType",rs.getString("Tipo"));
                    return Response.ok(resp.toJSONString()).build();
                }
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
}
