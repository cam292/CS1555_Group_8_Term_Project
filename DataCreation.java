import java.util.*;
import java.io.*;

public class DataCreation{
  public static Random rand;
  public static String[] months = {"JAN", "FEB", "MAR", "APR", "MAY", "JUNE", "JULY", "AUG", "SEP", "OCT", "NOV", "DEC"};

  public static ArrayList<Pair<Integer, Integer>> friendsPairs = new ArrayList<Pair<Integer, Integer>>();
  public static ArrayList<ArrayList<Integer>> gidIds = new ArrayList<ArrayList<Integer>>();
  public static int numGroups = 10;
  public static int numProfiles = 101;
  public static int numMessages = 300;

  public static int[] inGid = new int[numGroups];

  public static void main(String[] args){
    rand = new Random();
    try{
      File dataFile = new File("phase1_sample_data.sql");
      FileWriter dataWriter = new FileWriter(dataFile, false);

      dataWriter.write("--Profile Inserts --\n");
      insertProfiles(dataWriter, numProfiles);
      dataWriter.write("\n--Pending Friends Inserts --\n");
      insertPendingFriends(dataWriter,numProfiles);
      dataWriter.write("\n--Friends Inserts --\n");
      insertFriends(dataWriter, numProfiles);
      dataWriter.write("\n--Group Inserts --\n");
      insertGroups(dataWriter, numGroups);
      dataWriter.write("\n--Group Member Inserts --\n");
      insertGroupMembers(dataWriter, 50);
      dataWriter.write("\n--Message Inserts --\n");
      insertMessages(dataWriter, numMessages);
      dataWriter.close();
    }
    catch(FileNotFoundException e1){

    }
    catch(IOException e2){

    }


  }

  public static void insertProfiles(FileWriter writer, int numProfiles){
    String[] firstNames = {"Michael", "Kate", "Craig", "David", "Bill", "Richard", "Rick", "Sam", "Mary", "Julia", "James", "Jimmy", "Abby", "Justin", "Patrick", "Mare", "Morgan", "Andrea", "Ali", "Jason"};

    String[] lastNames = {"Johnson", "Stevenson", "Sontag", "Cohen", "Blosser", "Aron", "Hanna", "Tepe", "Casper", "Wilson", "Steven", "Gaunt", "Green", "Bolten", "Tyrion", "Stark", "Tyrell", "Cooper", "Shaw", "Rolnick"};

    int num = 0;

    for(int id=1; id < numProfiles+1; id++){
      StringBuilder query = new StringBuilder("INSERT INTO profile VALUES ('");
      query.append(id+"', '");
      num = rand.nextInt(firstNames.length); //randomly select a first name
      query.append(firstNames[num]+" ");

      num = rand.nextInt(lastNames.length); //randomly select a last name
      query.append(lastNames[num]+"', '");

      query.append(genPassword()+"', TO_DATE('"); //pick a password

      num = rand.nextInt(months.length); //randomly select a month
      query.append(months[num]+"-");

      //picking a day
      if(num % 2 == 0){ //odd months have 30 days (offset 1 b/c of array)
        num = rand.nextInt(31);
      }else{
        if(num == 1){ //february is the exception
          num = rand.nextInt(29);
        }else{
          num = rand.nextInt(32);
        }
      }

      if(num < 10){
        query.append("0"+num+"-");
      }else{
        query.append(num+"-");
      }

      num = rand.nextInt(60); //pick a year from 1910-2010
      num += 1950;
      // System.out.println("Year generated: "+num);
      query.append(num+"', 'MMM-DD-YY'), TO_TIMESTAMP('");

      num = rand.nextInt(29); //just hard code a day less than 28
      if(num < 10){
        query.append("0"+num+"-");
      }else{
        query.append(num+"-");
      }

      num = rand.nextInt(months.length);
      query.append(months[num]+"-");

      num = rand.nextInt(18); //pick a year
      query.append(num+":");

      num = rand.nextInt(25); //pick an hour
      query.append(num+":");

      num = rand.nextInt(61); //pick a minute
      if(num < 10){
        query.append("0"+num+"', 'DD-MON-RR:HH24:MI');");
      }else{
        query.append(num+"', 'DD-MON-RR:HH24:MI');");
      }

      System.out.println(query.toString());
      try{
        System.out.println("trying to write to file");
        writer.write(query.toString()+'\n');
      } catch(IOException e1){
        System.out.println("Unable to write to file");
      }
      query.delete(0, query.length()-1);
    }

    return;
  }

