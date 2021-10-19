import java.io.*;  
import java.sql.*;
import java.util.Scanner;

public class csvRead{  

    public static void titles(PreparedStatement prep) throws Exception{ 
        // Data values and helper variables 
        int count = 0;
        int tracker = 0;
        int batchNum = 0;
        String titleID = "";
        String titleType = "";
        String originalTitle = "";
        int runtimeMinutes = 0;
        String genres = "";
        int year = 0;
        double averageRating = 0.0;
        int numVotes = 0;
        String temp = "";
        String allIDs = "";

        // Iterate through the file
        Scanner sc = new Scanner(new File("data/titles.csv"), "UTF-8");  
        sc.useDelimiter("\t");
        sc.nextLine();
        while (sc.hasNext()){
            temp = sc.next();

            // Exclude newline characters
            if(temp.contains("\n")){
                temp = temp.substring(0, temp.indexOf("\n"));
            }

            // System.out.print("\n" + temp +"\n" + "count = " + count + "\n");
            
            // Read the data to the correct variable based on location in the file
            switch(count){
                case 1: titleID = temp; break;
                case 2: titleType = temp; break;
                case 3: originalTitle = temp; break;
                case 6: 
                    try{
                        runtimeMinutes = Integer.parseInt(temp); break;
                    } catch(Exception e){
                        runtimeMinutes = -1; break;
                    }
                case 7: genres = temp; break;
                case 8: 
                    try{
                        year = Integer.parseInt(temp); break;
                    } catch(Exception e){
                        year = -1; break;
                    }
                case 9: 
                    try{
                        averageRating = Double.parseDouble(temp); break;
                    } catch(Exception e){
                        averageRating = -1; break;
                    }
                case 10: 
                    try{
                        numVotes = Integer.parseInt(temp); break;
                    } catch(Exception e){
                        numVotes = -1; break;
                    }
                default: break;
            }
            ++count;

            // Once end of a row has been reached
            if(count == 11){ 
                ++tracker;
                count = 1;

                // If the title has not already been added
                if(!allIDs.contains(titleID)){
                    allIDs = allIDs + titleID + " ";
                    
                    // Set the parameters and add statement to batch, then clear the parameters
                    prep.setString(1, titleID);
                    prep.setString(2, titleType);
                    prep.setString(3, originalTitle);
                    prep.setInt(4, runtimeMinutes);
                    prep.setString(5, genres);
                    prep.setInt(6, year);  
                    prep.setDouble(7, averageRating);
                    prep.setInt(8, numVotes);
                    prep.addBatch();
                    prep.clearParameters();

                    // Every 5000 rows, submit the batch
                    if(tracker%5000 == 0){
                        // System.out.println("Titles - Entries Completed: " + tracker);
                        try{
                            int[] ret = prep.executeBatch();
                            // System.out.println("Titles Return Values Batch #" + batchNum + ":\n" + ret + "\n");
                            ++batchNum;
                        } catch(Exception e){
                            System.out.println("Titles failed to execute insert batch" + batchNum + "\n");
                            System.out.println("Exception = " + e);
                        }
                    }

                    // System.out.println("titleID = " + titleID);
                    // System.out.println("titleType = " + titleType);
                    // System.out.println("originalTitle = " + originalTitle);
                    // System.out.println("runtimeMinutes = " + runtimeMinutes);
                    // System.out.println("genres = " + genres);
                    // System.out.println("year = " + year);
                    // System.out.println("averageRating = " + averageRating);
                    // System.out.println("numVotes = " + numVotes);
                    // System.out.println("\n");
                }
            }
        }
        
        // Execute the final batch
        try{
            int[] ret = prep.executeBatch();
            // System.out.println("Titles Return Values Batch #" + batchNum + ":\n" + ret + "\n");
            ++batchNum;
        } catch(Exception e){
            System.out.println("Titles failed to execute insert batch" + batchNum + "\n");
        }
       
        //System.out.println(allIDs);
        System.out.println("Titles - Entries Finished With: " + tracker);
        sc.close();
    }  

