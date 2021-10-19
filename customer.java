import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.sql.*;
import java.awt.event.*;
import java.util.Vector;

public class customer extends sideselection{ 
  
  // Frame
  static JFrame jframe;

  // Main Panels
  static JPanel header, body, filters;

  // Changeable Components
  static JTextField search, minFiltYear, maxFiltYear, custID;
  static JComboBox typeSelect, genreSelect;
  static JComboBox minHistMonth, minHistYear, maxHistMonth, maxHistYear;
  static JTable trendTab, recTab, histTab, filtTab;
  static JScrollPane scrollTrendTab, scrollRecTab, scrollHistTab, scrollFiltTab;

  // Server Elements
  static Connection conn;
  static Statement stmt;

  // Other
  boolean filtered = false;

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

  public static void trendpop(){
    try{
      String[][] vals = new String[10][5];
      Statement stmt = conn.createStatement();
      String sqlStatement = "SELECT * FROM trending;";
      ResultSet result = stmt.executeQuery(sqlStatement);
      int loc = 0;
      while(result.next()){
        for(int i = 1; i < 5; ++i){
          vals[loc][i] = result.getString(i);
        }
        ++loc;
      }


      String[] colNames = {"Title", "TitleID", "Rank", "Date Added", "Last Watched"};
      trendTab = new JTable(new DefaultTableModel(colNames,0));
      DefaultTableModel model = (DefaultTableModel) trendTab.getModel();
      
      
      for(int i = 0; i < 10; ++i){
        sqlStatement = "SELECT originaltitle FROM content WHERE titleid='" + vals[i][1] + "';";
        result = stmt.executeQuery(sqlStatement);
        while(result.next()){vals[i][0] = result.getString(1);}
        model.addRow(vals[i]);
      }
    } catch(Exception e){
      System.out.println(e);
      System.out.println(e.getLocalizedMessage());
      System.out.println(e.getMessage());
      System.out.println(e.toString());
      System.out.println(e.getCause());
      JOptionPane.showMessageDialog(null,"Couldn't pull trending titles.");
    }
  }
  
