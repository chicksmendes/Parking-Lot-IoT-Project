import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {
	String databaseName;
	

	public void connect(String fileName) {
		Connection conn = null;
		try {
			String url = "jbdc:sqlite:" + fileName;
			try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			conn = DriverManager.getConnection(url);
		}
		catch (SQLException e) {
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

	public DataBase() {
		
	}
}

