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
import javax.ws.rs.GET;
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
@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
public class login {
     @POST
    public Response login_entry(InputStream input){
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject)jsonParser.parse(new InputStreamReader(input, "UTF-8"));
            
            JSONObject resp = new JSONObject();
            
            
            if(jsonObject.containsKey("username")){
                String username = jsonObject.get("username").toString();
                resp.put("respuesta", username);
            }else{
                return Response.status(400).build();
            }
            
            if(jsonObject.containsKey("password")){
                String password = jsonObject.get("password").toString();
                resp.put("respuesta", password);
            }else{
                return Response.status(400).build();
            }
            
            /*if(!username.equals(null)&&!password.equals(null)){
                
            }*/
            
            //Aqui se toman los argumentos y se realiza la query
            
            return Response.ok(resp.toJSONString()).build();
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(order.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(order.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(404).build();
    }
}
