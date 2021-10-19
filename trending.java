import java.sql.*;
import java.util.Vector;

public class trending {

  public static void main(String args[]) {
     Connection conn = null;
     String teamNumber = "11";
     String sectionNumber = "905";
     String dbName = "csce315" + sectionNumber + "_" + teamNumber + "db";
     String dbConnectionString = "jdbc:postgresql://csce-315-db.engr.tamu.edu/" + dbName;
     String userName = "csce315" + sectionNumber + "_" + teamNumber + "user";
     String userPassword = "NotPassword";

    //Connecting to the database 
    try {
        conn = DriverManager.getConnection(dbConnectionString,userName, userPassword);
     } catch (Exception e) {
        e.printStackTrace();
        System.err.println(e.getClass().getName()+": "+e.getMessage());
        System.exit(0);
     }

     System.out.println("Opened database successfully");
     
     try{
        //create a statement object
        Statement stmt = conn.createStatement();
        Statement stmt2 = conn.createStatement();

        //Running a query
        String sqlStatement = "SELECT titleID FROM WatchHistory WHERE date>='2005-01-01';";

        //Get Results
        ResultSet result = stmt.executeQuery(sqlStatement);
        ResultSet result2;
        Vector<String> ids = new Vector<String>();
        Vector<String> amt = new Vector<String>();

        System.out.println("--------------------Query Results--------------------");
        int tracker = 1;
        while (result.next()) {
            if(!ids.contains(result.getString(1))){
                if(tracker % 500 == 0){
                    System.out.println("Tracker = " + tracker); 
                }
                ++tracker;
                ids.add(result.getString(1));
                // System.out.println(result.getString(1));
                sqlStatement = "SELECT COUNT(titleID) FROM WatchHistory WHERE(titleID='" + ids.get(ids.size()-1) +"' AND date>='2005-01-01');";
                // System.out.println(sqlStatement);
                result2 = stmt2.executeQuery(sqlStatement);
                while(result2.next()){
                    amt.add(result2.getString(1));
                }
            }
        }

        System.out.println("Got IDS");

        String[] maxLocs = {"0","0","0","0","0","0","0","0","0","0"};
        for(int i = 0; i < amt.size(); ++i){
            for(int j = 9; j > -1; --j){
                if(j == 0){
                    if(Integer.parseInt(amt.get(i)) >= Integer.parseInt(amt.get(Integer.parseInt(maxLocs[j])))){
                        maxLocs[j] = String.valueOf(i);
                    }
                }else{
                    if(Integer.parseInt(amt.get(i)) >= Integer.parseInt(amt.get(Integer.parseInt(maxLocs[j-1])))){
                        maxLocs[j] = maxLocs[j-1];
                    } else if(Integer.parseInt(amt.get(i)) >= Integer.parseInt(amt.get(Integer.parseInt(maxLocs[j])))){
                        maxLocs[j] = String.valueOf(i);
                    }
                }
            }
        }
        
        String[] finIds = new String[10];
        String[] finNames = new String[10];
        String[] finDates = new String[10];
        int cnt = 1;
        int res = 0;
        for(int i =0; i < 10; ++i){
            finIds[i] = ids.get(Integer.parseInt(maxLocs[i]));

            sqlStatement = "SELECT originalTitle FROM content WHERE titleID='" + finIds[i] +"';";
            result = stmt.executeQuery(sqlStatement);
            while(result.next()){ finNames[i] = result.getString(1);}

            sqlStatement = "SELECT date FROM watchhistory WHERE titleID='" + finIds[i] +"' ORDER BY date DESC LIMIT 1;";
            result = stmt.executeQuery(sqlStatement);
            while(result.next()){ finDates[i] = result.getString(1);}

            sqlStatement = "INSERT INTO trending VALUES ('" + finIds[i] + "'," + cnt + ",'2021-10-10','" + Date.valueOf(finDates[i])+"');";
            res = stmt.executeUpdate(sqlStatement);
            ++cnt;
            System.out.println(res);

        }

       
   } catch (Exception e){
       e.printStackTrace();
       System.err.println(e.getClass().getName()+": "+e.getMessage());
       System.exit(0);
   }
    
    //closing the connection
    try {
      conn.close();
      System.out.println("Connection Closed.");
    } catch(Exception e) {
      System.out.println("Connection NOT Closed.");
    }//end try catch
  }//end main
}//end Class
