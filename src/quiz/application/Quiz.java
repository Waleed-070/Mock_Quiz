package quiz.application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.border.EmptyBorder;

public class Quiz extends JFrame implements ActionListener {
    
    String questions[][] = new String[10][5];
    String answers[][] = new String[10][2];
    String useranswers[][] = new String[10][1];
    byte[][] questionImages = new byte[10][];
    JLabel qno, question;
    JLabel questionImage;
    JButton viewFullImage;
    JDialog imageDialog;
    JPanel questionPanel;
    JRadioButton opt1, opt2, opt3, opt4;
    ButtonGroup groupoptions;
    JButton next, submit;
    
    public static int timer = 15;
    public static int ans_given = 0;
    public static int count = 0;
    public static int score = 0;
    
    String name;
    String course;
    
    Quiz(String name, String course) {
        this.name = name;
        this.course = course;
        setBounds(50, 0, 1440, 800);
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);
        
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/quiz.jpg"));
        JLabel image = new JLabel(i1);
        image.setBounds(0, 0, 1440, 300);
        add(image);
        
        qno = new JLabel();
        qno.setBounds(100, 320, 50, 30);
        qno.setFont(new Font("Tahoma", Font.PLAIN, 24));
        add(qno);
        
        questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
        questionPanel.setBackground(Color.WHITE);
        questionPanel.setBounds(150, 320, 900, 150);
        
        question = new JLabel();
        question.setFont(new Font("Tahoma", Font.PLAIN, 24));
        question.setBorder(new EmptyBorder(0, 0, 10, 0));
        questionPanel.add(question);
        
        // Create image panel with thumbnail and view button
        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        imagePanel.setBackground(Color.WHITE);
        imagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        questionImage = new JLabel();
        questionImage.setPreferredSize(new Dimension(150, 100));
        imagePanel.add(questionImage);
        
        viewFullImage = new JButton("View Full Image");
        viewFullImage.setVisible(false);
        viewFullImage.addActionListener(e -> showFullImage());
        imagePanel.add(viewFullImage);
        
        questionPanel.add(imagePanel);
        add(questionPanel);
        
