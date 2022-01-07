package com.mungwaagu.quizapp.models;

public class CategoryModel {

    public String category_name;
    public String category_total_questions;

    public CategoryModel(String category_name, String category_total_questions){
        this.category_name = category_name;
        this.category_total_questions = category_total_questions;
    }

    public String getCategory_name() {
        return category_name;
    }
    public String getCategory_total_questions() {
        return category_total_questions;
    }
}
