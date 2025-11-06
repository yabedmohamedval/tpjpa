package web.view;

public class QuizRow {
    private Long id;
    private String title;
    private String author;
    private long questionCount;

    public QuizRow(Long id, String title, String author, long questionCount) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.questionCount = questionCount;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public long getQuestionCount() { return questionCount; }
}
