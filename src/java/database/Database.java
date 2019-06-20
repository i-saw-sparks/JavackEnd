/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase gestora de la base de datos
 * @author ivan_
 */
public class Database 
{ 
    private static ConcurrentLinkedDeque<Connection> pool;
    private static Logger logger;
    private static String database = "BDCocoProyecto";
    private static String user = "cocollector";
    private static String password = "12345678";
    private static String host = "192.168.84.214";
    private static String port = "5432";
    private static int initialConnections = 3;
    private static String url;
    
    static
    {
        logger = Logger.getLogger("Database");
        pool = new ConcurrentLinkedDeque<>();
        url = "jdbc:postgresql://"+host+":"+port+"/"+database;
        System.out.println(url);
        for(int i=0; i<initialConnections;i++)
        {
            try
            {
                Class.forName("org.postgresql.Driver");
                Connection connection = DriverManager.getConnection(url, user, password);
                logger.info("Succesfully connected");
                pool.add(connection);
            }
            catch (SQLException ex) 
            {
                logger.info("Failed: "+ex.getMessage());
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
    }
    
    /**
     * Pide una conexión a db, si no hay disponibles crea una
     * @return conexión
     */
    public static Connection getConnection()
    {
        if(pool.isEmpty())
        {
            try 
            {
                Connection newConnection = DriverManager.getConnection(url,user,password);
                logger.info("DB requested");
                return newConnection;
            } 
            catch (SQLException ex) 
            {
                logger.info("Failed"+ex.getMessage());
                return null;
            }
        }
        else 
        {
            return pool.getFirst();
        }
    }
    
    /**
     * Devuelve una conexión y la reasigna en el stack
     * @param connection conexion a regresar
     */
    public static void returnConnection(Connection connection)
    {
        logger.info("Connection returned");
        pool.addLast(connection);
    }
}
