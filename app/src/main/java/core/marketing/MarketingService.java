package core.marketing;

import java.sql.PreparedStatement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import core.database.DBConnection;
import core.marketing.interfaces.IMarketingObject;
import core.marketing.util.MarketingObjectDetail;

/**
 * Created by ernestepistola on 2/4/17.
 */
@Deprecated
public final class MarketingService {


    /* Table names in database */
    private static final String DB_TABLE_PROMOS = "PROMOS";
    private static final String DB_TABLE_FARE_FEES = "FARE_FEES";

    public static List<FareFee> fetchFareFees(){

        ArrayList<FareFee> results = new ArrayList<FareFee>();
        /* Connect to database and retrieve list */

        DBConnection.openConnection();
        Connection conn = DBConnection.getConnection();

        try{
            String sql = "SELECT * FROM " + DB_TABLE_FARE_FEES;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){
                FareFee fareFee = new FareFee(rs.getString(FareFee.DB_COLUMN_BUS_NAME), rs.getDouble(FareFee.DB_COLUMN_FEE));
                results.add(fareFee);
                String details = getMarketingObjectDetailString(fareFee);
                System.out.println("Found Fare Fee: " + details);
            }

        }catch (SQLException e){
            System.err.println("An error occurred while attempting to fetch the list of fare fees from the database.");
            e.printStackTrace();
        }

