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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.keys.AesKey;
import org.jose4j.lang.ByteUtil;
import org.jose4j.lang.JoseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * 
 * @author ppc
 */
@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
public class login {
    /**
     * Hace la autenticación y genera el tokken de la sesión
     * @param input Request, es el body del JSON que se envió por POST
     * @return Response, 404 si hubo error y tipo + id si no.
     */
     @POST
    public Response login_entry(InputStream input){
        try {
            Connection conn = Database.getConnection();
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject)jsonParser.parse(new InputStreamReader(input, "UTF-8"));
            
            JSONObject resp = new JSONObject();
            String username="", password="";
            
            if(jsonObject.containsKey("User")){
                username = jsonObject.get("User").toString();
                resp.put("respuesta", username);
            }else{
                return Response.status(400).build();
            }
            
            if(jsonObject.containsKey("Password")){
                password = jsonObject.get("Password").toString();
                resp.put("respuesta", password);
            }else{
                return Response.status(400).build();
            }
            
            //if(!username.equals("")||!password.equals("")){
             //   return Response.status(400).build();
            //}
            
            
             try {
                String query = "SELECT \"ID\", \"Tipo\" from cocollector.\"Usuario\" where \"Nombre_usuario\" = ? AND \"Contrasena\" = ?";
                PreparedStatement st = conn.prepareStatement(query);
                st.setString(1, username);
                st.setString(2, password);
                ResultSet rs = st.executeQuery();
                JSONArray respArr = new JSONArray();
                JSONObject respobj=new JSONObject();
                if(rs.next())
                {
                    respobj = new JSONObject();
                    Integer id = rs.getInt("ID");
                    String tipo = rs.getString("Tipo");
                    
                    String token=Token.getToken(id.toString());
                    respobj.put("token", token);
                    respobj.put("userType", tipo);
                    respobj.put("hola", "hola");
                    respobj.put("id", id);
                    respArr.add(resp);
                    return Response.ok(respobj.toJSONString()).build();

                }
            } catch (SQLException ex) {
                Logger.getLogger(order.class.getName()).log(Level.SEVERE, null, ex);
            }
          
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(order.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(order.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(404).build();
    }
}