  public static String genPassword(){
    Random rand = new Random();
    int num = 0;
    StringBuilder pass = new StringBuilder();
    char[] passChars = {'a', 'A', 'b', 'B', 'c', 'C', 'd', 'D', 'e', 'E', 'f', 'F', 'g', 'G', 'h', 'H', 'i', 'I', 'j', 'J', 'k', 'K', 'l', 'L', 'm', 'M', 'n', 'N', 'o', 'O', 'p', 'P', 'q', 'Q', 'r', 'R', 's', 'S', 't', 'T', 'u', 'U', 'v', 'V', 'w', 'W', 'x', 'X', 'y', 'Y', 'z', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '!', '@', '#', '$', '%', '^', '&', '*'};

    num = rand.nextInt(45); //make password randomly from 5 to 50 characters
    for(int i=0; i<num+5; i++){
      num = rand.nextInt(passChars.length); //select a character
      pass.append(passChars[num]);
    }

    return pass.toString();
  }
  public static void insertPendingFriends(FileWriter writer, int numFriendships){
    int num = 0;
    String[] messages = {"Lets be friends!", "How have you been?", "Long time no see", "Hi", "Heyyyy", "Whats up", "Hi friend", "Friends?"};
    for(int idOne=1; idOne < numProfiles+1; idOne++){// userID 1
      for(int idTwo=1; idTwo<6; idTwo++){ //add next 5 user id's to userID 1's friens
        StringBuilder query = new StringBuilder("INSERT INTO pendingFriends VALUES ('");
        query.append(idOne+"', '");

        int p1 = idOne;

        int id;
        if(idOne > 94){
          id = 1;
        }else{
          id=idOne;
        }

        int nextId = id + idTwo;
        query.append(nextId+"', '");

        num = rand.nextInt(messages.length);
        query.append(messages[num]+"');");

        System.out.println(query.toString());
        try{
          System.out.println("trying to write to file");
          writer.write(query.toString()+'\n');
        } catch(IOException e1){
          System.out.println("Unable to write to file");
        }
        query.delete(0, query.length()-1);
      }
    }
  }
  public static void insertFriends(FileWriter writer, int numFriendships){
    int num = 0;
    String[] messages = {"Lets be friends!", "How have you been?", "Long time no see", "Hi", "Heyyyy", "Whats up", "Hi friend", "Friends?"};
    for(int idOne=1; idOne < numProfiles+1; idOne++){// userID 1
      for(int idTwo=1; idTwo<6; idTwo++){ //add next 5 user id's to userID 1's friens
        StringBuilder query = new StringBuilder("INSERT INTO friends VALUES ('");
        query.append(idOne+"', '");

        int p1 = idOne;

        int id;
        if(idOne > 94){
          id = 1;
        }else{
          id=idOne;
        }

        int nextId = id + idTwo;
        query.append(nextId+"', TO_DATE('");

        int p2 = nextId;
        friendsPairs.add(new Pair(p1, p2));

        num = rand.nextInt(months.length); //randomly select a month
        query.append(months[num]+"-");

        //picking a day
        if(num % 2 == 0){ //odd months have 30 days (offset 1 b/c of array)
          num = rand.nextInt(31);
        }else{
          if(num == 1){ //february is the exception
            num = rand.nextInt(29);
          }else{
            num = rand.nextInt(32);
          }
        }

        if(num < 10){
          query.append("0"+num+"-");
        }else{
          query.append(num+"-");
        }

        num = rand.nextInt(18); //pick a year from 2000-2017
        num += 2000;
        // System.out.println("Year generated: "+num);
        query.append(num+"', 'MMM-DD-YY'), '");

        num = rand.nextInt(messages.length);
        query.append(messages[num]+"');");

        System.out.println(query.toString());
        try{
          System.out.println("trying to write to file");
          writer.write(query.toString()+'\n');
        } catch(IOException e1){
          System.out.println("Unable to write to file");
        }
        query.delete(0, query.length()-1);
      }
    }
  }

  public static void insertGroups(FileWriter writer, int numGroups){

    String[] groupNames = {"Best friends 4evr", "Gaming geeks", "Dnd bros", "Database class", "Study group", "Surprise party", "Charity group", "Church group", "Money makers", "Spring break group"};
    String[] groupDesc = {"WE ARE THE BESTEST FRIENDS", "Shush Im playing LoL", "DnD. All night. Every night.", "Best class? Yes.", "In this group we study for physics", "Surprise party for Andrew!", "Charity fundraiser group", "Friends in church", "Make money. No sketchiness.", "Planning for spring break!"};

    for(int i = 1; i <= numGroups; i++){
      StringBuilder query = new StringBuilder("INSERT INTO groups VALUES (");
      query.append("'" + i + "', '" + groupNames[i-1] + "', '" + groupDesc[i-1] + "');");
      System.out.println(query.toString());
      try{
        System.out.println("trying to write to file");
        writer.write(query.toString()+'\n');
      } catch(IOException e1){
        System.out.println("Unable to write to file");
      }
      query.delete(0, query.length()-1);
    }
  }

