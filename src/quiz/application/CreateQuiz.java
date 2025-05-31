package quiz.application;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class CreateQuiz extends JFrame implements ActionListener {
    JButton uploadFile, submit, back;
    JTextField quizTitle;
    JTextArea filePreviewArea;
    JLabel selectedFileLabel, statusLabel;
    File selectedFile;
    String teacherEmail;
    List<QuizQuestion> questions;
    
    CreateQuiz(String teacherEmail) {
        this.teacherEmail = teacherEmail;
        this.questions = new ArrayList<>();
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);
        
        JLabel heading = new JLabel("Create New Quiz");
        heading.setBounds(750, 60, 400, 45);
        heading.setFont(new Font("Viner Hand ITC", Font.BOLD, 40));
        heading.setForeground(new Color(30, 144, 254));
        add(heading);
        
        JLabel titleLabel = new JLabel("Quiz Title");
        titleLabel.setBounds(735, 150, 300, 20);
        titleLabel.setFont(new Font("Mongolian Baiti", Font.BOLD, 18));
        titleLabel.setForeground(new Color(30, 144, 254));
        add(titleLabel);
        
        quizTitle = new JTextField();
        quizTitle.setBounds(735, 180, 300, 25);
        quizTitle.setFont(new Font("Times New Roman", Font.BOLD, 20));
        add(quizTitle);
        
        selectedFileLabel = new JLabel("No file selected");
        selectedFileLabel.setBounds(735, 220, 300, 20);
        selectedFileLabel.setFont(new Font("Mongolian Baiti", Font.PLAIN, 14));
        selectedFileLabel.setForeground(Color.GRAY);
        add(selectedFileLabel);
        
        uploadFile = new JButton("Upload Quiz File (TXT/PDF)");
        uploadFile.setBounds(735, 250, 300, 40);
        uploadFile.setBackground(new Color(30, 144, 254));
        uploadFile.setForeground(Color.WHITE);
        uploadFile.setFont(new Font("Times New Roman", Font.BOLD, 20));
        uploadFile.addActionListener(this);
        add(uploadFile);
        
        statusLabel = new JLabel("");
        statusLabel.setBounds(735, 290, 300, 20);
        statusLabel.setFont(new Font("Mongolian Baiti", Font.PLAIN, 14));
        statusLabel.setForeground(Color.RED);
        add(statusLabel);
        
        JLabel previewLabel = new JLabel("File Preview & Debug Info");
        previewLabel.setBounds(735, 320, 300, 20);
        previewLabel.setFont(new Font("Mongolian Baiti", Font.BOLD, 18));
        previewLabel.setForeground(new Color(30, 144, 254));
        add(previewLabel);
        
        filePreviewArea = new JTextArea();
        filePreviewArea.setEditable(false);
        filePreviewArea.setLineWrap(true);
        filePreviewArea.setWrapStyleWord(true);
        filePreviewArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(filePreviewArea);
        scrollPane.setBounds(735, 350, 300, 150);
        add(scrollPane);
        
        submit = new JButton("Submit");
        submit.setBounds(735, 520, 120, 25);
        submit.setBackground(new Color(30, 144, 254));
        submit.setForeground(Color.WHITE);
        submit.addActionListener(this);
        add(submit);
        
        back = new JButton("Back");
        back.setBounds(915, 520, 120, 25);
        back.setBackground(new Color(30, 144, 254));
        back.setForeground(Color.WHITE);
        back.addActionListener(this);
        add(back);
        
        setSize(1200, 650);
        setLocation(200, 150);
        setVisible(true);
    }
    
    private List<String> readFileLines(File file) throws IOException {
        List<String> lines = new ArrayList<>();
        String fileExtension = getFileExtension(file);
        
        if ("pdf".equalsIgnoreCase(fileExtension)) {
            try (PDDocument document = PDDocument.load(file)) {
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document);
                lines.addAll(Arrays.asList(text.split("\\r?\\n")));
            }
        } else if ("txt".equalsIgnoreCase(fileExtension)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }
        } else {
            throw new IOException("Unsupported file format: " + fileExtension);
        }
        
        return lines;
    }
    
    private String getFileExtension(File file) {
        String name = file.getName();
        int lastDotIndex = name.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return name.substring(lastDotIndex + 1);
        }
        return "";
    }
    
    private boolean parseQuizFile(File file) {
        questions.clear();
        StringBuilder debug = new StringBuilder();
        debug.append("=== Debug Information ===\n\n");
        debug.append("Processing file: ").append(file.getName()).append("\n");
        debug.append("File type: ").append(getFileExtension(file).toUpperCase()).append("\n\n");
        
        try { 
            List<String> lines = readFileLines(file);
            String questionText = null;
            String[] options = new String[4];
            String correctAnswer = null;
            String difficulty = null;
            byte[] questionImage = null;
            String questionImageName = null;
            int optionIndex = 0;
            int lineNumber = 0;
            
            for (String line : lines) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) {
                    debug.append("Line ").append(lineNumber).append(": Empty line\n");
                    continue;
                }
                
                debug.append("Line ").append(lineNumber).append(": ").append(line).append("\n");
                
                if (line.matches("\\d+\\..*")) {
                    debug.append("Found new question at line ").append(lineNumber).append("\n");
                    if (questionText != null && correctAnswer != null && difficulty != null) {
                        questions.add(new QuizQuestion(questionText, options.clone(), correctAnswer, difficulty, questionImage, questionImageName));
                        debug.append("Added previous question to list. Total questions: ").append(questions.size()).append("\n");
                    }
                    questionText = line.substring(line.indexOf(".") + 1).trim();
                    optionIndex = 0;
                    options = new String[4];
                    correctAnswer = null;
                    difficulty = null;
                    questionImage = null;
                    questionImageName = null;
                } else if (line.matches("[A-D]\\)\\s*.*")) {
                    debug.append("Found option: ").append(line).append("\n");
                    if (optionIndex < 4) {
                        options[optionIndex++] = line.trim();
                    }
                } else if (line.toLowerCase().startsWith("correct option:")) {
                    debug.append("Found correct answer\n");
                    correctAnswer = line.substring(line.indexOf(":") + 1).trim();
                    if (correctAnswer.contains("(")) {
                        correctAnswer = correctAnswer.substring(0, correctAnswer.indexOf("(")).trim();
                    }
                } else if (line.toLowerCase().startsWith("difficulty:")) {
                    debug.append("Found difficulty\n");
                    difficulty = line.substring(line.indexOf(":") + 1).trim();
                } else if (line.toLowerCase().startsWith("image:")) {
                    debug.append("Found image reference\n");
                    String imagePath = line.substring(line.indexOf(":") + 1).trim();
                    File imageFile = new File(file.getParent(), imagePath);
                    if (imageFile.exists()) {
                        questionImageName = imagePath;
                        questionImage = Files.readAllBytes(imageFile.toPath());
                        debug.append("Successfully loaded image: ").append(imagePath).append("\n");
                    } else {
                        debug.append("Warning: Image file not found: ").append(imagePath).append("\n");
                    }
                } else {
                    debug.append("Unrecognized line format at line ").append(lineNumber).append("\n");
                }
            }
            
            // Add the last question
            if (questionText != null && correctAnswer != null && difficulty != null) {
                questions.add(new QuizQuestion(questionText, options.clone(), correctAnswer, difficulty, questionImage, questionImageName));
                debug.append("Added final question. Total questions: ").append(questions.size()).append("\n");
            }
            
            debug.append("\nParsing completed. Found ").append(questions.size()).append(" questions.\n");
            filePreviewArea.setText(debug.toString());
            
            if (questions.isEmpty()) {
                statusLabel.setText("No valid questions found in the file!");
                return false;
            }
            
            statusLabel.setText("Successfully loaded " + questions.size() + " questions");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            debug.append("\nError reading file: ").append(e.getMessage()).append("\n");
            filePreviewArea.setText(debug.toString());
            statusLabel.setText("Error reading file!");
            return false;
        }
    }
    
    private void saveQuizToDB() {
        if (questions.isEmpty() || quizTitle.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a valid quiz file and enter a title!");
            return;
        }
        
        try {
            Connection conn = DBConnection.getConnection();
            
            // Get teacher ID
            String teacherQuery = "SELECT id FROM teachers WHERE email = ?";
            PreparedStatement teacherStmt = conn.prepareStatement(teacherQuery);
            teacherStmt.setString(1, teacherEmail);
            ResultSet rs = teacherStmt.executeQuery();
            
            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Teacher not found!");
                return;
            }
            
            int teacherId = rs.getInt("id");
            
            // Create questions table if it doesn't exist
            String createTableQuery = """
                CREATE TABLE IF NOT EXISTS quiz_questions (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    teacher_id INT NOT NULL,
                    quiz_title VARCHAR(255) NOT NULL,
                    question_text TEXT NOT NULL,
                    question_image MEDIUMBLOB,
                    question_image_name VARCHAR(255),
                    option_a VARCHAR(255) NOT NULL,
                    option_b VARCHAR(255) NOT NULL,
                    option_c VARCHAR(255) NOT NULL,
                    option_d VARCHAR(255) NOT NULL,
                    correct_answer CHAR(1) NOT NULL,
                    difficulty VARCHAR(10) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (teacher_id) REFERENCES teachers(id)
                )
            """;
            Statement stmt = conn.createStatement();
            stmt.execute(createTableQuery);
            
            // Delete all existing questions from this teacher
            String deleteQuery = "DELETE FROM quiz_questions WHERE teacher_id = ?";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
            deleteStmt.setInt(1, teacherId);
            deleteStmt.executeUpdate();
            
            // Insert questions
            String insertQuery = """
                INSERT INTO quiz_questions (teacher_id, quiz_title, question_text, 
                question_image, question_image_name,
                option_a, option_b, option_c, option_d, correct_answer, difficulty)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
            PreparedStatement pst = conn.prepareStatement(insertQuery);
            
            for (QuizQuestion question : questions) {
                pst.setInt(1, teacherId);
                pst.setString(2, quizTitle.getText().trim());
                pst.setString(3, question.getQuestionText());
                if (question.hasImage()) {
                    pst.setBytes(4, question.getQuestionImage());
                    pst.setString(5, question.getQuestionImageName());
                } else {
                    pst.setNull(4, java.sql.Types.BLOB);
                    pst.setNull(5, java.sql.Types.VARCHAR);
                }
                String[] options = question.getOptions();
                for (int i = 0; i < 4; i++) {
                    pst.setString(6 + i, options[i]);
                }
                pst.setString(10, question.getCorrectAnswer().substring(0, 1)); // Store just A, B, C, or D
                pst.setString(11, question.getDifficulty());
                pst.executeUpdate();
            }
            
            JOptionPane.showMessageDialog(this, "Quiz saved successfully!");
            setVisible(false);
            new TeacherHome(teacherEmail);
            
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving quiz: " + e.getMessage());
        }
    }
    
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == uploadFile) {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Quiz Files (*.txt, *.pdf)", "txt", "pdf"
            );
            fileChooser.setFileFilter(filter);
            
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                String extension = getFileExtension(selectedFile);
                if (!extension.equalsIgnoreCase("txt") && !extension.equalsIgnoreCase("pdf")) {
                    JOptionPane.showMessageDialog(this, 
                        "Please select a valid TXT or PDF file!", 
                        "Invalid File", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                selectedFileLabel.setText("Selected: " + selectedFile.getName());
                if (parseQuizFile(selectedFile)) {
                    JOptionPane.showMessageDialog(this, 
                        "File loaded successfully!\nFound " + questions.size() + " questions.");
                }
            }
        } else if (ae.getSource() == submit) {
            saveQuizToDB();
        } else if (ae.getSource() == back) {
            setVisible(false);
            new TeacherHome(teacherEmail);
        }
    }
} 