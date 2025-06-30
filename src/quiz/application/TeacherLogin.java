package quiz.application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class TeacherLogin extends JFrame implements ActionListener {
    JButton login, register, back;
    JTextField tfemail;
    JPasswordField tfpassword;
    
    TeacherLogin() {
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);
        
        // Create a main panel with BorderLayout to center the login panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(null);
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        loginPanel.setPreferredSize(new Dimension(400, 350));
        
        JLabel heading = new JLabel("Teacher Login");
        heading.setBounds(100, 20, 300, 45);
        heading.setFont(new Font("Viner Hand ITC", Font.BOLD, 30));
        heading.setForeground(new Color(30, 144, 254));
        loginPanel.add(heading);
        
        JLabel email = new JLabel("Email");
        email.setBounds(50, 80, 300, 20);
        email.setFont(new Font("Mongolian Baiti", Font.BOLD, 18));
        email.setForeground(new Color(30, 144, 254));
        loginPanel.add(email);
        
        tfemail = new JTextField();
        tfemail.setBounds(50, 110, 300, 25);
        tfemail.setFont(new Font("Times New Roman", Font.BOLD, 16));
        loginPanel.add(tfemail);
        
        JLabel password = new JLabel("Password");
        password.setBounds(50, 150, 300, 20);
        password.setFont(new Font("Mongolian Baiti", Font.BOLD, 18));
        password.setForeground(new Color(30, 144, 254));
        loginPanel.add(password);
        
        tfpassword = new JPasswordField();
        tfpassword.setBounds(50, 180, 300, 25);
        tfpassword.setFont(new Font("Times New Roman", Font.BOLD, 16));
        loginPanel.add(tfpassword);
        
        register = new JButton("Register");
        register.setBounds(50, 230, 120, 30);
        register.setBackground(new Color(30, 144, 254));
        register.setForeground(Color.WHITE);
        register.addActionListener(this);
        loginPanel.add(register);
        
        login = new JButton("Login");
        login.setBounds(230, 230, 120, 30);
        login.setBackground(new Color(30, 144, 254));
        login.setForeground(Color.WHITE);
        login.addActionListener(this);
        loginPanel.add(login);
        
        back = new JButton("Back");
        back.setBounds(140, 280, 120, 30);
        back.setBackground(new Color(30, 144, 254));
        back.setForeground(Color.WHITE);
        back.addActionListener(this);
        loginPanel.add(back);
        
        // Add the login panel to the main panel which will center it
        mainPanel.add(loginPanel);
        
        // Add the main panel to the frame
        setContentPane(mainPanel);
        
        // Set frame properties
        setSize(600, 500);
        setLocationRelativeTo(null); // Center the window on the screen
        setResizable(false);
        setVisible(true);
    }
    
    private boolean validateTeacher(String email, String password) {
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT * FROM teachers WHERE email = ? AND password = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, email);
            pst.setString(2, password);
            
            ResultSet rs = pst.executeQuery();
            boolean exists = rs.next();
            
            conn.close();
            return exists;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
            return false;
        }
    }
    
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == login) {
            String email = tfemail.getText();
            String password = new String(tfpassword.getPassword());
            
            if (email.trim().isEmpty() || password.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both email and password!");
                return;
            }
            
            if (validateTeacher(email, password)) {
                setVisible(false);
                new TeacherHome(email);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid email or password!");
            }
        } else if (ae.getSource() == register) {
            setVisible(false);
            new TeacherSignUp();
        } else if (ae.getSource() == back) {
            setVisible(false);
            new Login();
        }
    }
    
    public static void main(String[] args) {
        new TeacherLogin();
    }
} 