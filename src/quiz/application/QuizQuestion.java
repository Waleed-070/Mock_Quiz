package quiz.application;

public class QuizQuestion {
    private String questionText;
    private String[] options;
    private String correctAnswer;
    private String difficulty;
    private byte[] questionImage;
    private String questionImageName;
    
    public QuizQuestion(String questionText, String[] options, String correctAnswer, String difficulty) {
        this(questionText, options, correctAnswer, difficulty, null, null);
    }
    
    public QuizQuestion(String questionText, String[] options, String correctAnswer, String difficulty, byte[] questionImage, String questionImageName) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.difficulty = difficulty;
        this.questionImage = questionImage;
        this.questionImageName = questionImageName;
    } 
    
    public String getQuestionText() {
        return questionText;
    }
    
    public String[] getOptions() {
        return options;
    }
    
    public String getCorrectAnswer() {
        return correctAnswer;
    }
    
    public String getDifficulty() {
        return difficulty;
    }
    
    public byte[] getQuestionImage() {
        return questionImage;
    }
    
    public String getQuestionImageName() {
        return questionImageName;
    }
    
    public boolean hasImage() {
        return questionImage != null && questionImage.length > 0;
    }
    
    public int getCorrectOptionIndex() {
       
        return correctAnswer.charAt(0) - 'A';
    }
} 