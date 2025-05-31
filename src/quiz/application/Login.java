package quiz.application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class Login extends JFrame implements ActionListener {
 
    JButton beginButton, back, teacherLogin;
    JTextField tfname;
    JComboBox<String> courseDropdown;
    
    Login() {
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);
        
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/login.jpeg"));
        JLabel image = new JLabel(i1);
        image.setBounds(0, 0, 600, 500);
        add(image);
        
        JLabel heading = new JLabel("Simple Minds");
        heading.setBounds(750, 60, 300, 45);
        heading.setFont(new Font("Viner Hand ITC", Font.BOLD, 40));
        heading.setForeground(new Color(30, 144, 254));
        add(heading);
        
        JLabel name = new JLabel("Enter your username");
        name.setBounds(810, 150, 300, 20);
        name.setFont(new Font("Mongolian Baiti", Font.BOLD, 18));
        name.setForeground(new Color(30, 144, 254));
        add(name);
        
        tfname = new JTextField();
        tfname.setBounds(735, 200, 300, 25);
        tfname.setFont(new Font("Times New Roman", Font.BOLD, 20));
        add(tfname);
        
        JLabel courseLabel = new JLabel("Select Course");
        courseLabel.setBounds(810, 230, 300, 20);
        courseLabel.setFont(new Font("Mongolian Baiti", Font.BOLD, 18));
        courseLabel.setForeground(new Color(30, 144, 254));
        add(courseLabel);
        
        courseDropdown = new JComboBox<>(getCourses());
        courseDropdown.setBounds(735, 260, 300, 25);
        courseDropdown.setBackground(Color.WHITE);
        add(courseDropdown);
        
        beginButton = new JButton("Begin");
        beginButton.setBounds(735, 300, 120, 25);
        beginButton.setBackground(new Color(30, 144, 254));
        beginButton.setForeground(Color.WHITE);
        beginButton.addActionListener(this);
        add(beginButton);
        
        back = new JButton("Back");
        back.setBounds(915, 300, 120, 25);
        back.setBackground(new Color(30, 144, 254));
        back.setForeground(Color.WHITE);
        back.addActionListener(this);
        add(back);
        
        teacherLogin = new JButton("Teacher Login");
        teacherLogin.setBounds(735, 350, 300, 25);
        teacherLogin.setBackground(new Color(30, 144, 254));
        teacherLogin.setForeground(Color.WHITE);
        teacherLogin.addActionListener(this);
        add(teacherLogin);
        
        setSize(1200, 500);
        setLocation(200, 150);
        setVisible(true);
    }

    
    private Vector<String> getCourses() {
        Vector<String> courses = new Vector<>();
        courses.add("Select Course"); // Default option
        
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            String query = "SELECT DISTINCT course FROM teachers WHERE course IS NOT NULL";
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                String course = rs.getString("course");
                if (course != null && !course.trim().isEmpty()) {
                    courses.add(course);
                }
            }
            
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading courses: " + e.getMessage());
        }
        
        return courses;
    }
    
    private void updateStudentRecord(String username, String course) {
        try {
            Connection conn = DBConnection.getConnection();
            
            // Check if student exists
            String checkQuery = "SELECT id FROM students WHERE username = ? AND course = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            checkStmt.setString(2, course);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                // Update existing student's timestamp
                String updateQuery = "UPDATE students SET created_at = CURRENT_TIMESTAMP WHERE username = ? AND course = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setString(1, username);
                updateStmt.setString(2, course);
                updateStmt.executeUpdate();
            } else {
                // Insert new student
                String insertQuery = "INSERT INTO students (username, course, mark) VALUES (?, ?, 0)";
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setString(1, username);
                insertStmt.setString(2, course);
                insertStmt.executeUpdate();
            }
            
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }
    
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == beginButton) {
            String username = tfname.getText();
            String selectedCourse = (String) courseDropdown.getSelectedItem();
            
            if (username.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your username!");
                return;
            }
            
            if (selectedCourse == null || selectedCourse.equals("Select Course")) {
                JOptionPane.showMessageDialog(this, "Please select a course!");
                return;
            }
            
            // Update student record in database
            updateStudentRecord(username, selectedCourse);
            
            setVisible(false);
            new Quiz(username, selectedCourse);
        } else if (ae.getSource() == back) {
            setVisible(false);
        } else if (ae.getSource() == teacherLogin) {
            setVisible(false);
            new TeacherLogin();
        }
    }
    
    public static void main(String[] args) {
        new Login();
    }
}
