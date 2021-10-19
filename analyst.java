import java.awt.*;
import javax.swing.*;

import java.sql.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;


public class analyst extends sideselection{ 
  
  // Frame
  static JFrame jframe;

  // Changeable Components
  static JTextField search, min, max;
  static JComboBox typeSelect, genreSelect;
  static JTable tab;

  // Server Elements
  static Connection conn;
  static Statement stmt;


  public static void tomatoNumber(String mA, String mB)
  {
    sqlStart();

    ResultSet result;
    String cmd;
    ArrayList<String> customerID = new ArrayList<String>();
    ArrayList<String> movieID = new ArrayList<String>();
    ArrayList<String> temp = new ArrayList<String>();
    
    Boolean found = false;
    int counter = 0;

    String currentCustomer;
    String currentMovie;

    String movieA = mA;
    String movieB = mB;

    try
    {
        // Finds all instances of titleid where rating is 5
        cmd = "SELECT * FROM watchhistory WHERE rating>3 AND titleid='" + movieA + "';";
        result = stmt.executeQuery(cmd);

        // Adds to customers to customerID array
        while(result.next())
        {
            customerID.add(result.getString(1));
        }

        // Checks for duplicate entries
        for(int i = 0; i < customerID.size(); i++)
        {
            if(!temp.contains(customerID.get(i)))
            {
                // System.out.println("Trigger Test: " + customerID.get(i));
                temp.add(customerID.get(i));
            }
        }

        // Moves all unique entries back into customerID
        customerID.clear();
        customerID.addAll(temp);
        temp.clear();

        // System.out.println("All customers added");

        // for(int i = 0; i < customerID.size(); i++)
        // {
        //     System.out.println(customerID.get(i));
        // }

        
        while(!found)
        {
            // Adds all movies that are rated 5 from each customer into movieID array list.
            for(int i = 0; i < customerID.size(); i++)
            {
                currentCustomer = customerID.get(i);

                cmd = "SELECT * FROM watchhistory WHERE rating>3 AND customerid='" + currentCustomer + "';";
                result = stmt.executeQuery(cmd);

                while(result.next())
                {
                    movieID.add(result.getString(4));
                }
            }

            // Checks for duplicate entries
            for(int i = 0; i < movieID.size(); i++)
            {
                if(!temp.contains(movieID.get(i)))
                {
                    temp.add(movieID.get(i));
                }
            }

            // Transfer unique only list to movieID
            movieID.clear();
            movieID.addAll(temp);
            temp.clear();
            customerID.clear();

            // Checks if movie has been found
            if(movieID.contains(movieB))
            {
                found = true;
                break;
            }

            // Adds all customers that rated 5 from movies
            for(int i = 0; i < movieID.size(); i++)
            {
                currentMovie = movieID.get(i);

                cmd = "SELECT * FROM watchhistory WHERE rating>3 AND titleid='" + currentMovie + "';";
                result = stmt.executeQuery(cmd);

                while(result.next())
                {
                    customerID.add(result.getString(1));
                }
            }

            // Checks for duplicate entries
            for(int i = 0; i < customerID.size(); i++)
            {
                if(!temp.contains(customerID.get(i)))
                {
                    temp.add(customerID.get(i));
                }
            }

            customerID.clear();
            customerID.addAll(temp);
            temp.clear();
            movieID.clear();

            // Counts how many movies have been parsed
            counter++;

            // System.out.println("While Stuck");
        }
        

        System.out.println("There are " + counter + " movies between movies " + movieA + " and " + movieB);
    }
    catch(Exception e)
    {
        System.out.println("error");
    }

  }


public static void cultClass(){
    try{
        String msg = "Gathering Cult Classics...";
        JOptionPane.showMessageDialog(null, msg);
    
        String sqlStat = "SELECT DISTINCT(titleID),COUNT(*) FROM watchhistory WHERE rating>=4 GROUP BY titleID HAVING COUNT(customerID)>50 ORDER BY COUNT(*) DESC LIMIT 10;";
        stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery(sqlStat);
        Vector<Vector<String>> classInfo = new Vector<Vector<String>>();
        classInfo.add(new Vector<String>());
        classInfo.add(new Vector<String>());
        while(res.next()){
            classInfo.get(0).add(res.getString(1));
        }

        msg = "Finding set...";
        JOptionPane.showMessageDialog(null, msg);

        String param1, param2, param3;
        String t1= ""; 
        String t2 = ""; 
        String t3 = "";
        double maxCnt = 0;

            
        for(int i = 0; i < classInfo.get(0).size()-2;++i){
            // System.out.println("i= " + i);
            for(int j = i+1; j < classInfo.get(0).size()-1; ++j){
                // System.out.println("j= " + j);
                for(int k = j+1; k < classInfo.get(0).size(); ++k){
                    // System.out.println("k= " + k);
                    sqlStat = "SELECT COUNT(DISTINCT(customerID)) FROM watchhistory WHERE rating >= 4 AND customerID IN(";
                    param1 = "SELECT customerID FROM watchhistory WHERE titleID='" + classInfo.get(0).get(i) + "'";
                    param2 = "SELECT customerID FROM watchhistory WHERE titleID='" + classInfo.get(0).get(j) + "'";
                    param3 = "SELECT customerID FROM watchhistory WHERE titleID='" + classInfo.get(0).get(k) + "'";
                    sqlStat = sqlStat + param1 + " AND rating>=4 AND customerID IN(" + param2 + " AND rating>=4 AND customerID IN(" + param3 + " AND rating>=4)));";
                    res = stmt.executeQuery(sqlStat);
                    while(res.next()){
                        if(Double.parseDouble(res.getString(1)) > maxCnt){
                            maxCnt = Double.parseDouble(res.getString(1));
                            t1 = classInfo.get(0).get(i);
                            t2 = classInfo.get(0).get(j);
                            t3 = classInfo.get(0).get(k);
                        }
                    }
                }
            }
        }


        sqlStat = "SELECT originaltitle FROM content WHERE titleid='" + t1 +"';";
        res = stmt.executeQuery(sqlStat);
        while(res.next()){
            t1 = res.getString(1);
        }

        sqlStat = "SELECT originaltitle FROM content WHERE titleid='" + t2 +"';";
        res = stmt.executeQuery(sqlStat);
        while(res.next()){
            t2 = res.getString(1);
        }

        sqlStat = "SELECT originaltitle FROM content WHERE titleid='" + t3 +"';";
        res = stmt.executeQuery(sqlStat);
        while(res.next()){
            t3 = res.getString(1);
        }

        String fin = t1 + ", " + t2 + ", " + t3 + "\nCommon viewership: " + maxCnt;
        JOptionPane.showMessageDialog(null, fin, "Cult Classics", JOptionPane.INFORMATION_MESSAGE);
    } catch(Exception e){
        JOptionPane.showMessageDialog(null, "Could not gather cult classics.");
        System.out.println(e);
    }
}


public static void hollyPair(){
    try{
        String msg = "Gathering names...";
        JOptionPane.showMessageDialog(null, msg);

        String sqlStat = "SELECT DISTINCT actors FROM content WHERE (averagerating>'8.5');";
        String sqlStat2 = "SELECT DISTINCT actresses FROM content WHERE (averagerating>'8.5');";
        stmt = conn.createStatement();
        Statement stmt2 = conn.createStatement();
        ResultSet res = stmt.executeQuery(sqlStat);
        ResultSet res2 = stmt2.executeQuery(sqlStat2);
        Vector<String> names = new Vector<String>();
        String[] splt;
        while(res.next()){
            if(res.getString(1) != null){
                splt = res.getString(1).split(", ");
                for(int i = 0; i < splt.length; ++i){
                    if(!names.contains(splt[i]) && splt[i].length()>4){
                        names.add(splt[i]);
                    }
                }
            }
        }

        // System.out.println("Finished Actors, size: " + names.size());

        while(res2.next()){
            if(res2.getString(1) != null){
                splt = res2.getString(1).split(", ");
                for(int i = 0; i < splt.length; ++i){
                    if(!names.contains(splt[i]) && splt[i].length()>4){
                        names.add(splt[i]);
                    }
                }
            }
        }

        // System.out.println("Finished all names, size: " + names.size());

        // for(int i = 0; i < names.size(); ++i){
        //     System.out.println(names.get(i));
        // }

        msg = "Gathering average pair ratings...";

        // if(names.size() > 1)
        // {
        //     System.out.println(names.size());
        //     return;
        // } 
    
        

        int ckpnt = Integer.max(Integer.min(names.size() / 5,names.size()/2),1);
        double perc = 0.0;
        Vector<Vector<String>> pairs = new Vector<Vector<String>>();
        Vector<String> temp = new Vector<String>();
        for(int i = 0; i < names.size(); ++i){
            // System.out.println("I is: " + i);
            if(i%ckpnt == 0){
                perc = 0.0 + i;
                perc = (int)(perc / names.size() * 100);
                JOptionPane.showMessageDialog(null, msg + "\n" + perc + "% Complete");
            }
            for(int j = i+1; j < names.size(); ++j){
                // System.out.println(j);
                temp = new Vector<String>();
                temp.add(names.get(i));
                temp.add(names.get(j));
                sqlStat = "SELECT AVG(averagerating) FROM content WHERE((actors LIKE '%" + temp.get(0) + "%' OR actresses LIKE '%" + temp.get(0) + "%') AND (actors LIKE '%" + temp.get(1) + "%' OR actresses LIKE '%" + temp.get(1) + "%'));";
                res = stmt.executeQuery(sqlStat);
                while(res.next()){
                    temp.add(res.getString(1));
                    if(temp.get(2) != null){
                        pairs.add(temp);
                        // System.out.println(temp.get(0) + " with " + temp.get(1) + " for " + temp.get(2));
                    }
                }
            }
        }

        // for(int i = 0; i < pairs.size(); ++i){
        //     for(int j = 0; j < pairs.get(i).size(); ++j){
        //         System.out.println(pairs.get(i).get(j));
        //     }
        // }


        msg = "Finding maximum pair averages...";

        ckpnt =  Integer.max(Integer.min(pairs.size() / 5,pairs.size()/2),1);
        String[] maxLocs = {"0","0","0","0","0","0","0","0","0","0"};
        for(int i = 0; i < pairs.size(); ++i){
            sqlStat = "SELECT primaryname FROM names WHERE(nconst LIKE '%" + pairs.get(i).get(0) + "%');";
            sqlStat2 = "SELECT primaryname FROM names WHERE(nconst LIKE '%" + pairs.get(i).get(1) + "%');";
            // System.out.println(sqlStat);
            res = stmt.executeQuery(sqlStat);
            res2 = stmt2.executeQuery(sqlStat2);
            while(res.next()){
                pairs.get(i).set(0, res.getString(1));
                System.out.println(res.getString(1) + " should be in " + pairs.get(i).get(0));
            }
            while(res2.next()){
                pairs.get(i).set(1, res2.getString(1));
                System.out.println(res2.getString(1) + " should be in " + pairs.get(i).get(1));
            }
            System.out.println("Names: " + pairs.get(i).get(0) + ", " + pairs.get(i).get(1) + "\tRating: " + Double.parseDouble(pairs.get(i).get(2)));

            if(i%ckpnt == 0){
                perc = 0.0 + i;
                perc = (int)(perc / pairs.size() * 100);
                JOptionPane.showMessageDialog(null, msg + "\n" + perc + "% Complete");
            }
            for(int j = 9; j > -1; --j){
                if(j == 0){
                    if(Double.parseDouble(pairs.get(i).get(2)) >= Double.parseDouble(pairs.get(Integer.parseInt(maxLocs[j])).get(2))){
                        maxLocs[j] = String.valueOf(i);
                    }
                }else{
                    if(Double.parseDouble(pairs.get(i).get(2)) >= Double.parseDouble(pairs.get(Integer.parseInt(maxLocs[j-1])).get(2))){
                        maxLocs[j] = maxLocs[j-1];
                    } else if(Double.parseDouble(pairs.get(i).get(2)) >= Double.parseDouble(pairs.get(Integer.parseInt(maxLocs[j])).get(2))){
                        maxLocs[j] = String.valueOf(i);
                    }
                }
            }
        }

        String fin = "";
        for(int i =0; i < 10; ++i){
            fin = fin + "#" + (i+1) + ") " + pairs.get(Integer.parseInt(maxLocs[i])).get(0) + " and " + pairs.get(Integer.parseInt(maxLocs[i])).get(1) + " with a average rating of: " + pairs.get(Integer.parseInt(maxLocs[i])).get(2).subSequence(0, 3) + "\n";
        }
        JOptionPane.showMessageDialog(null, fin, "Hollywood Pairs", JOptionPane.INFORMATION_MESSAGE);
    } catch(Exception e){
        JOptionPane.showMessageDialog(null,"Could not gather \"Hollywood Pairs\"");
        // System.out.println(e);
    }
}