        DBConnection.closeConnection();
        return results;

    }

    public static List<Promo> fetchPromos(){
        ArrayList<Promo> results = new ArrayList<Promo>();
        /* Connect to database and retrieve list */

        DBConnection.openConnection();
        Connection conn = DBConnection.getConnection();

        try{
            String sql = "SELECT * FROM " + DB_TABLE_PROMOS;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){
                Promo promo = new Promo(rs.getString(Promo.DB_COLUMN_BUS_NAME), rs.getString(Promo.DB_COLUMN_PROMO));
                results.add(promo);
                String details = getMarketingObjectDetailString(promo);
                System.out.println("Found Promo: " + details);
            }

        }catch (SQLException e){
            System.err.println("An error occurred while attempting to fetch the list of promos from the database.");
            e.printStackTrace();
        }

        DBConnection.closeConnection();
        return results;
    }

    private static String getMarketingObjectDetailString(IMarketingObject marketingObject){
        String details = "";
        for(MarketingObjectDetail detail : marketingObject.getAdvertismentDetails()){
            details += detail.getKey() + " : " + detail.getValue() + " ";
        }
        return details;
    }

    public static void addPromoToDatabase(Promo promo){
        DBConnection.openConnection();
        Connection conn = DBConnection.getConnection();

        try {

            String details = getMarketingObjectDetailString(promo);
            System.out.println("Adding Promo to database: " + details);

            String sql = "INSERT INTO " + DB_TABLE_PROMOS + " VALUES (?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, promo.getBusName());
            stmt.setString(2, promo.getPromoDescription());
            stmt.execute();

        }catch(SQLException e){
            System.err.println("An error occurred while trying to add a Promo to the database.");
            e.printStackTrace();
        }

        DBConnection.closeConnection();
    }

    public static void addPromoToDatabase(String busName, String promoDescription){
        addPromoToDatabase(new Promo(busName, promoDescription));
    }

    public static void addFareFeeToDatabase(FareFee fareFee){
        DBConnection.openConnection();
        Connection conn = DBConnection.getConnection();

        try {

            String details = getMarketingObjectDetailString(fareFee);
            System.out.println("Adding Fare Fee to database: " + details);

            String sql = "INSERT INTO " + DB_TABLE_FARE_FEES + " VALUES (?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, fareFee.getBusName());
            stmt.setDouble(2, fareFee.getFee());
            stmt.execute();

        }catch(SQLException e){
            System.err.println("An error occurred while trying to add a Fare Fee to the database.");
            e.printStackTrace();
        }

        DBConnection.closeConnection();
    }

    public static void addFareFeeToDatabase(String busName, Double fareFee){
        addFareFeeToDatabase(new FareFee(busName, fareFee));
    }

    /* Initialize table for promos. Primary key is bus name */
    private static void initializePromoTable(){
        DBConnection.openConnection();
        Connection conn = DBConnection.getConnection();

        try {
            DatabaseMetaData dbm = conn.getMetaData();
            // check if already table is there
            ResultSet tables = dbm.getTables(null, null, DB_TABLE_PROMOS, null);
            if (!tables.next()) {
                System.out.println("Initializing table for Promos in database...");
                Statement stmt = conn.createStatement();
                String sql = "CREATE TABLE " + DB_TABLE_PROMOS +
                        " (" + Promo.DB_COLUMN_BUS_NAME + " VARCHAR(255) not NULL, " +
                        " " + Promo.DB_COLUMN_PROMO + " VARCHAR(255), " +
                        " PRIMARY KEY ( "+ Promo.DB_COLUMN_BUS_NAME +" ))";
                stmt.executeUpdate(sql);
            }
            else {
                System.out.println("The table for Promos already exists in the database...");
            }

        }catch (SQLException e){
            System.err.println("An error occurred while attempting to create the table for Promos in the database.");
            e.printStackTrace();
        }

        DBConnection.closeConnection();
    }

    /* Initialize table for fare fees. Primary key is bus name */
    private static void initializeFareFeesTable(){
        DBConnection.openConnection();
        Connection conn = DBConnection.getConnection();

        try {
            DatabaseMetaData dbm = conn.getMetaData();
            // check if already table is there
            ResultSet tables = dbm.getTables(null, null, DB_TABLE_FARE_FEES, null);
            if (!tables.next()) {
                System.out.println("Initializing table for Fare Fees in database...");
                Statement stmt = conn.createStatement();
                String sql = "CREATE TABLE " + DB_TABLE_FARE_FEES +
                        " (" + FareFee.DB_COLUMN_BUS_NAME + " VARCHAR(255) not NULL, " +
                        " " + FareFee.DB_COLUMN_FEE + " DECIMAL(6,2), " +
                        " PRIMARY KEY ( "+ FareFee.DB_COLUMN_BUS_NAME +" ))";
                stmt.executeUpdate(sql);
            }
            else {
                System.out.println("The table for Fare Fees already exists in the database...");
            }

        }catch (SQLException e){
            System.err.println("An error occurred while attempting to create the table for Fare Fees in the database.");
            e.printStackTrace();
        }

        DBConnection.closeConnection();
    }

    /* main method for testing and initialization of tables in database */
    /*
    public static void main(String[] args){
        initializeMarketingTables();
        reportMarketingAdvertisments();
    }
    */

    private static void reportMarketingAdvertisments(){

        List<Promo> promos = fetchPromos();
        List<FareFee> fareFees = fetchFareFees();

        int adCount = 0;

        for(Promo promo: promos){
            //String detail = getMarketingObjectDetailString(promo);
            //System.out.println("Promo found: " + detail);
            adCount++;
        }

        for(FareFee fareFee: fareFees){
            //String detail = getMarketingObjectDetailString(fareFee);
            //System.out.println("Fare Fee found: " + detail);
            adCount++;
        }

        if(adCount == 0){
            System.out.println("No advertisments found in the database.");
        }else{
            System.out.println("Total number of advertisments found: " + adCount);
        }

    }

    /* creates the tables in the sql database */
    private static void initializeMarketingTables(){
        initializePromoTable();
        initializeFareFeesTable();

//        Add fare fees for testing
//        addFareFeeToDatabase("Bus A", 15.00);
//        addFareFeeToDatabase("Bus B", 21.00);
//        addFareFeeToDatabase("Bus C", 10.00);
//        addFareFeeToDatabase("Bus D", 18.00);

//        Add promos for testing
//        addPromoToDatabase("Bus A", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua");
//        addPromoToDatabase("Bus D", "Lorem ipsum dolor sit amet, consectetur");
    }

}
