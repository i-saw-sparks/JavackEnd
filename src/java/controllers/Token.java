/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.keys.AesKey;
import org.jose4j.lang.ByteUtil;
import org.jose4j.lang.JoseException;

/**
 *
 * @author usuario
 */
public class Token {
    
    private static HashMap<String,String> tabla;
    static
    {
        tabla = new HashMap<>();
        
    }
    
    public static String getToken(String id)
    {
        try {
            AesKey key = new AesKey(ByteUtil.randomBytes(16));
            JsonWebEncryption jwe = new JsonWebEncryption();
            jwe.setPayload(id);
            jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.A128KW);
            jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
            jwe.setKey(key);
            String serializedJwe = jwe.getCompactSerialization();
            System.out.println("Serialized Encrypted JWE: " + serializedJwe);
            jwe = new JsonWebEncryption();
            jwe.setAlgorithmConstraints(new AlgorithmConstraints(AlgorithmConstraints.ConstraintType.WHITELIST,
                    KeyManagementAlgorithmIdentifiers.A128KW));
            jwe.setContentEncryptionAlgorithmConstraints(new AlgorithmConstraints(AlgorithmConstraints.ConstraintType.WHITELIST,
                    ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256));
            jwe.setKey(key);
            jwe.setCompactSerialization(serializedJwe);
            System.out.println("Payload: " + jwe.getPayload());
                        
            tabla.forEach((token,user)->
            {
                if(user.equals(id))
                {
                    tabla.remove(token);
                }
            });
            
            tabla.put(serializedJwe,id);

            
            return serializedJwe;
        } catch (JoseException ex) {
            Logger.getLogger(Token.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static int getId(String token)
    {
        System.out.println(tabla.get(token));
        return Integer.parseInt(tabla.get(token));
    }
    
    public static boolean authenticated(String token){
        if(token!=null&&!token.equals("")){
            if(tabla.containsKey(token))
            {
                return true;
            }
            return false;
        }else{
            return false;
        }
    }
}
