import java.util.*;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class FaceSpace{
	// JDBC driver name and database URL
	//static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "dbc:oracle:thin:@class3.cs.pitt.edu:1521:dbclass";

	static Connection conn = null;
	
	//  Database credentials
	static final String USER = "dpd30";
  	static final String PASS = "3924808"; //please don't steal this lol
	
	// Other variables
	static int profileIndex = 0;	//should be saved to a file after each use
   

	// Logged in user info
	static String myId;
	static String myName;

	public static void main(String[] args) {
		//use main to setup connection and then test functions
		//String classpath = System.getProperty("java.class.path");
		//System.out.println(classpath);
		try{
			//Register JDBC driver
			DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());
			//Class.forName("com.mysql.jdbc.Driver");

			//Open a connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
			
			//TODO: put this in logout function
			//Clean-up environment
			//conn.close();
		} catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		} catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}
		
		//test functions
		createUser("John Warwick", "jwarwick@gmail.com", "abab", new java.sql.Date(2017, 12, 5));
		
		Login("0", "abab");
		
	}
	
	public static void createUser(String name, String email, String pass, java.sql.Date dateOfBirth){
		/* WORKING EXAMPLE
		String query = "INSERT INTO profile VALUES('" + profileIndex + "', 'Ron Swanson', 'RS23@gmail.com', 'abab', TO_DATE('JUN-13-2009', 'MON-DD-YY'), TO_TIMESTAMP('23-JUN-18:12:23', 'DD-MON-YY:HH24:MI'))";
		try {
			System.out.println(query);
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.executeUpdate();
		} catch (Exception e){
			e.printStackTrace();
		}
		*/
		String timeStamp = "TO_TIMESTAMP('" + new SimpleDateFormat("dd-MMM-yy:HH:mm").format(new java.util.Date()) + "', 'DD-MON-YY:HH24:MI')"; 
		String birth = "TO_DATE('" + new SimpleDateFormat("MMM-dd-yy").format(dateOfBirth) + "', 'MON-DD-YY')";
		String query = "INSERT INTO profile VALUES( ? , ? , ? , ? , " + birth + " , " + timeStamp + " )"; //this is safe since an sql date has to be passed in, not a string
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, Integer.toString(profileIndex));
			pstmt.setString(2, name);
			pstmt.setString(3, email);
			pstmt.setString(4, pass);
			//System.out.println("birth: " + birth + ", timestamp: " + timeStamp);
			pstmt.executeUpdate();
		} catch (SQLException se){
			se.printStackTrace();
		}
		
		profileIndex++;
	}
	
	public static void Login(String id, String password){
		String query = "SELECT * FROM profile WHERE userid= ? AND password= ?";
		
		//read result set - if we have a match, set the user credentials in this program
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, id);
			pstmt.setString(2, password);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()){
				myId = rs.getString("userid");
				myName = rs.getString("name");
			}
			String timeStamp = "TO_TIMESTAMP('" + new SimpleDateFormat("dd-MMM-yy:HH:mm").format(new java.util.Date()) + "', 'DD-MON-YY:HH24:MI')";
			String query2 = "UPDATE profile SET lastlogin=" + timeStamp + " WHERE userid= ? AND password= ?";
			PreparedStatement pstmt2 = conn.prepareStatement(query2);
			pstmt2.setString(1, id);
			pstmt2.setString(2, password);
			pstmt2.executeQuery();
			System.out.println("Logged in as " + myName + "!");
			
		} catch (SQLException se){
			se.printStackTrace();
		}
	}
}
