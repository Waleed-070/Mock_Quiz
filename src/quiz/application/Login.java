package quiz.application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class Login extends JFrame implements ActionListener {
 
    JButton beginButton, back, teacherLogin;
    JTextField tfname;
    JComboBox<String> courseDropdown;
    
    Login() {
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        JPanel imagePanel = new JPanel(new BorderLayout());
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/login.jpeg"));
        JLabel image = new JLabel(i1);
        imagePanel.add(image, BorderLayout.CENTER);
        imagePanel.setPreferredSize(new Dimension(600, 500));
        
        JPanel formPanel = new JPanel(null);
        formPanel.setBackground(Color.WHITE);
        
        JLabel heading = new JLabel("Simple Minds");
        heading.setBounds(150, 60, 300, 45);
        heading.setFont(new Font("Viner Hand ITC", Font.BOLD, 40));
        heading.setForeground(new Color(30, 144, 254));
        formPanel.add(heading);
        
        JLabel name = new JLabel("Enter your username");
        name.setBounds(210, 150, 300, 20);
        name.setFont(new Font("Mongolian Baiti", Font.BOLD, 18));
        name.setForeground(new Color(30, 144, 254));
        formPanel.add(name);
        
        tfname = new JTextField();
        tfname.setBounds(135, 200, 300, 25);
        tfname.setFont(new Font("Times New Roman", Font.BOLD, 20));
        formPanel.add(tfname);
        
        JLabel courseLabel = new JLabel("Select Course");
        courseLabel.setBounds(210, 230, 300, 20);
        courseLabel.setFont(new Font("Mongolian Baiti", Font.BOLD, 18));
        courseLabel.setForeground(new Color(30, 144, 254));
        formPanel.add(courseLabel);
        
        ArrayList<String> courseList = getCourses();
        courseDropdown = new JComboBox<>(courseList.toArray(new String[0]));
        courseDropdown.setBounds(135, 260, 300, 25);
        courseDropdown.setBackground(Color.WHITE);
        formPanel.add(courseDropdown);
        
        beginButton = new JButton("Begin");
        beginButton.setBounds(135, 300, 120, 25);
        beginButton.setBackground(new Color(30, 144, 254));
        beginButton.setForeground(Color.WHITE);
        beginButton.addActionListener(this);
        formPanel.add(beginButton);
        
        back = new JButton("Back");
        back.setBounds(315, 300, 120, 25);
        back.setBackground(new Color(30, 144, 254));
        back.setForeground(Color.WHITE);
        back.addActionListener(this);
        formPanel.add(back);
        
        teacherLogin = new JButton("Teacher Login");
        teacherLogin.setBounds(135, 350, 300, 25);
        teacherLogin.setBackground(new Color(30, 144, 254));
        teacherLogin.setForeground(Color.WHITE);
        teacherLogin.addActionListener(this);
        formPanel.add(teacherLogin);
        
        mainPanel.add(imagePanel, BorderLayout.WEST);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        add(mainPanel);
        
        setSize(1200, 500);
        setLocation(200, 150);
        setVisible(true);
    }

    
    private ArrayList<String> getCourses() {
        ArrayList<String> courses = new ArrayList<>();
        courses.add("Select Course"); 
        
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT DISTINCT course FROM teachers WHERE course IS NOT NULL";
            Statement stmt = conn.createStatement();
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
            String checkQuery = "SELECT id FROM students WHERE username = ? AND course = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            checkStmt.setString(2, course);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                String updateQuery = "UPDATE students SET created_at = CURRENT_TIMESTAMP WHERE username = ? AND course = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setString(1, username);
                updateStmt.setString(2, course);
                updateStmt.executeUpdate();
            } else {
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
