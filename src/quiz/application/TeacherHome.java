package quiz.application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TeacherHome extends JFrame implements ActionListener {
    JButton createQuiz, viewResults, viewQuestions, logout;
    JLabel heading;
    String teacherEmail;
    
    TeacherHome(String teacherEmail) {
        this.teacherEmail = teacherEmail;
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);
        
        heading = new JLabel("Teacher Dashboard");
        heading.setBounds(270, 60, 400, 45);
        heading.setFont(new Font("Viner Hand ITC", Font.BOLD, 40));
        heading.setForeground(new Color(30, 144, 254));
        add(heading);
        
        createQuiz = new JButton("Create Quiz");
        createQuiz.setBounds(300, 150, 300, 40);
        createQuiz.setBackground(new Color(30, 144, 254));
        createQuiz.setForeground(Color.WHITE);
        createQuiz.setFont(new Font("Times New Roman", Font.BOLD, 20));
        createQuiz.addActionListener(this);
        add(createQuiz);
        
        viewQuestions = new JButton("View Questions");
        viewQuestions.setBounds(300, 220, 300, 40);
        viewQuestions.setBackground(new Color(30, 144, 254));
        viewQuestions.setForeground(Color.WHITE);
        viewQuestions.setFont(new Font("Times New Roman", Font.BOLD, 20));
        viewQuestions.addActionListener(this);
        add(viewQuestions);
        
        viewResults = new JButton("View Results");
        viewResults.setBounds(300, 290, 300, 40);
        viewResults.setBackground(new Color(30, 144, 254));
        viewResults.setForeground(Color.WHITE);
        viewResults.setFont(new Font("Times New Roman", Font.BOLD, 20));
        viewResults.addActionListener(this);
        add(viewResults);
        
        logout = new JButton("Logout");
        logout.setBounds(300, 360, 300, 40);
        logout.setBackground(new Color(30, 144, 254));
        logout.setForeground(Color.WHITE);
        logout.setFont(new Font("Times New Roman", Font.BOLD, 20));
        logout.addActionListener(this);
        add(logout);
        
        setSize(800, 500);
        setLocation(200, 150);
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == createQuiz) {
            setVisible(false);
            new CreateQuiz(teacherEmail);
        } else if (ae.getSource() == viewQuestions) {
            setVisible(false);
            new ViewQuestions(teacherEmail);
        } else if (ae.getSource() == viewResults) {
            setVisible(false);
            new ViewResults(teacherEmail);
        } else if (ae.getSource() == logout) {
            setVisible(false);
            new Login();
        }
    }
    
    public static void main(String[] args) {
        new TeacherHome("test@example.com"); 
    }
} 