    // Follows the same patter as titles()
    public static void custRating(PreparedStatement prep) throws Exception{  
        int count = 0;
        int tracker = 0;
        int batchNum = 0;
        int customerID = 0;
        double rating = 0.0;
        String date = "";
        String titleID = "";
        String temp = "";

        Scanner sc = new Scanner(new File("data/customer_ratings.csv"),"UTF-8");  
        sc.useDelimiter("\t");
        sc.nextLine();
        while (sc.hasNext()){
            temp = sc.next();
            if(temp.contains("\n")){
                temp = temp.substring(0, temp.indexOf("\n"));
            }
            // System.out.print("\n" + temp +"\n" + "count = " + count + "\n");  //find and returns the next complete token from this scanner  
            switch(count){
                case 1: 
                    try{
                        customerID = Integer.parseInt(temp); break;
                    } catch(Exception e){
                        customerID = -1; break;
                    }
                case 2: 
                    try{
                        rating = Double.parseDouble(temp); break;
                    } catch(Exception e){
                        rating = -1; break;
                    }
                case 3: date = temp; break;
                case 4: titleID = temp; break;
                default: break;
            }
            ++count;
            if(count == 5){ 
                ++tracker;
                count = 1;

                prep.setInt(1, customerID);
                prep.setDouble(2, rating);
                prep.setDate(3, Date.valueOf(date));
                prep.setString(4, titleID);
                prep.addBatch();
                prep.clearParameters();

                if(tracker%5000 == 0){
                    // System.out.println("Customer Ratings - Entries Completed: " + tracker);
                    try{
                        int[] ret = prep.executeBatch();
                        // System.out.println("Customer Ratings Return Values Batch #" + batchNum + ":\n" + ret + "\n");
                        ++batchNum;
                    } catch(Exception e){
                        System.out.println("Customer failed to execute insert batch " + batchNum + "\n");
                        System.out.println("Error: " + e);
                    }
                }

                // System.out.println("customerID = " + customerID);
                // System.out.println("rating = " + rating);
                // System.out.println("date = " + date);
                // System.out.println("titleID = " + titleID);
            }
        } 
        try{
            int[] ret = prep.executeBatch();
            // System.out.println("Customer Ratings Return Values Batch #" + batchNum + ":\n" + ret + "\n");
            ++batchNum;
        } catch(Exception e){
            System.out.println("Customer failed to execute insert batch " + batchNum + "\n");
        }


        System.out.println("Customer Ratings - Entries Finished With: " + tracker);
        sc.close();
    }  

    // Follows the same patter as titles()
    public static void names(PreparedStatement prep) throws Exception{  
        int count = 0;
        int tracker = 0;
        int batchNum = 0;
        String nconst = "";
        String primaryName = "";
        int birthYear = 0;
        int deathYear = 0;
        String primaryProfession = "";
        String temp = "";

        Scanner sc = new Scanner(new File("data/names.csv"),"UTF-8");  
        sc.useDelimiter("\t");
        sc.nextLine();
        while (sc.hasNext() || sc.hasNextLine()){
            temp = sc.next();
            // System.out.print("\n" + temp +"\n" + "count = " + count + "\n");  //find and returns the next complete token from this scanner  
            if(temp.contains("\n")){
                temp = temp.substring(0, temp.indexOf("\n"));
                // System.out.println("Here with temp = " + temp);
            }
            switch(count){
                case 1: nconst = temp; break;
                case 2: primaryName = temp; break;
                case 3:
                    if(temp == "\t"){
                        birthYear = -1; break;
                    } else{
                        try{
                            birthYear = Integer.parseInt(temp); break;
                        } catch(Exception e){
                            birthYear = -1; break;
                        }
                    }
                case 4: 
                    if(temp == "\t"){
                        deathYear = -1; break;
                    } else{
                        try{
                            deathYear = Integer.parseInt(temp); break;
                        } catch(Exception e){
                            deathYear = -1; break;
                        }
                    }
                case 5: primaryProfession = temp; break;
                default: break;
            }
            ++count;
            if(count == 6){ 
                ++tracker;
                count = 1;

                prep.setString(1, nconst);
                prep.setString(2, primaryName);
                prep.setInt(3, birthYear);
                prep.setInt(4, deathYear);
                prep.setString(5, primaryProfession);
                prep.addBatch();
                prep.clearParameters();

                if(tracker%5000 == 0){
                    // System.out.println("Names - Entries Completed: " + tracker);
                    try{
                        int[] ret = prep.executeBatch();
                        // System.out.println("Names Return Values Batch #" + batchNum + ":\n" + ret + "\n");
                        ++batchNum;
                    } catch(Exception e){
                        System.out.println("Names failed to execute insert batch" + batchNum + "\n");
                    }
                }

                // System.out.println("nconst = " + nconst);
                // System.out.println("primaryName = " + primaryName);
                // System.out.println("birthYear = " + birthYear);
                // System.out.println("deathYear = " + deathYear);
                // System.out.println("primaryProfession = " + primaryProfession);
            }
        }
        try{
            int[] ret = prep.executeBatch();
            // System.out.println("Names Return Values Batch #" + batchNum + ":\n" + ret + "\n");
            ++batchNum;
        } catch(Exception e){
            System.out.println("Names failed to execute insert batch" + batchNum + "\n");
        }

        System.out.println("Names - Entries Finished With: " + tracker);
        sc.close();
    }  

