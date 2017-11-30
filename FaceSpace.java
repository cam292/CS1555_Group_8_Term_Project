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
	static int profileIndex = 1;	//should be saved to a file after each use
   

	// Logged in user info
	static String myId;
	static String myName;

	// Input
	static Scanner scan;

	public static void main(String[] args) {

		scan = new Scanner(System.in);

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
		
		//TODO: create actual menu
		//test functions
		createUser("John Warwick", "jwarwick@gmail.com", "abab", new java.sql.Date(2017, 12, 5));
		createUser("Ron Swanson", "rs23@gmail.com", "cbcb", new java.sql.Date(2017, 5, 4));

		Login("1", "abab");
		
		InitiateFriendship("2");

		Login("2", "cbcb");

		ConfirmFriendship();

		DisplayFriends();

		scan.close();
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

	public static void InitiateFriendship(String toId){
		try{
			if(myId == "" || myId == null){
				System.out.println("You are not logged in!");
				return;
			}
			String query = "INSERT INTO pendingFriends VALUES(" + myId + ", ? , ? )";
			System.out.println("Please enter the message to go along with the friend request: ");
			String message = scan.nextLine();
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, toId);
			pstmt.setString(2, message);
			System.out.println("\nAre you sure you want to send the friend request? 'y' or 'n': ");
			String res = scan.nextLine();
			if(res.equals("y") || res.equals("yes")){
				pstmt.executeQuery();
				System.out.println("Request sent!");
			} else {
				System.out.println("Request not sent!");
			}
		} catch(SQLException se){
			se.printStackTrace();
		}
	}

	public static void ConfirmFriendship(){
		try {
			String done="";
			while(!done.equals("-2")){
			if(myId == "" || myId == null){
				System.out.println("You are not logged in!");
				return;
			}
			String query = "SELECT * FROM pendingfriends p JOIN profile f on p.fromid=f.userid WHERE toid=" + myId;
			PreparedStatement pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();
			int i = 0;
			ArrayList<String> fromIds = new ArrayList<String>();
			ArrayList<String> messages = new ArrayList<String>();
			System.out.println("Here are your following friend requests: ");
			while(rs.next()){
				System.out.println(i + ": " + rs.getString("name") + " (" + rs.getString("fromid") + "), " + rs.getString("message"));
				fromIds.add(rs.getString("fromId"));
				messages.add(rs.getString("message"));
				i++;
			}
			
			//get pendingGroupMembers where myId in groupMembership and role="admin"
			query = "SELECT * FROM (SELECT * FROM (SELECT * FROM groupMembership WHERE userid=" + myId + " AND role='admin') g JOIN pendingGroupMembers p ON g.gid=p.gid) n JOIN profile f ON userId=f.userId";
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();
			System.out.println("Here are all of your following group requests: ");
			
			ArrayList<String> gids = new ArrayList<String>();
			ArrayList<String> gFromIds = new ArrayList<String>();
			ArrayList<String> gMessages = new ArrayList<String>();
			int groupStart = i;
			while(rs.next()){
				System.out.println(i + ": Group " + rs.getString("gid") + ", " + rs.getString("name") + " (" + rs.getString("userId") + "), " + rs.getString("message"));
				gids.add(rs.getString("gid"));
				gFromIds.add(rs.getString("userId"));
				gMessages.add(rs.getString("message"));
				i++;
			}
			System.out.println("Please select a number to accept, or type \"-1\" for all. Once you are done, type \"-2\"");
			done=scan.nextLine();
			if(done.equals("-1")){
				System.out.println("Accepting all!");
				for(int j = 0; j < i; j++){
					int acceptNum = j;
					String date = "TO_DATE('" + new SimpleDateFormat("MMM-dd-yy").format(new java.util.Date()) + "', 'MON-DD-YY')";
					if(acceptNum >= i){
						System.out.println("Invalid number.");
					} else {
						String query2="";
						if(acceptNum < groupStart){
							query2="INSERT INTO friends VALUES('" + myId + "', '" + fromIds.get(acceptNum) + "', " + date + ", '" + messages.get(acceptNum) + "')";
							PreparedStatement pstmt3=conn.prepareStatement("INSERT INTO friends VALUES('" + fromIds.get(acceptNum) + "', '" + myId + "', " + date + ", '" + messages.get(acceptNum) + "')");
							pstmt3.executeQuery();
							
						} else {
							query2="INSERT INTO groupMembership VALUES('" + gids.get(acceptNum) + "', '" + gFromIds.get(acceptNum) + "', 'member')";
						}
						PreparedStatement pstmt2 = conn.prepareStatement(query2);
						pstmt2.executeQuery();
					}
				}
			} else if (Integer.parseInt(done) >= 0){
				int acceptNum = Integer.parseInt(done);
				String date = "TO_DATE('" + new SimpleDateFormat("MMM-dd-yy").format(new java.util.Date()) + "', 'MON-DD-YY')";
				if(acceptNum >= i){
					System.out.println("Invalid number.");
				} else {
					String query2="";
					if(acceptNum < groupStart){
						query2="INSERT INTO friends VALUES('" + myId + "', '" + fromIds.get(acceptNum) + "', " + date + ", '" + messages.get(acceptNum) + "')";
						PreparedStatement pstmt3=conn.prepareStatement("INSERT INTO friends VALUES('" + fromIds.get(acceptNum) + "', '" + myId + "', " + date + ", '" + messages.get(acceptNum) + "')");
						pstmt3.executeQuery();
					} else {
						query2="INSERT INTO groupMembership VALUES('" + gids.get(acceptNum) + "', '" + gFromIds.get(acceptNum) + "', 'member')";
					}
					PreparedStatement pstmt2 = conn.prepareStatement(query2);
					pstmt2.executeQuery();
				}
			}
	
			} //finished

			System.out.println("Done adding. All other requests have been rejected.");
			//run delete from statements		
			String delQuery = "DELETE FROM pendingFriends p WHERE p.toId=" + myId;
			PreparedStatement delPrep = conn.prepareStatement(delQuery);
			delPrep.executeQuery();

		} catch (SQLException se){
			se.printStackTrace();
		}
	}

	public static void DisplayFriends(){
		try{
			String done="-1";
			//each friend tuple stored as (1,2) and (2,1) for simplicity
			String friendQuery = "SELECT * FROM friends f JOIN profile p ON f.userid2=p.userid WHERE f.userid1=" + myId;
			PreparedStatement pstmt = conn.prepareStatement(friendQuery);
			ResultSet rs = pstmt.executeQuery();
			System.out.println("Here are all of your friends: ");
			ArrayList<String> friendIds = new ArrayList<String>();
			ArrayList<String> friendNames = new ArrayList<String>();
			ArrayList<String> friendEmails = new ArrayList<String>();
			ArrayList<String> friendDob = new ArrayList<String>();
			while(rs.next()){
				System.out.println("Id: " + rs.getString("userid2") + "\t\tName: " + rs.getString("name")); 
				friendIds.add(rs.getString("userid2"));
				friendNames.add(rs.getString("name"));
				friendEmails.add(rs.getString("email"));
				friendDob.add(rs.getString("date_of_birth"));
			}
			System.out.println("\n\nHere are all of your friends' friends: ");
			for(int i = 0; i < friendIds.size(); i++){
				String friendQuery2 = "SELECT * FROM friends f JOIN profile p ON f.userid2=p.userid WHERE f.userid1=" + friendIds.get(i);
				PreparedStatement pstmt2 = conn.prepareStatement(friendQuery2);
				ResultSet rs2 = pstmt2.executeQuery();
				while(rs2.next()){
					if(!rs2.getString("userid2").equals(myId)){
						System.out.println("Id: " + rs2.getString("userid2") + "\t\tName: " + rs2.getString("name") + "\t\tFriend of: " + friendNames.get(i));
					}
				}
			}
			while(!done.equals("0")){
			System.out.println("Please enter a userID of a friend to view their profile or '0' to return to the menu.");
			done = scan.nextLine();
			if(!done.equals("0")){
				if(friendIds.contains(done)){
					int index = 0;
					for(int i = 0; i < friendIds.size(); i++){
						if(friendIds.get(i)==done){
							index = i;
						}
					}
					System.out.println("Id: " + friendIds.get(index) + "\t\tName: " + friendNames.get(index) + "\t\tEmail: " + friendEmails.get(index) + "\t\tDate of birth: " + friendDob.get(index));
				} else {
					System.out.println("Invalid id or not a friend. Please try again.");
				}
			}
			} //return to menu
		} catch (SQLException se){
			se.printStackTrace();
		}
	}

}
