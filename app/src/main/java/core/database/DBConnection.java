package core.database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * Created by ernestepistola on 2/4/17.
 */
public final class  DBConnection {

    private static Connection conn;
    /* make sure to use the jdbc:jtds:sqlserver class and modify only the server name, user, password, and other details as required */
    /* only jdbc:jtds:sqlserver works for Android becuase of the driver */
    //public static final String DB_CONNECTION_STRING = "jdbc:jtds:sqlserver://ernmobileappsqlserver.database.windows.net:1433;databaseName=mobileappdb;user=pau_salcedo@ernmobileappsqlserver;password=#12Mobileappdbpassword;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
     public static final String DB_CONNECTION_STRING = "jdbc:jtds:sqlserver://192.168.1.100;databaseName=btmsBak;user=sa;password=123;";
    /* implementation of database connection and manipulation functions */
    public static void openConnection(){

        try {

            Class.forName("net.sourceforge.jtds.jdbc.Driver");

            conn = DriverManager.getConnection(DB_CONNECTION_STRING);
            if (!conn.isClosed()) {
                System.out.println("connection opened");
            } else {
                System.out.println("connection failed to open");
            }

        } catch (ClassNotFoundException e) {
            // JDBC initialization error
            e.printStackTrace();
        } catch (SQLException e) {
            // SQL connection error
            e.printStackTrace();
        }
    }

    public static void closeConnection(){
        //System.out.println("attempting to close the connection...");
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("connection closed");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("unable to close the connection...");
    }

    public static Connection getConnection(){
        return conn;
    }

}