    // Follows the same patter as titles()
    public static void crew(PreparedStatement prep) throws Exception{  
        int count = 0;
        int tracker = 0;
        int batchNum = 0;
        String titleID = "";
        String directors = "";
        String writers = "";
        String temp = "";

        Scanner sc = new Scanner(new File("data/crew.csv"),"UTF-8");  
        sc.useDelimiter("\t");
        sc.nextLine();
        while (sc.hasNext()){
            temp = sc.next();
            if(temp.contains("\n")){
                temp = temp.substring(0, temp.indexOf("\n"));
            }
            // System.out.print("\n" + temp +"\n" + "count = " + count + "\n");  //find and returns the next complete token from this scanner  
            switch(count){
                case 1: titleID = temp; break;
                case 2:
                    if(temp == "\t"){
                        directors = null; break;
                    } else{
                        directors = temp; break;
                    }
                case 3: 
                    if(temp == "\t"){
                        writers = null; break;
                    } else{
                        writers = temp; break;
                    }
                default: break;
            }
            ++count;
            if(count == 4){ 
                ++tracker;
                count = 1;

                prep.setString(1, directors);
                prep.setString(2, writers);
                prep.setString(3, titleID);
                prep.addBatch();
                prep.clearParameters();

                if(tracker%5000 == 0){
                    // System.out.println("Crew - Entries Completed: " + tracker);
                    try{
                        int[] ret = prep.executeBatch();
                        // System.out.println("Crew Return Values Batch #" + batchNum + ":\n" + ret + "\n");
                        ++batchNum;
                    } catch(Exception e){
                        System.out.println("Crew failed to execute insert batch" + batchNum + "\n");
                    }
                }

                // System.out.println("titleID = " + titleID);
                // System.out.println("directors = " + directors);
                // System.out.println("writers = " + writers);
            }
        } 
        try{
            int[] ret = prep.executeBatch();
            // System.out.println("Crew Return Values Batch #" + batchNum + ":\n" + ret + "\n");
            ++batchNum;
        } catch(Exception e){
            System.out.println("Crew failed to execute insert batch" + batchNum + "\n");
        }

        System.out.println("Crew - Entries Finished With: " + tracker);
        sc.close();
    }  

    // Follows the same patter as titles()
    public static void principals(PreparedStatement prep) throws Exception{  
        int count = 0;
        int tracker = 0;
        int batchNum = 0;
        String titleID = "";
        String nconst = "";
        String category = "";
        String temp = "";

        Scanner sc = new Scanner(new File("data/principals.csv"),"UTF-8");  
        sc.useDelimiter("\t");
        sc.nextLine();
        while (sc.hasNext()){
            temp = sc.next();
            if(temp.contains("\n")){
                temp = temp.substring(0, temp.indexOf("\n"));
            }
            // System.out.print("\n" + temp +"\n" + "count = " + count + "\n");  //find and returns the next complete token from this scanner  
            switch(count){
                case 1: titleID = temp; break;
                case 2: nconst = temp; break;
                case 3: category = temp; sc.nextLine(); break;
                default: break;
            }
            // System.out.println("titleID -"  + titleID + ", nconst - " + nconst + ", category - " + category);
            ++count;
            if(count == 4){ 
                ++tracker;
                count = 0;
                String director, writer, actor, actress = ""; 
                if(category.contains("director")){
                    director = ", " + nconst; 
                    writer="";
                    actor="";
                    actress = "";
                } else if (category.contains("writer")){
                    director = "";
                    writer= ", " + nconst; 
                    actor="";
                    actress = "";
                } else if(category.contains("actor")){
                    director = "";
                    writer="";
                    actor = ", " + nconst; 
                    actress = "";
                } else if(category.contains("actress")){
                    director = "";
                    writer="";
                    actor="";
                    actress = ", " + nconst; 
                } else {
                    director = "";
                    writer="";
                    actor="";
                    actress = "";
                }
                    prep.setString(1, director);
                    prep.setString(2, writer);
                    prep.setString(3, actor);
                    prep.setString(4, actress);
                    prep.setString(5, titleID);
                    prep.addBatch();
                    // System.out.println(prep);
                    prep.clearParameters();

                if(tracker%5000 == 0){
                    // System.out.println("Principals - Entries Completed: " + tracker);
                    // System.out.println("titleID -"  + titleID + ", nconst - " + nconst + ", category - " + category);
                    try{
                        int[] ret = prep.executeBatch();
                        // System.out.println("Principals Return Values Batch #" + batchNum + ":\n" + ret + "\n");
                        ++batchNum;
                    } catch(Exception e){
                        System.out.println("Principals failed to execute insert batch" + batchNum + "\n");
                    }
                }

                // System.out.println("titleID = " + titleID);
                // System.out.println("nconst = " + nconst);
                // System.out.println("category = " + category);
            }
        } 
        try{
            int[] ret = prep.executeBatch();
            // System.out.println("Principals Return Values Batch #" + batchNum + ":\n" + ret + "\n");
            ++batchNum;
        } catch(Exception e){
            System.out.println("Principals failed to execute insert batch" + batchNum + "\n");
        }

        System.out.println("Principals - Entries Finished With: " + tracker);
        sc.close();
    }  

