package com.mungwaagu.quizapp.models;

public class QuestionModel {

    private String question, optionA, optionB, optionC, correctAnswer;

    public QuestionModel(String question, String optionA, String optionB, String optionC, String correctAnswer) {
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestions() {
        return question;
    }
    public String getOptionA() {
        return optionA;
    }
    public String getOptionB() {
        return optionB;
    }
    public String getOptionC() {
        return optionC;
    }
    public String getCorrectAnswer() {
        return correctAnswer;
    }
}
