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
        
        // Add background image
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/teacherLogin.jpg"));
        Image i2 = i1.getImage().getScaledInstance(600, 500, Image.SCALE_SMOOTH);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel image = new JLabel(i3);
        image.setBounds(0, 0, 600, 500);
        add(image);
        
        // Login panel on the right side
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(null);
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setBounds(600, 0, 600, 500);
        add(loginPanel);
        
        JLabel heading = new JLabel("Teacher Login");
        heading.setBounds(100, 60, 300, 45);
        heading.setFont(new Font("Viner Hand ITC", Font.BOLD, 30));
        heading.setForeground(new Color(30, 144, 254));
        loginPanel.add(heading);
        
        JLabel email = new JLabel("Email");
        email.setBounds(50, 150, 300, 20);
        email.setFont(new Font("Mongolian Baiti", Font.BOLD, 18));
        email.setForeground(new Color(30, 144, 254));
        loginPanel.add(email);
        
        tfemail = new JTextField();
        tfemail.setBounds(50, 180, 300, 25);
        tfemail.setFont(new Font("Times New Roman", Font.BOLD, 16));
        loginPanel.add(tfemail);
        
        JLabel password = new JLabel("Password");
        password.setBounds(50, 220, 300, 20);
        password.setFont(new Font("Mongolian Baiti", Font.BOLD, 18));
        password.setForeground(new Color(30, 144, 254));
        loginPanel.add(password);
        
        tfpassword = new JPasswordField();
        tfpassword.setBounds(50, 250, 300, 25);
        tfpassword.setFont(new Font("Times New Roman", Font.BOLD, 16));
        loginPanel.add(tfpassword);
        
        register = new JButton("Register");
        register.setBounds(50, 300, 120, 30);
        register.setBackground(new Color(30, 144, 254));
        register.setForeground(Color.WHITE);
        register.addActionListener(this);
        loginPanel.add(register);
        
        login = new JButton("Login");
        login.setBounds(230, 300, 120, 30);
        login.setBackground(new Color(30, 144, 254));
        login.setForeground(Color.WHITE);
        login.addActionListener(this);
        loginPanel.add(login);
        
        back = new JButton("Back");
        back.setBounds(140, 350, 120, 30);
        back.setBackground(new Color(30, 144, 254));
        back.setForeground(Color.WHITE);
        back.addActionListener(this);
        loginPanel.add(back);
        
        // Set frame properties
        setSize(1200, 500);
        setLocationRelativeTo(null);
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