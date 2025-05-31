package quiz.application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class TeacherSignUp extends JFrame implements ActionListener {
    JButton register, back;
    JTextField tfname, tfemail, tfcourse;
    JPasswordField tfpassword;
    
    TeacherSignUp() {
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);
        
        JLabel heading = new JLabel("Teacher Registration");
        heading.setBounds(750, 60, 400, 45);
        heading.setFont(new Font("Viner Hand ITC", Font.BOLD, 40));
        heading.setForeground(new Color(30, 144, 254));
        add(heading);
        
        JLabel name = new JLabel("Name");
        name.setBounds(810, 120, 300, 20);
        name.setFont(new Font("Mongolian Baiti", Font.BOLD, 18));
        name.setForeground(new Color(30, 144, 254));
        add(name);
        
        tfname = new JTextField();
        tfname.setBounds(735, 150, 300, 25);
        tfname.setFont(new Font("Times New Roman", Font.BOLD, 20));
        add(tfname);
        
        JLabel email = new JLabel("Email");
        email.setBounds(810, 190, 300, 20);
        email.setFont(new Font("Mongolian Baiti", Font.BOLD, 18));
        email.setForeground(new Color(30, 144, 254));
        add(email);
        
        tfemail = new JTextField();
        tfemail.setBounds(735, 220, 300, 25);
        tfemail.setFont(new Font("Times New Roman", Font.BOLD, 20));
        add(tfemail);
        
        JLabel password = new JLabel("Password");
        password.setBounds(810, 260, 300, 20);
        password.setFont(new Font("Mongolian Baiti", Font.BOLD, 18));
        password.setForeground(new Color(30, 144, 254));
        add(password);
        
        tfpassword = new JPasswordField();
        tfpassword.setBounds(735, 290, 300, 25);
        tfpassword.setFont(new Font("Times New Roman", Font.BOLD, 20));
        add(tfpassword);
        
        JLabel course = new JLabel("Course");
        course.setBounds(810, 330, 300, 20);
        course.setFont(new Font("Mongolian Baiti", Font.BOLD, 18));
        course.setForeground(new Color(30, 144, 254));
        add(course);
        
        tfcourse = new JTextField();
        tfcourse.setBounds(735, 360, 300, 25);
        tfcourse.setFont(new Font("Times New Roman", Font.BOLD, 20));
        add(tfcourse);
        
        register = new JButton("Register");
        register.setBounds(735, 410, 120, 25);
        register.setBackground(new Color(30, 144, 254));
        register.setForeground(Color.WHITE);
        register.addActionListener(this);
        add(register);
        
        back = new JButton("Back");
        back.setBounds(915, 410, 120, 25);
        back.setBackground(new Color(30, 144, 254));
        back.setForeground(Color.WHITE);
        back.addActionListener(this);
        add(back);
        
        setSize(1200, 500);
        setLocation(200, 150);
        setVisible(true);
    }
    
    private boolean registerTeacher(String name, String email, String password, String course) {
        try {
            Connection conn = DBConnection.getConnection();
            
            // Check if email already exists
            String checkQuery = "SELECT id FROM teachers WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Email already registered!");
                return false;
            }
            
            // Insert new teacher
            String insertQuery = "INSERT INTO teachers (name, email, password, course) VALUES (?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, name);
            insertStmt.setString(2, email);
            insertStmt.setString(3, password);
            insertStmt.setString(4, course);
            
            insertStmt.executeUpdate();
            conn.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
            return false;
        }
    }
    
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == register) {
            String name = tfname.getText();
            String email = tfemail.getText();
            String password = new String(tfpassword.getPassword());
            String course = tfcourse.getText();
            
            if (name.trim().isEmpty() || email.trim().isEmpty() || 
                password.trim().isEmpty() || course.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!");
                return;
            }
            
            if (registerTeacher(name, email, password, course)) {
                JOptionPane.showMessageDialog(this, "Registration successful!");
                setVisible(false);
                new TeacherLogin();
            }
        } else if (ae.getSource() == back) {
            setVisible(false);
            new TeacherLogin();
        }
    }
    
    public static void main(String[] args) {
        new TeacherSignUp();
    }
} 