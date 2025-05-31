package quiz.application;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.*;
import java.nio.file.*;

public class ViewQuestions extends JFrame implements ActionListener {
    
    private String teacherEmail;
    private JTable questionsTable;
    private JButton back;
    private String course;
    private JFileChooser fileChooser;
    
    ViewQuestions(String teacherEmail) {
        this.teacherEmail = teacherEmail;
        
        setBounds(50, 0, 1400, 700);
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);
        
        // Get teacher's course
        getCourseForTeacher();
        
        JLabel heading = new JLabel("Quiz Questions");
        heading.setBounds(50, 20, 700, 30);
        heading.setFont(new Font("Tahoma", Font.BOLD, 24));
        add(heading);
        
        // Create table model with columns
        String[] columns = {"S.No.", "Quiz Title", "Question", "Image", "Options", "Correct Answer", "Difficulty"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Create table and add to scroll pane
        questionsTable = new JTable(model);
        questionsTable.setFont(new Font("Tahoma", Font.PLAIN, 14));
        questionsTable.setRowHeight(60);
        
        // Set column widths
        questionsTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        questionsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        questionsTable.getColumnModel().getColumn(2).setPreferredWidth(300);
        questionsTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        questionsTable.getColumnModel().getColumn(4).setPreferredWidth(400);
        questionsTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        questionsTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        
        // Custom renderer for the Image column
        questionsTable.getColumnModel().getColumn(3).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel panel = new JPanel();
                panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
                panel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                
                String imageName = value != null ? value.toString() : "";
                JButton selectButton = new JButton("Select File");
                selectButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
                panel.add(selectButton);
                
                JLabel label = new JLabel(imageName.isEmpty() ? "No file selected" : imageName);
                label.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                panel.add(label);
                
                return panel;
            }
        });
        
        // Add mouse listener for the select file button
        questionsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = questionsTable.rowAtPoint(e.getPoint());
                int col = questionsTable.columnAtPoint(e.getPoint());
                
                if (col == 3 && row >= 0) {
                    Rectangle cellRect = questionsTable.getCellRect(row, col, true);
                    Point tablePoint = e.getPoint();
                    Point panelPoint = new Point(tablePoint.x - cellRect.x, tablePoint.y - cellRect.y);
                    
                    // Check if click was on the button area
                    if (panelPoint.x <= 100) { // Approximate button width
                        selectAndSaveImage(row);
                    }
                }
            }
        });
        
        questionsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        JScrollPane scrollPane = new JScrollPane(questionsTable);
        scrollPane.setBounds(50, 60, 1300, 500);
        add(scrollPane);
        
        // Initialize file chooser
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif"));
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        
        back = new JButton("Back");
        back.setBounds(650, 580, 100, 30);
        back.setBackground(new Color(30, 144, 254));
        back.setForeground(Color.WHITE);
        back.addActionListener(this);
        add(back);
        
        // Load questions
        loadQuestions();
        
        setVisible(true);
    }
    
    private void selectAndSaveImage(int row) {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            int serialNumber = (int) questionsTable.getValueAt(row, 0);
            int questionId = getQuestionId(serialNumber);
            
            if (questionId != -1) {
                try {
                    // Validate file size (max 5MB)
                    if (selectedFile.length() > 5 * 1024 * 1024) {
                        JOptionPane.showMessageDialog(this, 
                            "Image file is too large. Please select an image smaller than 5MB.",
                            "File Too Large",
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Validate file type
                    String fileName = selectedFile.getName().toLowerCase();
                    if (!fileName.endsWith(".jpg") && !fileName.endsWith(".jpeg") && 
                        !fileName.endsWith(".png") && !fileName.endsWith(".gif")) {
                        JOptionPane.showMessageDialog(this,
                            "Please select a valid image file (JPG, PNG, or GIF).",
                            "Invalid File Type",
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    saveImageToDatabase(questionId, selectedFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, 
                        "Error saving image: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void saveImageToDatabase(int questionId, File imageFile) {
        try {
            byte[] imageData = Files.readAllBytes(imageFile.toPath());
            
            Connection conn = DBConnection.getConnection();
            String query = "UPDATE quiz_questions SET question_image = ?, question_image_name = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            
            pstmt.setBytes(1, imageData);
            pstmt.setString(2, imageFile.getName());
            pstmt.setInt(3, questionId);
            
            pstmt.executeUpdate();
            conn.close();
            
            JOptionPane.showMessageDialog(this, "Image saved successfully!");
            loadQuestions(); // Refresh the table
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving image: " + e.getMessage());
        }
    }
    
    private int getQuestionId(int serialNumber) {
        try {
            Connection conn = DBConnection.getConnection();
            String query = """
                SELECT q.id
                FROM quiz_questions q
                JOIN teachers t ON q.teacher_id = t.id
                WHERE t.email = ?
                ORDER BY q.quiz_title, q.id
                LIMIT 1 OFFSET ?
            """;
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, teacherEmail);
            pstmt.setInt(2, serialNumber - 1);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                conn.close();
                return id;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
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
    
    private void loadQuestions() {
        DefaultTableModel model = (DefaultTableModel) questionsTable.getModel();
        model.setRowCount(0);
        
        try {
            Connection conn = DBConnection.getConnection();
            String query = """
                SELECT q.quiz_title, q.question_text, 
                       q.question_image, q.question_image_name,
                       q.option_a, q.option_b, q.option_c, q.option_d, 
                       q.correct_answer, q.difficulty
                FROM quiz_questions q
                JOIN teachers t ON q.teacher_id = t.id
                WHERE t.email = ?
                ORDER BY q.quiz_title, q.id
            """;
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, teacherEmail);
            ResultSet rs = pstmt.executeQuery();
            
            int serialNumber = 1;
            while (rs.next()) {
                String quizTitle = rs.getString("quiz_title");
                String questionText = rs.getString("question_text");
                String imageName = rs.getString("question_image_name");
                
                String options = String.format(
                    "A) %s\nB) %s\nC) %s\nD) %s",
                    rs.getString("option_a"),
                    rs.getString("option_b"),
                    rs.getString("option_c"),
                    rs.getString("option_d")
                );
                
                String correctAnswer = rs.getString("correct_answer");
                String difficulty = rs.getString("difficulty");
                
                model.addRow(new Object[]{
                    serialNumber++,
                    quizTitle,
                    questionText,
                    imageName != null ? imageName : "",
                    options,
                    correctAnswer,
                    difficulty
                });
            }
            
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading questions: " + e.getMessage());
        }
    }
    
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == back) {
            setVisible(false);
            new TeacherHome(teacherEmail);
        }
    }
    
    public static void main(String[] args) {
        new ViewQuestions("test@example.com");
    }
} 