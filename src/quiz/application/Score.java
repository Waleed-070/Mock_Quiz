package quiz.application;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDateTime;

public class Score extends JFrame implements ActionListener {

    private String name;
    private int score;
    private String course;

    Score(String name, int score, String course) {
        this.name = name;
        this.score = score;
        this.course = course;
        
        setBounds(400, 150, 750, 550);
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);
        
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/score.png"));
        Image i2 = i1.getImage().getScaledInstance(300, 250, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel image = new JLabel(i3);
        image.setBounds(0, 200, 300, 250);
        add(image);
        
        JLabel heading = new JLabel("Thank you " + name + " for taking the " + course + " quiz");
        heading.setBounds(45, 30, 700, 30);
        heading.setFont(new Font("Tahoma", Font.PLAIN, 26));
        add(heading);
        
        JLabel lblscore = new JLabel("Your score is " + score);
        lblscore.setBounds(350, 200, 300, 30);
        lblscore.setFont(new Font("Tahoma", Font.PLAIN, 26));
        add(lblscore);
        
        JButton submit = new JButton("Play Again");
        submit.setBounds(380, 270, 120, 30);
        submit.setBackground(new Color(30, 144, 255));
        submit.setForeground(Color.WHITE);
        submit.addActionListener(this);
        add(submit);
        
        // Save score to database
        saveScore();
        
        setVisible(true);
    }
    
    private void saveScore() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = "UPDATE students SET mark = ?, completed_at = CURRENT_TIMESTAMP WHERE username = ? AND course = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, score);
            pstmt.setString(2, name);
            pstmt.setString(3, course);
            pstmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving score: " + e.getMessage());
        }
    }
    
    public void actionPerformed(ActionEvent ae) {
        setVisible(false);
        new Login();
    }

    public static void main(String[] args) {
        new Score("User", 0, "Sample Course");
    }
}
