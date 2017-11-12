import java.util.*;
import java.io.*;

public class DataCreation{
  public static void main(String[] args){
    try{
      File dataFile = new File("phase1_sample_data.sql");
      FileWriter dataWriter = new FileWriter(dataFile, false);

      insertProfiles(dataWriter, 50);
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

    String[] months = {"JAN", "FEB", "MAR", "APR", "MAY", "JUNE", "JULY", "AUG", "SEP", "OCT", "NOV", "DEC"};

    Random rand = new Random();
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
}
