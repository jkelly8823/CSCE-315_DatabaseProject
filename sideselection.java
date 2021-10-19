import java.sql.*;
import java.time.Year;
import java.awt.event.*;
import javax.swing.*;

public class sideselection extends JFrame implements ActionListener{
    static JFrame f;
	
	public static void main(String[] args)
    {
		//t1.frame.setVisible(false);
      //creates a new frame
      f = new JFrame("DB GUI");

      //creates an object
      sideselection s = new sideselection();

      //creates panel
      JPanel p = new JPanel();
	  
	  //creates text prompt
	  JLabel customerType = new JLabel("Are you a customer or a manager?");

      JButton customer = new JButton("Customer");
	  JButton manager = new JButton("Manager");
	  
	  //add action listeners to buttons !! FUNCTIONALITY HAS NOT BEEN IMPLEMENTED!!
	  customer.addActionListener(s);
      manager.addActionListener(s); 


      // adds buttons and label to panel
	  p.add(customerType);
	  p.add(customer);
      p.add(manager);

      // adds panel to frame
      f.add(p);

      // sets the size of frame
      f.setSize(400, 400);

      f.setVisible(true);
	  
    }
	
    // if button is pressed
    public void actionPerformed(ActionEvent e)
    {
        String s = e.getActionCommand();
        if(s.equals("Customer")){
            f.dispose();
            try{
                customer.go();
            } catch(Exception x){
                JOptionPane.showMessageDialog(null,"Failed to open customer GUI.");
            }
        } else if(s.equals("Manager")){
            f.dispose();
            try{
                analyst.go();
            } catch(Exception x){
                JOptionPane.showMessageDialog(null,"Failed to open analyst GUI.");
            }
        }
    }
}