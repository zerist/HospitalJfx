import java.sql.*;

public class TestDb {

	public static void main(String[] args) {
		String dName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		String dbURL = "jdbc:sqlserver://localhost:1433;DatabaseName=test";
		String name = "sa";
		String pwd = "1518079220";
		
		try {
			Class.forName(dName);
			Connection conn = DriverManager.getConnection(dbURL, name ,pwd);
			System.out.println("con");
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("fail : " + e.getMessage());
		}
		
	}

}