    public static void main(String[] args) throws Exception{  
        // Set up and open the DB
        Connection conn = null;
        String teamNumber = "11";
        String sectionNumber = "905";
        String dbName = "csce315" + sectionNumber + "_" + teamNumber + "db";
        String dbConnectionString = "jdbc:postgresql://csce-315-db.engr.tamu.edu/" + dbName;
        String userName = "csce315" + sectionNumber + "_" + teamNumber + "user";
        String userPassword = "NotPassword";

        try {
            conn = DriverManager.getConnection(dbConnectionString,userName, userPassword);
         } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }

        System.out.println("Opened database successfully");

        try{
            // Create the personalRecs and Trending tables, which don't take input
            Statement stmt = conn.createStatement();
            String sqlStatement = "CREATE TABLE IF NOT EXISTS personalRecs(customerID INT, titleID TEXT, rank INT, isTrending BOOL, dateSuggested DATE);";
            int result = stmt.executeUpdate(sqlStatement);

            stmt = conn.createStatement();
            sqlStatement = "CREATE TABLE IF NOT EXISTS Trending(titleID TEXT PRIMARY KEY, rank INT, added DATE, lastWatched DATE);";
            result = stmt.executeUpdate(sqlStatement);
            
            // Create each table as needed, and read in the relevant CSV data
            stmt = conn.createStatement();
            sqlStatement = "CREATE TABLE IF NOT EXISTS Content(titleID TEXT PRIMARY KEY, titleType TEXT, originalTitle TEXT, runtimeMinutes INT, genres TEXT, year INT, averageRating DECIMAL, numVotes INT, directors TEXT, writers TEXT, actors TEXT, actresses TEXT);";
            result = stmt.executeUpdate(sqlStatement);

            PreparedStatement csvStatement = conn.prepareStatement("INSERT INTO Content VALUES (?,?,?,?,?,?,?,?);");
            titles(csvStatement);

            sqlStatement = "CREATE TABLE IF NOT EXISTS WatchHistory(customerID INT, rating DECIMAL, date DATE, titleID TEXT);";
            result = stmt.executeUpdate(sqlStatement);
            csvStatement = conn.prepareStatement("INSERT INTO WatchHistory VALUES (?,?,?,?);");
            custRating(csvStatement);

            sqlStatement = "CREATE TABLE IF NOT EXISTS Names(nconst TEXT, primaryName TEXT, birthYear INT, deathYear INT, primaryProfession TEXT);";
            result = stmt.executeUpdate(sqlStatement);
            csvStatement = conn.prepareStatement("INSERT INTO Names VALUES (?,?,?,?,?);");
            names(csvStatement);

            // The final two CSV files provide new data to an existing table, so UPDATE is used
            csvStatement = conn.prepareStatement("UPDATE Content SET directors=?,writers=? WHERE titleID=?;");
            crew(csvStatement);

            csvStatement = conn.prepareStatement("UPDATE Content SET directors=CONCAT(directors,?),writers=CONCAT(writers,?),actors=CONCAT(actors,?),actresses=CONCAT(actresses,?) WHERE titleID=?;");
            principals(csvStatement);



        } catch (Exception e){
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
         
         try {
           conn.close();
           System.out.println("Connection Closed.");
         } catch(Exception e) {
           System.out.println("Connection NOT Closed.");
         }
    }

}  