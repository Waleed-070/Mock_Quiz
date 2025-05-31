package quiz.application;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ViewResults extends JFrame implements ActionListener {
    
    private String teacherEmail;
    private JTable resultsTable;
    private JButton back;
    private String course;
    
    ViewResults(String teacherEmail) {
        this.teacherEmail = teacherEmail;
        
        setBounds(50, 0, 1000, 700);
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);
        
        // Get teacher's course
        getCourseForTeacher();
        
        JLabel heading = new JLabel(course + " Course Results");
        heading.setBounds(50, 20, 700, 30);
        heading.setFont(new Font("Tahoma", Font.BOLD, 24));
        add(heading);
        
        // Create table model with columns
        String[] columns = {"Student Name", "Score", "Completion Time"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        // Create table and add to scroll pane
        resultsTable = new JTable(model);
        resultsTable.setFont(new Font("Tahoma", Font.PLAIN, 16));
        resultsTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setBounds(50, 60, 900, 500);
        add(scrollPane);
        
        back = new JButton("Back");
        back.setBounds(450, 580, 100, 30);
        back.setBackground(new Color(30, 144, 254));
        back.setForeground(Color.WHITE);
        back.addActionListener(this);
        add(back);
        
        // Load results
        loadResults();
        
        setVisible(true);
    }
    
    private void getCourseForTeacher() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT course FROM teachers WHERE email = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, teacherEmail);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                course = rs.getString("course");
            } else {
                JOptionPane.showMessageDialog(this, "Error: Course not found for teacher");
                dispose();
                new TeacherHome(teacherEmail);
            }
            
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error getting teacher's course: " + e.getMessage());
        }
    }
    
    private void loadResults() {
        DefaultTableModel model = (DefaultTableModel) resultsTable.getModel();
        model.setRowCount(0); // Clear existing rows
        
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT username, mark, completed_at FROM students " +
                         "WHERE course = ? AND completed_at IS NOT NULL " +
                         "ORDER BY completed_at DESC";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, course);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String username = rs.getString("username");
                int mark = rs.getInt("mark");
                Timestamp completedAt = rs.getTimestamp("completed_at");
                
                model.addRow(new Object[]{
                    username,
                    mark,
                    completedAt
                });
            }
            
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading results: " + e.getMessage());
        }
    }
    
    public void actionPerformed(ActionEvent ae) {
        setVisible(false);
        new TeacherHome(teacherEmail);
    }
    
    public static void main(String[] args) {
        new ViewResults("test@example.com");
    }
} 