  public static void recpop(String custid){
    try{
      
		//create statement
		Statement stmt = conn.createStatement();

		//create customerid holding variable and query variable
		String customerID = custid;
		//String customerID = "685565";
		String initQuery = "SELECT titleid FROM watchhistory WHERE customerid = " + customerID;

		//create vector to hold IDs and variable to store results from query
		Vector<String> ids =  new Vector<String>();
		ResultSet result = stmt.executeQuery(initQuery);

		//Process query results
		while (result.next()) {
			ids.add(result.getString("titleid"));
			//System.out.println(result.getString("titleid"));
		}

		System.out.println("Watch History titles secured.");

		//initialize variables to temporary string
		String targetID = "blank";
		String loopQuery = "blank";

		//create vector for storing genres 
		Vector<String> genres = new Vector<String>();

		//for each titleid, pull the genres and store them
		for(int i = 0; i < ids.size(); i++){
			targetID = ids.get(i);
			//System.out.println(ids.get(i));
			loopQuery = "SELECT genres FROM content WHERE titleid = '" + targetID + "'";
			result = stmt.executeQuery(loopQuery);
			
			while(result.next()){
				if(!genres.contains(result.getString(1))){
					genres.add(result.getString(1));
					//System.out.println(result.getString(1));
				}
			}
		}

		System.out.println("Watch History genres secured.");

		//initialize variables to temporary string
		String genreSearch = "blank";

		//create vector for storing recs
		Vector<String> recs = new Vector<String>();

		//for each genre, pull highest rated movie
		for(int i = 0; i < genres.size(); i++){
			genreSearch = genres.get(i);
			//System.out.println(genres.get(i));
			loopQuery = "SELECT titleid, originalTitle FROM content WHERE genres LIKE '%" + genreSearch + "%' ORDER BY averageRating DESC LIMIT 1";
			result = stmt.executeQuery(loopQuery);
			
			while(result.next()){
				recs.add(result.getString("originalTitle"));
				//System.out.println(result.getString("originalTitle"));
			}
		}
		
		System.out.println("Recommendations secured.");
		
		String[][] recs2 = new String[10][1];
		//String holder = "blank";
		for(int i = 0; i < 10; i++){
			//holder = recs.getString(i);
			recs2[i][0] = recs.get(i);
		}

		String[] colNames = {"Title"};
		DefaultTableModel model = new DefaultTableModel(recs2,colNames);
		recTab.setModel(model);
		/*String[] colNames = {"Title", "TitleID", "Date Watched"};
		DefaultTableModel dtm = new DefaultTableModel(data,colNames);
		histTab.setModel(dtm);*/
		
    } catch(Exception e){
      System.out.println(e);
      System.out.println(e.getLocalizedMessage());
      System.out.println(e.getMessage());
      System.out.println(e.toString());
      System.out.println(e.getCause());
      JOptionPane.showMessageDialog(null,"Couldn't pull recommendations.");
    }
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
      String sqlStatement = "SELECT titleID, date FROM watchhistory WHERE (customerid='" + custID + "' AND date > '" + y1 + "-" + m1 + "-" + d1 + "' AND date < '" + y2 + "-" + m2 +"-" + d2 + "') ORDER BY date DESC;";
      ResultSet result = stmt.executeQuery(sqlStatement);

      Vector<String> ids = new Vector<String>();
      Vector<String> date = new Vector<String>();
      while (result.next()) {
          ids.add(result.getString(1));  
          date.add(result.getString(2));            
      }


      String[][] data = new String[ids.size()][3];
      for(int i=0; i < ids.size(); ++i){
          sqlStatement = "SELECT originalTitle FROM content WHERE titleID='"+ ids.get(i) + "';";
          result = stmt.executeQuery(sqlStatement);
          while(result.next()){
            data[i][0] = result.getString(1);
            data[i][1] = ids.get(i);
            data[i][2] = date.get(i);
          }
      }


      String[] colNames = {"Title", "TitleID", "Date Watched"};
      DefaultTableModel dtm = new DefaultTableModel(data,colNames);
      histTab.setModel(dtm);
    }catch (Exception e){
      e.printStackTrace();
      System.err.println(e.getClass().getName()+": "+e.getMessage());
      System.exit(0);
    }
  }

  public static void filterpage(){
    jframe.remove(body);
    
    int conditions = 0;
    String sqlStat = "SELECT titleid,originaltitle,titletype,genres,year FROM content WHERE(";
    try{
      if(!search.getText().contains("n/a")){
        ++conditions;
        if(conditions > 1){
          sqlStat = sqlStat + " AND ";
        }
        sqlStat = sqlStat + "titleid='" + search.getText() + "'";
      }
      if(typeSelect.getSelectedIndex() != 0){
        ++conditions;
        if(conditions > 1){
          sqlStat = sqlStat + " AND ";
        }
        sqlStat = sqlStat + "(titletype LIKE '%" +typeSelect.getSelectedItem() + "%')";
      }
      if(genreSelect.getSelectedIndex() != 0){
        ++conditions;
        if(conditions > 1){
          sqlStat = sqlStat + " AND ";
        }
        sqlStat = sqlStat + "(genres LIKE '%" +genreSelect.getSelectedItem() + "%')";
      }
      if(!minFiltYear.getText().contains("n/a")){
        ++conditions;
        if(conditions > 1){
          sqlStat = sqlStat + " AND ";
        }
        sqlStat = sqlStat + "year > '" +minFiltYear.getText() + "'";
      }
      if(!maxFiltYear.getText().contains("n/a")){
        ++conditions;
        if(conditions > 1){
          sqlStat = sqlStat + " AND ";
        }
        sqlStat = sqlStat + "year < '" +maxFiltYear.getText() + "'";
      }
      
      sqlStat = sqlStat + ");";
      JOptionPane.showMessageDialog(null,"Pulling titles based on filters...");

      // System.out.println(sqlStat);
     
      Statement stat = conn.createStatement();
      ResultSet res = stat.executeQuery(sqlStat);
      
      String[] cols = {"titleid", "originaltitle", "titletype", "genres", "year"};
      filtTab = new JTable(new DefaultTableModel(cols,0));
      DefaultTableModel model = (DefaultTableModel) filtTab.getModel();
      
      String[] row = new String[5];
      while(res.next()){
        row[0] = res.getString(1);
        row[1] = res.getString(2);
        row[2] = res.getString(3);
        row[3] = res.getString(4);
        row[4] = res.getString(5);
        model.addRow(row);
      }
      
      
      scrollFiltTab = new JScrollPane(filtTab);
      jframe.add(scrollFiltTab, BorderLayout.CENTER);
      jframe.setVisible(false);
      jframe.setVisible(true);

      JOptionPane.showMessageDialog(null,"Finished pulling titles based on filters.");
    } catch(Exception e){
      JOptionPane.showMessageDialog(null,"Could not execute filter.");
    }
  }

  public static void go(){ 
      customer s = new customer();
      histTab = new JTable();
      trendTab = new JTable();
	  recTab = new JTable();
      sqlStart();

      // Used throughout
      String[] months = {"n/a","01","02","03","04","05","06","07","08","09","10","11","12"};
      String[] years = {"n/a","1999","2000","2001", "2002", "2003", "2004","2005"};


      // creating a Frame   
      jframe = new JFrame(); 


      // header components
      header = new JPanel();
      {
        JPanel searchSec = new JPanel();

        JTextField searchLabel = new JTextField("Search by titleID:");
        searchLabel.setEditable(false);

        search = new JTextField("n/a",20);
        search.setEditable(true);
        JButton searchGo = new JButton("Search");
        searchGo.addActionListener(s);

        JButton title = new JButton("Home");
        title.addActionListener(s);
        title.setPreferredSize(new Dimension(75,10));
        title.setHorizontalAlignment(JTextField.CENTER);

        JButton quit = new JButton("Quit");
        quit.addActionListener(s);


        searchSec.setLayout(new FlowLayout(FlowLayout.LEFT, 10,10));
        searchSec.add(searchLabel); searchSec.add(search); searchSec.add(searchGo);
        header.setLayout(new BorderLayout());
        header.add(searchSec, BorderLayout.WEST);
        header.add(title, BorderLayout.CENTER);
        header.add(quit, BorderLayout.EAST);
      }


      // body components
      body = new JPanel();
      {
        JTextField trendLabel = new JTextField("Trending");
        trendLabel.setEditable(false);     
        trendpop();  
        scrollTrendTab = new JScrollPane(trendTab);
        

        JTextField recLabel = new JTextField("Personal Recommendations");
        recLabel.setEditable(false);
        //recpop();
        scrollRecTab = new JScrollPane(recTab);


        JPanel history = new JPanel();
       
        JTextField histLabel = new JTextField("Recently Watched");
        histLabel.setEditable(false);


        minHistMonth = new JComboBox(months);
        minHistYear = new JComboBox(years);

        JTextField dateWatched = new JTextField("<= Date Watched <= ");
        dateWatched.setEditable(false);
        maxHistMonth = new JComboBox(months);
        maxHistYear = new JComboBox(years);


        JTextField idLabel = new JTextField("Customer ID: ");
        idLabel.setEditable(false);
        custID = new JTextField(10);

        JButton popHist = new JButton("Get Watch History");
        popHist.addActionListener(s);


        history.setLayout(new FlowLayout(FlowLayout.LEFT,5,10));
        history.add(histLabel); 
        history.add(minHistMonth); history.add(minHistYear);
        history.add(dateWatched);
        history.add(maxHistMonth); history.add(maxHistYear);
        history.add(idLabel); history.add(custID);
        history.add(popHist);
        

        scrollHistTab = new JScrollPane(histTab);

        body.add(trendLabel); body.add(scrollTrendTab);
        body.add(recLabel); body.add(scrollRecTab);
        body.add(history); body.add(scrollHistTab);
        body.setLayout(new GridLayout(6,1,20,5));
      }


      // filter components
      filters = new JPanel();
      { 
        JTextField type = new JTextField("Type:");
        type.setEditable(false);
        String[] typeArr = {"n/a","tvShort","movie","tvMovie","short","tvMiniSeries","videoGame","tvEpisode","video","tvSpecial","tvSeries"};
        typeSelect = new JComboBox(typeArr);


        JTextField genre = new JTextField("Genre:");
        genre.setEditable(false);
        String[] genreArr = {"n/a","Action", "Comedy", "Horror", "Adult", "Thriller", "Fantasy", "Mystery", "Short",
        "Documentary", "Talk-Show", "Romance", "Game-Show", "Drama", "Sci-Fi", "News", "Sport", "Music", "Musical",
        "Adventure", "Crime", "Family", "Animation", "Biography", "Reality-TV", "War", "Western", "Film-Noir", "History"};
        genreSelect = new JComboBox(genreArr);


        JTextField date = new JTextField("Release Date:");
        date.setEditable(false);

        JPanel dateSearch = new JPanel();
        minFiltYear = new JTextField("n/a",10);

        JTextField dateFilt = new JTextField("<= Date Watched <= ");
        dateFilt.setEditable(false);
        maxFiltYear = new JTextField("n/a",10);

        dateSearch.setLayout(new FlowLayout(FlowLayout.CENTER,5,10));
        dateSearch.add(minFiltYear);
        dateSearch.add(dateFilt);
        dateSearch.add(maxFiltYear);

    
        JButton rstFilters = new JButton("Reset Filters");
        rstFilters.addActionListener(s);


        filters.add(type);
        filters.add(typeSelect);
        filters.add(genre);
        filters.add(genreSelect);
        filters.add(date);
        filters.add(dateSearch);
        filters.add(rstFilters);
        filters.setLayout(new GridLayout(7,1,20,10));
      }


      // Add to jframe
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
      } else if(s.equals("Reset Filters")){
        search.setText("n/a");
        typeSelect.setSelectedIndex(0);
        genreSelect.setSelectedIndex(0);
        minFiltYear.setText("n/a");
        maxFiltYear.setText("n/a");
      } else if(s.equals("Get Watch History")){
		try{
		
		if(minHistMonth.getSelectedIndex() == 0 || minHistYear.getSelectedIndex() == 0 || maxHistMonth.getSelectedIndex() == 0 || maxHistYear.getSelectedIndex() == 0){
		JOptionPane.showMessageDialog(null,"Invalid Date Selection.");

		} else if(Integer.parseInt((String)minHistYear.getSelectedItem()) > Integer.parseInt((String)maxHistYear.getSelectedItem())){
		JOptionPane.showMessageDialog(null,"Invalid Date Selection.");

		} else if (minHistYear.getSelectedItem() == maxHistYear.getSelectedItem()){           
		  if (Integer.parseInt((String)minHistMonth.getSelectedItem()) > Integer.parseInt((String)maxHistMonth.getSelectedItem())){
			JOptionPane.showMessageDialog(null,"Invalid Date Selection.");

		  } else {
			JOptionPane.showMessageDialog(null,"Pulling Watch History...");
			recpop(custID.getText());
			histpop(Integer.parseInt(custID.getText()), (String)minHistYear.getSelectedItem(), (String)minHistMonth.getSelectedItem(), (String)maxHistYear.getSelectedItem(), (String)maxHistMonth.getSelectedItem());
			JOptionPane.showMessageDialog(null,"Watch History Updated.");
		  }
		} else {
		  JOptionPane.showMessageDialog(null,"Pulling Watch History...");
		  recpop(custID.getText());
		  histpop(Integer.parseInt(custID.getText()), (String)minHistYear.getSelectedItem(), (String)minHistMonth.getSelectedItem(), (String)maxHistYear.getSelectedItem(), (String)maxHistMonth.getSelectedItem());
		  JOptionPane.showMessageDialog(null,"Watch History Updated.");
		}

		} catch(Exception x){
		JOptionPane.showMessageDialog(null,"Information provided not valid.");
		}
      } else if(s.equals("Search")){
        filtered = true;
        filterpage();
      } else if(s.equals("Home") && filtered){
          filtered = false;
          jframe.remove(scrollFiltTab);
          jframe.add(body,BorderLayout.CENTER);
          jframe.setVisible(false);
          jframe.setVisible(true);
      }
  }





}

