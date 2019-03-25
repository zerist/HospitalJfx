import java.security.interfaces.RSAKey;
import java.sql.*;

import javax.swing.text.DefaultEditorKit.InsertBreakAction;

public class TestDb {

	public static void main(String[] args) {
		String dName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		String dbURL = "jdbc:sqlserver://localhost:1433;DatabaseName=test";
		String name = "sa";
		String pwd = "1518079220";
		String t1 = null;
		String t2 = "";
		System.out.println(" " +t2.length());
		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection conn = null;
		try {
			Class.forName(dName);
			conn = DriverManager.getConnection(dbURL, name ,pwd);
			System.out.println("con");
			
			ps = conn.prepareStatement("select tid, num from dbo.Table_test");
			rs = ps.executeQuery();
			while(rs.next()) {
				String tid = rs.getString("tid");
				//String tname = rs.getString(3);
				//String createDate = rs.getString(4);
				String num = rs.getString(2);
				//String statusId = rs.getString(5);
				//System.out.println(tid+" "+num+" "+tname+" "+createDate+" "+statusId);
				System.out.println(tid + " " + num);
			}
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("fail : " + e.getMessage());
		}finally {
			try {
				if(rs != null) rs.close();
				if(ps != null) ps.close();
				if(conn != null) conn.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

}


