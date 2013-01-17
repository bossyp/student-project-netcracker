package ua.netcrackerteam.DAO;

import java.sql.*;
import java.util.Locale;

/**
 * Pattern Singelton. For creating connection to database and one access point in project
 * @author maksym
 */
public class DbConnectionSingleton
{
    private static DbConnectionSingleton uniqueInstance;
    private Connection conn;
    
    private DbConnectionSingleton() {
        try	{
            conn = getConnection();
        }
        catch (SQLException e)
        {
            while ( e != null)
            {
                System.out.println("" + e);
                e = e.getNextException();
            }
        }
    }
    
    /**
     * @return singleton object
     */
    public static synchronized DbConnectionSingleton getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new DbConnectionSingleton();
        }
        return uniqueInstance;
    }
    
    /**
     * Method for getting connection object once in constructor
     * @return Connection object
     * @throws SQLException 
     */
    private Connection getConnection() throws SQLException
    {
        String url="jdbc:oracle:thin:@85.238.104.112:1521:XE";
        String username="netcracker";
        String password="oracle";
        try
        {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        }
        catch(java.lang.ClassNotFoundException er)
        {
            System.out.print("JDBC-Driver is wrong!\n");
            System.out.println(er.getMessage());
            er.printStackTrace();
            System.exit(0);
        }
        Locale locale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
        conn = DriverManager.getConnection(url,username,password);
        System.out.println("Connection is OK!");
        Locale.setDefault(locale);
        return conn;
    }
    
    /**
     * 
     * @return connection to database
     */
    public Connection getConn()
    {
        return conn;
    }
}