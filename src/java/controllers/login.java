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
 * @author manie
 */
@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
public class login {
     @POST
    public Response login_entry(InputStream input){
        try {
            Connection conn = Database.getConnection();
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject)jsonParser.parse(new InputStreamReader(input, "UTF-8"));
            
            JSONObject resp = new JSONObject();
            String username="", password="";
            
            if(jsonObject.containsKey("username")){
                username = jsonObject.get("username").toString();
                resp.put("respuesta", username);
            }else{
                return Response.status(400).build();
            }
            
            if(jsonObject.containsKey("password")){
                password = jsonObject.get("password").toString();
                resp.put("respuesta", password);
            }else{
                return Response.status(400).build();
            }
            
            if(!username.equals("")||!password.equals("")){
                return Response.status(400).build();
            }
            
             try {
                String query = "SELECT \"ID\", \"Tipo\" from cocollector.\"Usuario\" where \"Nombre_usuario\" = "+ username +" AND \"Contrasena\" = "+ password;
                PreparedStatement st = conn.prepareStatement(query);
                ResultSet rs = st.executeQuery();
                JSONArray respArr = new JSONArray();
                JSONObject respobj=null;
                 System.out.println("holajaja");
                while(rs.next())
                {
                    respobj = new JSONObject();
                    String id = rs.getString("ID");
                    String tipo = rs.getString("Tipo");
                    
                    try{
                        AesKey key = new AesKey(ByteUtil.randomBytes(16));
                        JsonWebEncryption jwe = new JsonWebEncryption();
                        jwe.setPayload(id);
                        jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.A128KW);
                        jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
                        jwe.setKey(key);
                        String serializedJwe = jwe.getCompactSerialization();
                        System.out.println("Serialized Encrypted JWE: " + serializedJwe);
                        jwe = new JsonWebEncryption();
                        jwe.setAlgorithmConstraints(new AlgorithmConstraints(ConstraintType.WHITELIST, 
                               KeyManagementAlgorithmIdentifiers.A128KW));
                        jwe.setContentEncryptionAlgorithmConstraints(new AlgorithmConstraints(ConstraintType.WHITELIST, 
                               ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256));
                        jwe.setKey(key);
                        jwe.setCompactSerialization(serializedJwe);
                        System.out.println("Payload: " + jwe.getPayload());
                        
                        respobj.put("token", jwe.getPayload());
                    }catch (JoseException ex) {
                        Logger.getLogger(login.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    respobj.put("userType", tipo);
                    respobj.put("hola", "hola");
                    respobj.put("id", id);
                    respArr.add(resp);
                }
                return Response.ok(respobj.toJSONString()).build();
            } catch (SQLException ex) {
                Logger.getLogger(order.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return Response.ok(resp.toJSONString()).build();
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(order.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(order.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(404).build();
    }
}
