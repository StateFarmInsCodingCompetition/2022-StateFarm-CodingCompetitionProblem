package com.statefarm.codingcompetition.simpledatatool.sql.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**Helper Class for creating SQL Connections
 * 
 */
public class SqlConnector {
    
    //This variable is set to be unreadibe outside of the class for security reasons;
    private static String connectionString;


    public static Connection createConnection(){
        try{
        return DriverManager.getConnection(connectionString);
        } catch (SQLException e){
            return null;
        }
    }

    public static void setConnectionString(String connString){
        connectionString = connString;
    }
}
