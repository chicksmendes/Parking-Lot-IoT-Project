package org.eclipse.leshan.server.demo;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

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
                + "	billingrate double  null\n"
                + ");";
        
     // SQL statement for creating a new table
        String sql2 = "CREATE TABLE IF NOT EXISTS vehicle (\n"
                + "	plate text PRIMARY KEY,\n"
                + "	park text NOT NULL,\n"
                + "	time text not null,\n"
                + "	cost float not null\n"
                + ");";
        
        try (Connection conn = DriverManager.getConnection(databaseName);
                Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql1);
            stmt.execute(sql2);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }		
	}
	
	public void insertParkingSpot(String endpointName, double billingRate) {
		String sql = "INSERT INTO park(endpoint, state, plate) VALUES(?,?,?)";
		 
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, endpointName);
            pstmt.setString(2, "free");
            pstmt.setString(3, "---");
            //pstmt.setDouble(4, billingRate);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
		

	}
	
//	public void insertVehicle(String plate) {
//		Statement stmt = null;
//		
//		try {
////			 Class.forName("org.sqlite.JDBC");
////	         c = DriverManager.getConnection("jdbc:sqlite:" + name + ".db");
////	         c.setAutoCommit(false);
////	         System.out.println("Opened database successfully");
//	         stmt = c.createStatement();
//	         String sql = "INSERT INTO VEHICLE (PLATE, PARK, STARTTIME,TIME, COST) " +
//	                        "VALUES (" + plate + ", '-1',  datetime(timestamp, 'localtime') , 0 );"; 
//	        // data('now')
//	         stmt.executeUpdate(sql);
//
//	         stmt.close();
//	         c.commit();
//	      } catch ( Exception e ) {
//	         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
//	         System.exit(0);
//	      }
//	      System.out.println("Records created successfully");
//	}
//		
//	public void selectPark() {
//		   Statement stmt = null;
//		   try {
//		      
////			   Class.forName("org.sqlite.JDBC");
////		         c = DriverManager.getConnection("jdbc:sqlite:" + name + ".db");
////			      c.setAutoCommit(false);
////			      System.out.println("Opened database successfully");
//
//
//		      stmt = c.createStatement();
//		      ResultSet rs = stmt.executeQuery( "SELECT * FROM COMPANY;" );
//		      
//		      while ( rs.next() ) {
//		         int id = rs.getInt("id");
//		         String  name = rs.getString("name");
//		         int age  = rs.getInt("age");
//		         String  address = rs.getString("address");
//		         float salary = rs.getFloat("salary");
//		         
//		         System.out.println( "ID = " + id );
//		         System.out.println( "NAME = " + name );
//		         System.out.println( "AGE = " + age );
//		         System.out.println( "ADDRESS = " + address );
//		         System.out.println( "SALARY = " + salary );
//		         System.out.println();
//		      }
//		      rs.close();
//		      stmt.close();
//		   } catch ( Exception e ) {
//		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
//		      System.exit(0);
//		   }
//		   System.out.println("Operation done successfully");
//	}
//	
//	public void selectVehicle() {
//		   Statement stmt = null;
//		   try {
//		      
////			   Class.forName("org.sqlite.JDBC");
////		         c = DriverManager.getConnection("jdbc:sqlite:" + name + ".db");
////			      c.setAutoCommit(false);
////			      System.out.println("Opened database successfully");
//
//
//		      stmt = c.createStatement();
//		      ResultSet rs = stmt.executeQuery( "SELECT * FROM VEHICLE;" );
//		      
//		      while ( rs.next() ) {
//		         int id = rs.getInt("id");
//		         String  name = rs.getString("name");
//		         int age  = rs.getInt("age");
//		         String  address = rs.getString("address");
//		         float salary = rs.getFloat("salary");
//		         
//		         System.out.println( "ID = " + id );
//		         System.out.println( "NAME = " + name );
//		         System.out.println( "AGE = " + age );
//		         System.out.println( "ADDRESS = " + address );
//		         System.out.println( "SALARY = " + salary );
//		         System.out.println();
//		      }
//		      rs.close();
//		      stmt.close();
//		   } catch ( Exception e ) {
//		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
//		      System.exit(0);
//		   }
//		   System.out.println("Operation done successfully");
//	}
//
//	public void update() {
//		Statement stmt = null;
//		   
//		   try {
//		      stmt = c.createStatement();
//		      String sql = "UPDATE COMPANY set SALARY = 25000.00 where ID=1;";
//		      stmt.executeUpdate(sql);
//		      c.commit();
//
//		      ResultSet rs = stmt.executeQuery( "SELECT * FROM COMPANY;" );
//		      
//		      while ( rs.next() ) {
//		         int id = rs.getInt("id");
//		         String  name = rs.getString("name");
//		         int age  = rs.getInt("age");
//		         String  address = rs.getString("address");
//		         float salary = rs.getFloat("salary");
//		         
//		         System.out.println( "ID = " + id );
//		         System.out.println( "NAME = " + name );
//		         System.out.println( "AGE = " + age );
//		         System.out.println( "ADDRESS = " + address );
//		         System.out.println( "SALARY = " + salary );
//		         System.out.println();
//		      }
//		      rs.close();
//		      stmt.close();
//		   } catch ( Exception e ) {
//		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
//		      System.exit(0);
//		   }
//		    System.out.println("Operation done successfully");
//		   
//	}
//
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
	
//	public void close() {
//		try {
//			c.close();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
////		Connection conn = null;
////		try {
////			String url = "jbdc:sqlite:" + fileName;
////			try {
////				Class.forName("oracle.jdbc.driver.OracleDriver");
////			} catch (ClassNotFoundException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
////			conn = DriverManager.getConnection(url);
////		}
////		catch (SQLException e) {
////			System.out.println(e.getMessage());
////		}
////		finally {
////			try {
////				if( conn != null) {
////					conn.close();
////				} 
////			}catch (SQLException e) {
////				System.out.println(e.getMessage());
////			}
////		}
////	}
////
////	public DataBase() {
////		
////	}
}

