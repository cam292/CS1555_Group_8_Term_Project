import java.util.*;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class FSDriver {

	static boolean loggedIn = false;
	static int exit = 0;

	public static void main(String[] args){
		FaceSpace.main(new String[0]);
		Scanner scan = new Scanner(System.in);
		while(exit != 1){
		while(!loggedIn && exit==0){
			//createUser("John Warwick", "jwarwick@gmail.com", "abab", new java.sql.Date(2017, 12, 5));
			System.out.println("Welcome to FaceSpace! If you would like to create a profile, type 1. If you would like to login, type 2. To exit, type 0.");
			String input = scan.nextLine();
			if(input.equals("1")){
				System.out.println("Please enter your name: ");
				String name = scan.nextLine();
				System.out.println("Please enter your email: ");
				String email = scan.nextLine();
				System.out.println("Please enter your password: ");
				String pass = scan.nextLine();
				System.out.println("Please enter the year you were born (number ex 2017): ");
				String yr = scan.nextLine();
				System.out.println("Please enter the month you were born (number): ");
				String mo = scan.nextLine();
				System.out.println("Please enter the day you were born (number): ");
				String dy = scan.nextLine();
				FaceSpace.createUser(name, email, pass, new java.sql.Date(Integer.parseInt(yr), Integer.parseInt(mo), Integer.parseInt(dy)));

			} else if (input.equals("2")){
				System.out.println("Please enter your userid: ");
				String id = scan.nextLine();
				System.out.println("Please enter your password: ");
				String pass = scan.nextLine();
				if(FaceSpace.Login(id, pass)){
					loggedIn=true;
				} else {
					loggedIn=false;
				}
			} else if (input.equals("0")){
				exit = 1;
			}
		}

		while(loggedIn){

			System.out.println("Hello! Please select an option from the menu:\n\t1 Initiate Friendship\n\t2 Confirm Friendship\n\t3 Display Friends\n\t4 Create Group\n\t5 Add to Group\n\t6 Message User\n\t7 Message Group\n\t8 Display Messages\n\t9 Display New Messages\n\t10 Top (k) users with most messages received over (x) months\n\t11 Search for User\n\t12 Find Friendship Path\n\t99 Delete Profile\n\t0 Log out");
			String input = scan.nextLine();
			if(input.equals("0")){
				FaceSpace.LogOut();
				System.out.println("Logged out!");
				loggedIn=false;
			} else if (input.equals("1")){
				System.out.println("Please enter the id of the user you'd like to send a friend request to:");
				String id = scan.nextLine();
				FaceSpace.InitiateFriendship(id);
			} else if (input.equals("2")){
				FaceSpace.ConfirmFriendship();
			} else if (input.equals("3")){
				FaceSpace.DisplayFriends();
			} else if (input.equals("4")){
				System.out.println("Please enter the group name:");
				String name = scan.nextLine();
				System.out.println("Please enter the member limit of the group (number):");
				String lim = scan.nextLine();
				System.out.println("Please enter the description of the group:");
				String desc = scan.nextLine();
				FaceSpace.CreateGroup(name, lim, desc);
			} else if (input.equals("5")){
				System.out.println("Please enter the id of the user you'd like to add to the group:");
				String uid = scan.nextLine();
				System.out.println("Please enter the id of the group you'd like to add to:");
				String gid = scan.nextLine();
				System.out.println("Please enter the message to go with the request:");
				String mess = scan.nextLine();
				FaceSpace.InitiateAddingGroup(uid, gid, mess);
			} else if (input.equals("6")){
				System.out.println("Please enter the id of the user you'd like to send a message to:");
				String id = scan.nextLine();
				FaceSpace.sendMessageToUser(String.valueOf(id));
			} else if (input.equals("7")){
				System.out.println("Please enter the id of the group you'd like to send a message to:");
				String id = scan.nextLine();
				FaceSpace.sendMessageToGroup(String.valueOf(id));
			} else if (input.equals("8")){
				FaceSpace.displayMessages();
			} else if (input.equals("9")){
				FaceSpace.displayNewMessages();
			} else if (input.equals("10")){
				System.out.println("Enter the (k) users you'd like to display:");
				String k = scan.nextLine();
				System.out.println("Enter the past (x) months you'd like to search:");
				String x = scan.nextLine();
				FaceSpace.topMessages(Integer.parseInt(k), Integer.parseInt(x));
			}else if (input.equals("11")){
				System.out.println("Please enter the search query:");
				String query = scan.nextLine();
				System.out.println();
				FaceSpace.SearchForUser(query);
			} else if (input.equals("12")){
				System.out.println("Please enter the id of the user you'd like to start from:");
				String id1 = scan.nextLine();
				System.out.println("Please enter the id of the user you'd like to end at:");
				String id2 = scan.nextLine();
				FaceSpace.ThreeDegrees(id1, id2);
			} else if (input.equals("99")){
				int id = FaceSpace.LogOut();
				FaceSpace.dropUser(id);
			}

		}
		}


	}
}
