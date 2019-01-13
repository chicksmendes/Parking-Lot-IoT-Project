package org.eclipse.leshan.server.demo;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBase {
	String databaseName;
	Connection c = null;
	String name = null;
	
	public void deleteDataBase(String fileName) {
		
		 File file = new File(fileName); 
		 file.delete();
	}
	
	public void connect(String fileName) {
      try {
         Class.forName("org.sqlite.JDBC");
         c = DriverManager.getConnection("jdbc:sqlite:" + fileName + ".db");
      } catch ( Exception e ) {
         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
         System.exit(0);
      }
      name = fileName;
      System.out.println("Opened database successfully");
	}
	
	public void createTable() {
		Statement stmt = null;
		
		try {
//			Class.forName("org.sqlite.JDBC");
//	         c = DriverManager.getConnection("jdbc:sqlite:" + name + ".db");
//	         c.setAutoCommit(false);
//	        System.out.println("Opened database successfully");

			stmt = c.createStatement();
	         String sql1 = "CREATE TABLE PARK " +
	                        "(ID INT PRIMARY KEY  AUTOINCREMENT NOT NULL," +
	                        " ENDPOINT       TEXT    NOT NULL" + 
	                        " STATE          TEXT    NOT NULL, " + 
	                        " PLATE          TEXT    NOT NULL)"; 
	        stmt.executeUpdate(sql1);
	        c.commit();
	        stmt.close();
	         
	         stmt = c.createStatement();
	         String sql = "CREATE TABLE VEHICLE  " +
	                        "(ID INT PRIMARY KEY  AUTOINCREMENT   NOT NULL," +
	                        " PLATE          TEXT    NOT NULL, " + 
	                        " PARK           KEY    NOT NULL, " + 
	                        " STARTTIME      TEXT    NOT NULL, " + 
	                        " TIME           INT     NOT NULL, " + 
	                        " COST           FLOAT   NOT NULL)"; 
	         stmt.executeUpdate(sql);
	         c.commit();
	         stmt.close();
	        

	  	   System.out.println("Table created successfully");
      } catch ( Exception e ) {
    	  System.out.println("Table already exists");
      }
		
	}
	
	public void insertParkingSpot(String endpoint) {
		Statement stmt = null;
		
		try {
//			 Class.forName("org.sqlite.JDBC");
//	         c = DriverManager.getConnection("jdbc:sqlite:" + name + ".db");
//	         c.setAutoCommit(false);
//	         System.out.println("Opened database successfully");
	         stmt = c.createStatement();
	         String sql = "INSERT INTO PARK (ENDPOINT, STATE, PLATE) " +  "VALUES (" + endpoint + ", 'free', 'Vehicle-_-_');"; 
	         stmt.executeUpdate(sql);

	         stmt.close();
	         c.commit();
	      } catch ( Exception e ) {
	         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	         System.exit(0);
	      }
	      System.out.println("Records created successfully");
	}
	
	public void insertVehicle(String plate) {
		Statement stmt = null;
		
		try {
//			 Class.forName("org.sqlite.JDBC");
//	         c = DriverManager.getConnection("jdbc:sqlite:" + name + ".db");
//	         c.setAutoCommit(false);
//	         System.out.println("Opened database successfully");
	         stmt = c.createStatement();
	         String sql = "INSERT INTO VEHICLE (PLATE, PARK, STARTTIME,TIME, COST) " +
	                        "VALUES (" + plate + ", '-1',  datetime(timestamp, 'localtime') , 0 );"; 
	        // data('now')
	         stmt.executeUpdate(sql);

	         stmt.close();
	         c.commit();
	      } catch ( Exception e ) {
	         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	         System.exit(0);
	      }
	      System.out.println("Records created successfully");
	}
		
	public void selectPark() {
		   Statement stmt = null;
		   try {
		      
//			   Class.forName("org.sqlite.JDBC");
//		         c = DriverManager.getConnection("jdbc:sqlite:" + name + ".db");
//			      c.setAutoCommit(false);
//			      System.out.println("Opened database successfully");


		      stmt = c.createStatement();
		      ResultSet rs = stmt.executeQuery( "SELECT * FROM COMPANY;" );
		      
		      while ( rs.next() ) {
		         int id = rs.getInt("id");
		         String  name = rs.getString("name");
		         int age  = rs.getInt("age");
		         String  address = rs.getString("address");
		         float salary = rs.getFloat("salary");
		         
		         System.out.println( "ID = " + id );
		         System.out.println( "NAME = " + name );
		         System.out.println( "AGE = " + age );
		         System.out.println( "ADDRESS = " + address );
		         System.out.println( "SALARY = " + salary );
		         System.out.println();
		      }
		      rs.close();
		      stmt.close();
		   } catch ( Exception e ) {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      System.exit(0);
		   }
		   System.out.println("Operation done successfully");
	}
	
	public void selectVehicle() {
		   Statement stmt = null;
		   try {
		      
//			   Class.forName("org.sqlite.JDBC");
//		         c = DriverManager.getConnection("jdbc:sqlite:" + name + ".db");
//			      c.setAutoCommit(false);
//			      System.out.println("Opened database successfully");


		      stmt = c.createStatement();
		      ResultSet rs = stmt.executeQuery( "SELECT * FROM VEHICLE;" );
		      
		      while ( rs.next() ) {
		         int id = rs.getInt("id");
		         String  name = rs.getString("name");
		         int age  = rs.getInt("age");
		         String  address = rs.getString("address");
		         float salary = rs.getFloat("salary");
		         
		         System.out.println( "ID = " + id );
		         System.out.println( "NAME = " + name );
		         System.out.println( "AGE = " + age );
		         System.out.println( "ADDRESS = " + address );
		         System.out.println( "SALARY = " + salary );
		         System.out.println();
		      }
		      rs.close();
		      stmt.close();
		   } catch ( Exception e ) {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      System.exit(0);
		   }
		   System.out.println("Operation done successfully");
	}

	public void update() {
		Statement stmt = null;
		   
		   try {
		      stmt = c.createStatement();
		      String sql = "UPDATE COMPANY set SALARY = 25000.00 where ID=1;";
		      stmt.executeUpdate(sql);
		      c.commit();

		      ResultSet rs = stmt.executeQuery( "SELECT * FROM COMPANY;" );
		      
		      while ( rs.next() ) {
		         int id = rs.getInt("id");
		         String  name = rs.getString("name");
		         int age  = rs.getInt("age");
		         String  address = rs.getString("address");
		         float salary = rs.getFloat("salary");
		         
		         System.out.println( "ID = " + id );
		         System.out.println( "NAME = " + name );
		         System.out.println( "AGE = " + age );
		         System.out.println( "ADDRESS = " + address );
		         System.out.println( "SALARY = " + salary );
		         System.out.println();
		      }
		      rs.close();
		      stmt.close();
		   } catch ( Exception e ) {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      System.exit(0);
		   }
		    System.out.println("Operation done successfully");
		   
	}

	public void delete(String endpoint) {
		Statement stmt = null;
	      
	      try {
	         
	         stmt = c.createStatement();
	         String sql = "DELETE from PARK where ENDPOINT=" + endpoint + ";";
	         stmt.executeUpdate(sql);
	         c.commit();

	      
	      stmt.close();
	      } catch ( Exception e ) {
	         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	         System.exit(0);
	      }
	      System.out.println("Operation done successfully");
	}
	public void close() {
		try {
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
//		Connection conn = null;
//		try {
//			String url = "jbdc:sqlite:" + fileName;
//			try {
//				Class.forName("oracle.jdbc.driver.OracleDriver");
//			} catch (ClassNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			conn = DriverManager.getConnection(url);
//		}
//		catch (SQLException e) {
//			System.out.println(e.getMessage());
//		}
//		finally {
//			try {
//				if( conn != null) {
//					conn.close();
//				} 
//			}catch (SQLException e) {
//				System.out.println(e.getMessage());
//			}
//		}
//	}
//
//	public DataBase() {
//		
//	}
}