        // Create image dialog for full-size view
        imageDialog = new JDialog(this, "Full Image", true);
        imageDialog.setLayout(new BorderLayout());
        JLabel fullImageLabel = new JLabel();
        fullImageLabel.setHorizontalAlignment(JLabel.CENTER);
        JScrollPane scrollPane = new JScrollPane(fullImageLabel);
        imageDialog.add(scrollPane, BorderLayout.CENTER);
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> imageDialog.setVisible(false));
        imageDialog.add(closeButton, BorderLayout.SOUTH);
        imageDialog.setSize(800, 400);
        imageDialog.setLocationRelativeTo(this);
        
        // Load questions from database
        loadQuestionsFromDB();
        
        opt1 = new JRadioButton();
        opt1.setBounds(170, 500, 700, 30);
        opt1.setBackground(Color.WHITE);
        opt1.setFont(new Font("Dialog", Font.PLAIN, 20));
        add(opt1);
        
        opt2 = new JRadioButton();
        opt2.setBounds(170, 540, 700, 30);
        opt2.setBackground(Color.WHITE);
        opt2.setFont(new Font("Dialog", Font.PLAIN, 20));
        add(opt2);
        
        opt3 = new JRadioButton();
        opt3.setBounds(170, 580, 700, 30);
        opt3.setBackground(Color.WHITE);
        opt3.setFont(new Font("Dialog", Font.PLAIN, 20));
        add(opt3);
        
        opt4 = new JRadioButton();
        opt4.setBounds(170, 620, 700, 30);
        opt4.setBackground(Color.WHITE);
        opt4.setFont(new Font("Dialog", Font.PLAIN, 20));
        add(opt4);
        
        groupoptions = new ButtonGroup();
        groupoptions.add(opt1);
        groupoptions.add(opt2);
        groupoptions.add(opt3);
        groupoptions.add(opt4);
        
        next = new JButton("Next");
        next.setBounds(1100, 500, 200, 40);
        next.setFont(new Font("Tahoma", Font.PLAIN, 22));
        next.setBackground(new Color(30, 144, 255));
        next.setForeground(Color.WHITE);
        next.addActionListener(this);
        add(next);
        
        submit = new JButton("Submit");
        submit.setBounds(1100, 560, 200, 40);
        submit.setFont(new Font("Tahoma", Font.PLAIN, 22));
        submit.setBackground(new Color(30, 144, 255));
        submit.setForeground(Color.WHITE);
        submit.setEnabled(false);
        submit.addActionListener(this);
        add(submit);
        
        start(0);
        
        setVisible(true);
    }
    
    private void showFullImage() {
        if (questionImages[count] != null && questionImages[count].length > 0) {
            ImageIcon icon = new ImageIcon(questionImages[count]);
            JLabel fullImageLabel = (JLabel) ((JScrollPane) imageDialog.getContentPane().getComponent(0)).getViewport().getView();
            
            // Scale image if it's too large while maintaining aspect ratio
            if (icon.getIconWidth() > 750 || icon.getIconHeight() > 550) {
                double scale = Math.min(750.0 / icon.getIconWidth(), 550.0 / icon.getIconHeight());
                Image scaledImg = icon.getImage().getScaledInstance(
                    (int)(icon.getIconWidth() * scale),
                    (int)(icon.getIconHeight() * scale),
                    Image.SCALE_SMOOTH
                );
                fullImageLabel.setIcon(new ImageIcon(scaledImg));
            } else {
                fullImageLabel.setIcon(icon);
            }
            
            imageDialog.setVisible(true);
        }
    }
    
    private void loadQuestionsFromDB() {
        try {
            Connection conn = DBConnection.getConnection();
            
            // Get questions for the specific course
            String query = "SELECT q.* FROM quiz_questions q " +
                         "JOIN teachers t ON q.teacher_id = t.id " +
                         "WHERE t.course = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, course);
            ResultSet rs = pstmt.executeQuery();
            
            // Store questions in a list
            List<Integer> indices = new ArrayList<>();
            List<String[]> allQuestions = new ArrayList<>();
            List<String[]> allAnswers = new ArrayList<>();
            List<byte[]> allImages = new ArrayList<>();
            
            while (rs.next()) {
                String[] questionData = new String[5];
                questionData[0] = rs.getString("question_text");
                questionData[1] = rs.getString("option_a");
                questionData[2] = rs.getString("option_b");
                questionData[3] = rs.getString("option_c");
                questionData[4] = rs.getString("option_d");
                
                String[] answerData = new String[2];
                answerData[1] = rs.getString("option_" + rs.getString("correct_answer").toLowerCase());
                
                byte[] imageData = rs.getBytes("question_image");
                
                allQuestions.add(questionData);
                allAnswers.add(answerData);
                allImages.add(imageData);
                indices.add(allQuestions.size() - 1);
            }
            
            // Randomly select 10 questions
            Collections.shuffle(indices);
            for (int i = 0; i < Math.min(10, indices.size()); i++) {
                int idx = indices.get(i);
                questions[i] = allQuestions.get(idx);
                answers[i] = allAnswers.get(idx);
                questionImages[i] = allImages.get(idx);
            }
            
            // If we don't have enough questions, show an error message
            if (indices.size() < 10) {
                JOptionPane.showMessageDialog(this, 
                    "Not enough questions available for " + course + ". Only " + indices.size() + " questions found.");
                // Fill remaining with placeholder questions
                for (int i = indices.size(); i < 10; i++) {
                    questions[i][0] = "Question not available";
                    questions[i][1] = "N/A";
                    questions[i][2] = "N/A";
                    questions[i][3] = "N/A";
                    questions[i][4] = "N/A";
                    answers[i][1] = "N/A";
                    questionImages[i] = null;
                }
            }
            
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading questions: " + e.getMessage());
        }
    }
    
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == next) {
            repaint();
            opt1.setEnabled(true);
            opt2.setEnabled(true);
            opt3.setEnabled(true);
            opt4.setEnabled(true);
            
            ans_given = 1;
            if (groupoptions.getSelection() == null) {
                useranswers[count][0] = "";
            } else {
                useranswers[count][0] = groupoptions.getSelection().getActionCommand();
            }
            
            if (count == 8) {
                next.setEnabled(false);
                submit.setEnabled(true);
            }
            
            count++;
            start(count);
        } else if (ae.getSource() == submit) {
            ans_given = 1;
            if (groupoptions.getSelection() == null) {
                useranswers[count][0] = "";
            } else {
                useranswers[count][0] = groupoptions.getSelection().getActionCommand();
            }

            for (int i = 0; i < useranswers.length; i++) {
                if (useranswers[i][0].equals(answers[i][1])) {
                    score += 10;
                }
            }
            setVisible(false);
            new Score(name, score, course);
        }
    }
    
    public void paint(Graphics g) {
        super.paint(g);
        
        String time = "Time left - " + timer + " seconds";
        g.setColor(Color.RED);
        g.setFont(new Font("Tahoma", Font.BOLD, 25));
        
        if (timer > 0) { 
            g.drawString(time, 1100, 500);
        } else {
            g.drawString("Times up!!", 1100, 500);
        }
        
        timer--;
        
        try {
            Thread.sleep(1000);
            repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (ans_given == 1) {
            ans_given = 0;
            timer = 15;
        } else if (timer < 0) {
            timer = 15;
            opt1.setEnabled(true);
            opt2.setEnabled(true);
            opt3.setEnabled(true);
            opt4.setEnabled(true);
            
            if (count == 8) {
                next.setEnabled(false);
                submit.setEnabled(true);
            }
            if (count == 9) {
                if (groupoptions.getSelection() == null) {
                   useranswers[count][0] = "";
                } else {
                    useranswers[count][0] = groupoptions.getSelection().getActionCommand();
                }
                
                for (int i = 0; i < useranswers.length; i++) {
                    if (useranswers[i][0].equals(answers[i][1])) {
                        score += 10;
                    }
                }
                setVisible(false);
                new Score(name, score, course);
            } else {
                if (groupoptions.getSelection() == null) {
                   useranswers[count][0] = "";
                } else {
                    useranswers[count][0] = groupoptions.getSelection().getActionCommand();
                }
                count++;
                start(count);
            }
        }
    }
    
    public void start(int count) {
        qno.setText("" + (count + 1) + ". ");
        question.setText(questions[count][0]);
        
        // Handle question image
        if (questionImages[count] != null && questionImages[count].length > 0) {
            ImageIcon originalIcon = new ImageIcon(questionImages[count]);
            
            // Create thumbnail
            Image img = originalIcon.getImage();
            Image thumbnailImg = img.getScaledInstance(150, 100, Image.SCALE_SMOOTH);
            ImageIcon thumbnailIcon = new ImageIcon(thumbnailImg);
            
            questionImage.setIcon(thumbnailIcon);
            questionImage.setVisible(true);
            viewFullImage.setVisible(true);
        } else {
            questionImage.setIcon(null);
            questionImage.setVisible(false);
            viewFullImage.setVisible(false);
        }
        
        opt1.setText(questions[count][1]);
        opt1.setActionCommand(questions[count][1]);
        
        opt2.setText(questions[count][2]);
        opt2.setActionCommand(questions[count][2]);
        
        opt3.setText(questions[count][3]);
        opt3.setActionCommand(questions[count][3]);
        
        opt4.setText(questions[count][4]);
        opt4.setActionCommand(questions[count][4]);
        
        groupoptions.clearSelection();
    }
    
    public static void main(String[] args) {
        new Quiz("User", "Sample Course");
    }
}