  public static void insertGroupMembers(FileWriter writer, int numToAdd){
    String[] msg = {"can i pls be in group", "Add me", "Can I be added please?", "Hey can I get in?", "Whats with all the hoopla?"};
    String[] roles = {"admin", "user"};

    for(int i = 0; i <= numGroups; i++){
      gidIds.add(i, new ArrayList<Integer>()); //initialize arrays
    }

    for(int i = 0; i < numToAdd; i++){

      StringBuilder query = new StringBuilder("INSERT INTO pendingGroupMembers VALUES (");
      StringBuilder query2 = new StringBuilder("INSERT INTO groupMembership VALUES (");

      int id = rand.nextInt(numProfiles)+1;
      int gid = rand.nextInt(numGroups)+1;
      inGid[gid-1]++;
      gidIds.get(gid).add(id);
      int msgNum = rand.nextInt(msg.length);
      query.append("'" + gid + "', '" + id + "', '" + msg[msgNum] + "');");

      if(inGid[gid-1] == 1){
        //first group member is admin
        query2.append("'" + gid + "', '" + id + "', '" + roles[0] + "');");
      } else {
        query2.append("'" + gid + "', '" + id + "', '" + roles[1] + "');");
      }

      System.out.println(query.toString());
      System.out.println(query2.toString());

      try{
        System.out.println("trying to write to file");
        writer.write(query.toString()+'\n');
        writer.write(query2.toString()+'\n');
      } catch(IOException e1){
        System.out.println("Unable to write to file");
      }

      query.delete(0, query.length()-1);
      query2.delete(0, query2.length()-1);
    }
  }

  public static void insertMessages(FileWriter writer, int numMessages){
    String[] msg = {"Hey", "Hi", "Hey bud", "I miss you!", "Lets hang out sometime?", "How was class today?", "CS is the best!", "Lunch?", "Lets go to dinner!", "Cant wait until Thanksgiving!", "Any fun plans this weekend?", "Bowling?"};
    String[] group = {"Hey guys!", "Hows everyone doin?", "GROUP DINNER!!!", "Party at my place tonight!!", "Whats everyone up to?", "Lets get this surprise party ready!"};

    for(int i = 1; i <= numMessages; i++){
      StringBuilder query = new StringBuilder("INSERT INTO messages VALUES (");
      //select random friend pair OR group
      int friendOrGroup = rand.nextInt(5);
      if(friendOrGroup == 0){
        //group message
        //get random group, random id
        int grp = rand.nextInt(gidIds.size());
        while(gidIds.get(grp).size() <= 0){
          grp = rand.nextInt(gidIds.size());
        }
        int id = gidIds.get(grp).get(rand.nextInt(gidIds.get(grp).size()));
        query.append("'" + i + "', '" + id + "', '" + group[rand.nextInt(group.length)] + "', NULL, '" + grp +"', ");
        int num = 0;
        query.append("TO_TIMESTAMP('");

        num = rand.nextInt(29); //just hard code a day less than 28
        if(num < 10){
          query.append("0"+num+"-");
        }else{
          query.append(num+"-");
        }

        num = rand.nextInt(months.length);
        query.append(months[num]+"-");

        num = rand.nextInt(18); //pick a year
        query.append(num+":");

        num = rand.nextInt(25); //pick an hour
        query.append(num+":");

        num = rand.nextInt(61); //pick a minute
        if(num < 10){
          query.append("0"+num+"', 'DD-MON-RR:HH24:MI');");
        }else{
          query.append(num+"', 'DD-MON-RR:HH24:MI'));");
        }

      } else {
        //friend message
        Pair pair = friendsPairs.get(rand.nextInt(friendsPairs.size()));
        query.append("'" + i + "', '" + pair.getFirst() + "', '" + msg[rand.nextInt(msg.length)] + "', '" + pair.getSecond() + "', NULL, ");
        int num = 0;
        query.append("TO_TIMESTAMP('");

        num = rand.nextInt(29); //just hard code a day less than 28
        if(num < 10){
          query.append("0"+num+"-");
        }else{
          query.append(num+"-");
        }

        num = rand.nextInt(months.length);
        query.append(months[num]+"-");

        num = rand.nextInt(18); //pick a year
        query.append(num+":");

        num = rand.nextInt(25); //pick an hour
        query.append(num+":");

        num = rand.nextInt(61); //pick a minute
        if(num < 10){
          query.append("0"+num+"', 'DD-MON-RR:HH24:MI');");
        }else{
          query.append(num+"', 'DD-MON-RR:HH24:MI'));");
        }
      }

      System.out.println(query.toString());
      try{
        System.out.println("trying to write to file");
        writer.write(query.toString()+'\n');
      } catch(IOException e1){
        System.out.println("Unable to write to file");
      }
      query.delete(0, query.length()-1);
    }
  }


  public static class Pair<F, S> {
    private F first; //first member of pair
    private S second; //second member of pair

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public void setFirst(F first) {
        this.first = first;
    }

    public void setSecond(S second) {
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }
}
}
