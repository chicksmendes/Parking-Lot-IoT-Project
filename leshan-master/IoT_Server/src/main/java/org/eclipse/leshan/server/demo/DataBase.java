package org.eclipse.leshan.server.demo;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class DataBase {
	// SQLite connection string
	String databaseName;
	
	public void deleteDataBase(String fileName) {
		
		 File file = new File(fileName); 
		 file.delete();
	}
	
	public void createDataBase(String fileName) {
		databaseName = "jdbc:sqlite:" + fileName + ".db/";
		 
        try (Connection conn = DriverManager.getConnection(databaseName)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }
 
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
	}
	
	private Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(databaseName);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
	
	public void createTable() {
		
        // SQL statement for creating a new table
        String sql1 = "CREATE TABLE IF NOT EXISTS park (\n"
                + "	endpoint text PRIMARY KEY,\n"
                + "	state text NOT NULL,\n"
                + "	plate text not null,\n"
                + "	billingrate double  null,\n"
                + "	yvalue int  null,\n"
                + "	counter int not null\n"
                + ");";
        
     // SQL statement for creating a new table
        String sql2 = "CREATE TABLE IF NOT EXISTS vehicle (\n"
                + "	plate text PRIMARY KEY,\n"
                + "	park text NOT NULL,\n"
                + "	time text not null,\n"
                + "	cost double not null\n"
                + ");";
        
        try (Connection conn = DriverManager.getConnection(databaseName);
                Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql1);
            stmt.execute(sql2);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Table Created");
	}
	
	public void updateState(String endpointName, String state) {
		String sql = "UPDATE park SET state= ? " + "WHERE endpoint= ?";
		
		try(Connection conn = this.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			 
            // set the corresponding param
            pstmt.setString(1, state);
            pstmt.setString(2, endpointName);
            // update 
            pstmt.executeUpdate();
		}
		catch (SQLException e) {
            System.out.println(e.getMessage());
		}
	}
	
	public String readState(String endpoint) {
		String sql = "SELECT state FROM park WHERE endpoint = ?";	
		String state = null;
		try (Connection conn = this.connect();
            PreparedStatement pstmt  = conn.prepareStatement(sql)){
            
            // set the value
            pstmt.setString(1,endpoint);
            //
            ResultSet rs  = pstmt.executeQuery();
            
            // loop through the result set
            while (rs.next()) {
            	state = rs.getString("state");
                System.out.println(state);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
		
		return state;
	}
	
	public void insertParkingSpot(String endpointName, double billingRate) {
		String sql = "INSERT INTO park(endpoint, state, plate, billingrate, yvalue, counter) VALUES(?,?,?,?,?,?)";
		 
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, endpointName);
            pstmt.setString(2, "free");
            pstmt.setString(3, "---");
            pstmt.setDouble(4, billingRate);
            pstmt.setInt(5, -100);
            pstmt.setInt(6, 0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
	}
	
	public void insertVehicle(String plate) {
		String sql = "INSERT INTO vehicle(plate, park, time, cost) VALUES (?,?,?,?)";
		
		try(Connection conn = this.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, plate);
			pstmt.setString(2, "-1");
			pstmt.setString(3, "---");
			pstmt.setDouble(4, 0.0);
			pstmt.executeUpdate();
		} catch (SQLException e) {
            System.out.println(e.getMessage());
        }
	}

	public void selectPark() {
		String sql = "SELECT * FROM park";	
		try(Connection conn = this.connect();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql)){
			while (rs.next()) {
		         
		         String endpoint = rs.getString("endpoint");
		         String state = rs.getString("endpoint");
		         String plate = rs.getString("plate");
		         Double  billingrate= rs.getDouble("billingrate");
		        
		         System.out.println( "ENDPOINT = " + endpoint );
		         System.out.println( "STATE = " + state );
		         System.out.println( "PLATE = " + plate );
		         System.out.println( "BILLINGRATE = " + billingrate );
		      }
		} catch (SQLException e) {
            System.out.println(e.getMessage());
        }
	}
	


	public void selectVehicle() {
		String sql = "SELECT * FROM vehicle";
		try(Connection conn = this.connect();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql)){
			while (rs.next()) {
		         
				 String plate = rs.getString("plate");
				 String park = rs.getString("park");
				 String time = rs.getString("time");
		         Double cost= rs.getDouble("cost");
		         
		         System.out.println( "PLATE = " + plate );
		         System.out.println( "PARK = " + park );
		         System.out.println( "TIME = " + time );
		         System.out.println( "COST = " + cost );
		      }
		} catch (SQLException e) {
            System.out.println(e.getMessage());
        }
	}
	

	
	public void update(String endpoint, String state) {
		
		String sql = "UPDATE park SET state= ? , " + "WHERE endpoint= ?";
		String sqL = "SELECT * FROM COMPANY";
		
		try(Connection conn = this.connect();
				PreparedStatement stmt = conn.prepareStatement(sql)){
				stmt.executeUpdate();
		
				ResultSet rs = stmt.executeQuery(sqL);
			while (rs.next()) {
				String endpointt = rs.getString("endpoint");
				String statee = rs.getString("endpoint");
				String plate = rs.getString("plate");
				Double  billingrate= rs.getDouble("billingrate");
  
				System.out.println( "ENDPOINT = " + endpointt );
				System.out.println( "STATE = " + statee );
				System.out.println( "PLATE = " + plate );
				System.out.println( "BILLINGRATE = " + billingrate );
			}
		}
		catch (SQLException e) {
            System.out.println(e.getMessage());
		}			
	}	
	
	

	public void delete(String endpointName) {
		String sql = "DELETE FROM park WHERE endpoint = ?";
		 
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
 
            // set the corresponding param
            pstmt.setString(1, endpointName);
            // execute the delete statement
            pstmt.executeUpdate();
 
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
	}
	

	public void connection(String fileName) {
		Connection conn = null;
		try {
			String url = "jbdc:sqlite:" + fileName;
			conn = DriverManager.getConnection(url);
			
			System.out.println("Connected.");
		} catch (SQLException e) {
		System.out.println(e.getMessage());
		}
		finally {
			try {
				if( conn != null) {
					conn.close();
				} 
			}catch (SQLException e) {
				System.out.println(e.getMessage());
				}
			}
		}
	

}