  public static void sqlStart(){
    conn = null;
    try {
      Class.forName("org.postgresql.Driver");
      conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315905_11db",
      "csce315905_11user", "NotPassword");
      stmt = conn.createStatement();
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println(e.getClass().getName()+": "+e.getMessage());
      System.exit(0);
    }
    JOptionPane.showMessageDialog(null,"Opened database successfully");
  }


  public static void histpop(int custID, String y1, String m1, String y2, String m2){
    try{
      String d1, d2 = "";
      switch(m1){
        case "02": d1 = "28"; break;
        case "01":
        case "03":
        case "05":
        case "07":
        case "08":
        case "10":
        case "12": d1 = "31"; break;
        case "04":
        case "06":
        case "09":
        case "11": d1 = "30"; break;
        default: d1 = "28"; break;
      }
      switch(m2){
        case "02": d2 = "28"; break;
        case "01":
        case "03":
        case "05":
        case "07":
        case "08":
        case "10":
        case "12": d2 = "31"; break;
        case "04":
        case "06":
        case "09":
        case "11": d2 = "30"; break;
        default: d2 = "28"; break;
      }
      String sqlStatement = "SELECT titleID, rank, lastwatched FROM trending WHERE (lastwatched > '" + y1 + "-" + m1 + "-" + d1 + "' AND lastwatched < '" + y2 + "-" + m2 +"-" + d2 + "') ORDER BY lastwatched DESC;";
      ResultSet result = stmt.executeQuery(sqlStatement);

      Vector<String> ids = new Vector<String>();
      Vector<String> rank = new Vector<String>();
      Vector<String> lastwatched = new Vector<String>();

      while (result.next()) {
          ids.add(result.getString(1));
          rank.add(result.getString(2));   
          lastwatched.add(result.getString(3));   

      }

        // takes in the actual names for the movies listed adds all the respective data into a 2d array 
      String[][] data = new String[ids.size()][6];
      for(int i=0; i < ids.size(); ++i){
          sqlStatement = "SELECT originalTitle, averagerating, titletype, genres FROM content WHERE titleID='"+ ids.get(i) + "';";
          result = stmt.executeQuery(sqlStatement);
          
          
          while(result.next()){
            data[i][0] = rank.get(i);
            data[i][1] = result.getString(1);
            data[i][2] = result.getString(2);
            data[i][3] = result.getString(3);
            data[i][4] = result.getString(4);
            data[i][5] = lastwatched.get(i);
          }
      }

      //now to sort data based on ranking
      Arrays.sort(data, new Comparator<String[]>() {
          @Override
          public int compare(String[] first, String[] second) {
              int a = Integer.parseInt(first[0]); 
              int b = Integer.parseInt(second[0]); 
              if(a > b) return 1;
              else return -1;
          }
      });
    
      String[] colNames = {"Ranking", "Title", "Average Rating", "Content Type", "Genre", "Date Last Watched"};
      tab = new JTable(data,colNames);
      tab.setDefaultEditor(Object.class, null);
      }catch (Exception e){
        e.printStackTrace();
        System.err.println(e.getClass().getName()+": "+e.getMessage());
        System.exit(0);
      }
  }

  



  public static void go() { 
      analyst s = new analyst();
      sqlStart();


      // creating a Frame   
      jframe = new JFrame("Analyst GUI Tool");
      jframe.setSize(1800, 1000);


      
      // header components
      JPanel header = new JPanel();
      {
        JPanel searchSec = new JPanel();
        search = new JTextField(20);
        search.setEditable(true);
        JButton searchGo = new JButton("Search (ID only)");

        JTextField title = new JTextField("Current Viewership Trends");
        title.setEditable(false);
        title.setPreferredSize(new Dimension(75,10));
        title.setHorizontalAlignment(JTextField.CENTER);

        JButton quit = new JButton("Quit");
        quit.addActionListener(s);


        searchSec.setLayout(new FlowLayout(FlowLayout.LEFT, 10,10));
        searchSec.add(search); searchSec.add(searchGo);


        header.setLayout(new BorderLayout());
        header.add(searchSec, BorderLayout.WEST);
        header.add(title, BorderLayout.CENTER);
        header.add(quit, BorderLayout.EAST);

      }

      // body components
      JPanel body = new JPanel();
      {
        // Dimension dimBut = new Dimension(100,50);
 
        histpop(1488844, "1899", "01", "2999", "12");

        JScrollPane scrollTab = new JScrollPane(tab);

        body.add(scrollTab);
        body.setLayout(new GridLayout(6,1,20,5));
      }

      JTextField graph = new JTextField("Graphical Viewership Trends");

      body.add(graph);
      graph.setEditable(false);

      jframe.setLayout(new BorderLayout(20,10));
      {
        jframe.add(header, BorderLayout.NORTH);
        jframe.add(body, BorderLayout.CENTER);
        jframe.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        // jframe.setUndecorated(true);
        jframe.setVisible(true);
      } 

      
      JPanel filters = new JPanel();
      { 
        JTextField type = new JTextField("Content Type:");
        type.setEditable(false);
        String[] typeArr = {"","tvShort","movie","tvMovie","short","tvMiniSeries","videoGame","tvEpisode","video","tvSpecial","tvSeries"};
        typeSelect = new JComboBox(typeArr);
        typeSelect.addActionListener(s);


        JTextField genre = new JTextField("Genre:");
        genre.setEditable(false);
        String[] genreArr = {"","Action", "Comedy", "Horror", "Adult", "Thriller", "Fantasy", "Mystery", "Short",
        "Documentary", "Talk-Show", "Romance", "Game-Show", "Drama", "Sci-Fi", "News", "Sport", "Music", "Musical",
        "Adventure", "Crime", "Family", "Animation", "Biography", "Reality-TV", "War", "Western", "Film-Noir", "History"};
        genreSelect = new JComboBox(genreArr);
        genreSelect.addActionListener(s);


        JPanel dateSearch = new JPanel();
        min = new JTextField(10);
        min.setEditable(true);
        JButton dat = new JButton("<= Year Released <= ");
        dat.addActionListener(s);
        max = new JTextField(10);
        max.setEditable(true);
        dateSearch.setLayout(new FlowLayout(FlowLayout.CENTER,5,10));
        dateSearch.add(min); dateSearch.add(dat); dateSearch.add(max);

        JButton hpairs = new JButton("Show Hollywood Pairs");
        hpairs.addActionListener(s);
        JButton cclass = new JButton("Show Cult Classics");
        cclass.addActionListener(s);
        JButton tnum = new JButton("Show Tomato Number/Path");
        tnum.addActionListener(s);

        JButton rstFilters = new JButton("Reset Filters");
        rstFilters.addActionListener(s);


        filters.add(type);
        filters.add(typeSelect);
        filters.add(genre);
        filters.add(genreSelect);
        filters.add(dateSearch);
        filters.add(hpairs);
        filters.add(cclass);
        filters.add(tnum);
        filters.add(rstFilters);
        filters.setLayout(new GridLayout(9,1,20,10));
      }

      jframe.setLayout(new BorderLayout(20,10));
      {
        jframe.add(header, BorderLayout.NORTH);
        jframe.add(body, BorderLayout.CENTER);
        jframe.add(filters, BorderLayout.WEST);
        jframe.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        // jframe.setUndecorated(true);
        jframe.setVisible(true);
      }   
      
  }

  public void actionPerformed(ActionEvent e)
  {
    String s = e.getActionCommand();
    if (s.equals("Quit")) {
        try {
        conn.close();
        JOptionPane.showMessageDialog(null,"Connection Closed.");
        } catch(Exception ex) {
        JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
        }
        jframe.dispose();
    } else if (s.equals("<= Year Released <= ")) {
        search.setText("");
        typeSelect.setSelectedIndex(0);
        genreSelect.setSelectedIndex(0);
        min.setText("");
        max.setText("");
    } else if(s.equals("Show Hollywood Pairs")){
        hollyPair();
    } else if(s.equals("Show Cult Classics")){
        cultClass();
    } else if(s.equals("Show Tomato Number/Path")){
        tomatoNumber("tt0021409", "tt00304678");
    } else if(s.equals("Reset Filters")){
        search.setText("");
        typeSelect.setSelectedIndex(0);
        genreSelect.setSelectedIndex(0);
        min.setText("");
        max.setText("");
    } else if (e.getSource() == typeSelect){
        System.out.println(typeSelect.getSelectedItem());
    } else if (e.getSource() == genreSelect){
        System.out.println(genreSelect.getSelectedItem());
    }
  }


  

}
